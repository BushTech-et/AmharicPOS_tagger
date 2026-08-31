package com.amharic.postagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hidden Markov Model (HMM) POS tagger using the Viterbi algorithm.
 *
 * The model is trained on annotated data supplied as lists of
 * {@link TaggedToken} sentences.  During decoding it uses:
 *   - Transition probabilities  P(tag_i | tag_{i-1})
 *   - Emission probabilities    P(word | tag)
 *   - Add-k (Laplace) smoothing to handle unseen events
 *
 * Unknown words are backed off to the {@link RuleBasedTagger} so that the
 * HMM benefits from morphological analysis even for words not seen in training.
 */
public class HMMTagger {

    // Smoothing constant (Laplace / add-k)
    private static final double SMOOTHING_K = 0.01;

    // Special start/end tag symbols used only inside this class
    private static final String START_TAG = "<START>";
    private static final String END_TAG   = "<END>";

    // Tag inventory derived from training data (plus START/END)
    private final List<String> tagVocab = new ArrayList<>();

    // transition[from][to]  – log-probability
    private double[][] transition;

    // emission[tag] – log-probability per word (sparse: stored as map)
    private final List<Map<String, Double>> emission = new ArrayList<>();

    // index → tag string (cleared and rebuilt on each train() call)
    private final Map<String, Integer> tagIndex = new HashMap<>();

    // Raw count tables used during training
    private Map<String, Map<String, Integer>> transitionCounts;
    private Map<String, Map<String, Integer>> emissionCounts;
    private Map<String, Integer> tagCounts;

    // Fallback tagger for unknown words
    private final RuleBasedTagger ruleFallback = new RuleBasedTagger();

    // Flag: true after train() has been called at least once
    private boolean trained = false;

    // ── Training ───────────────────────────────────────────────────────────

    /**
     * Trains the HMM on a corpus of tagged sentences.
     *
     * @param corpus list of sentences, each a list of {@link TaggedToken}
     */
    public void train(List<List<TaggedToken>> corpus) {
        transitionCounts = new HashMap<>();
        emissionCounts   = new HashMap<>();
        tagCounts        = new HashMap<>();

        for (List<TaggedToken> sentence : corpus) {
            String prevTag = START_TAG;
            for (TaggedToken tt : sentence) {
                String word = tt.getToken();
                String tag  = tt.getTag().label();

                // emission
                emissionCounts
                        .computeIfAbsent(tag, k -> new HashMap<>())
                        .merge(word, 1, Integer::sum);

                // transition
                transitionCounts
                        .computeIfAbsent(prevTag, k -> new HashMap<>())
                        .merge(tag, 1, Integer::sum);

                // tag frequency
                tagCounts.merge(tag, 1, Integer::sum);

                prevTag = tag;
            }
            // sentence end
            transitionCounts
                    .computeIfAbsent(prevTag, k -> new HashMap<>())
                    .merge(END_TAG, 1, Integer::sum);
        }

        buildVocabAndMatrices();
        trained = true;
    }

    // ── Decoding ───────────────────────────────────────────────────────────

    /**
     * Tags a pre-tokenized sentence using the Viterbi algorithm.
     * Falls back to {@link RuleBasedTagger} if the model has not been trained.
     *
     * @param tokens list of surface-form tokens
     * @return tagged token list in the same order
     */
    public List<TaggedToken> tag(List<String> tokens) {
        if (!trained) {
            // Graceful degradation: use rule-based tagger
            return ruleFallback.tag(tokens);
        }
        return viterbi(tokens);
    }

    // ── Viterbi implementation ─────────────────────────────────────────────

    private List<TaggedToken> viterbi(List<String> tokens) {
        int n      = tokens.size();
        int numTags = contentTagCount();   // excludes START and END

        // viterbi[t][j] = log-prob of the best path ending at position t with tag j
        double[][] viterbi  = new double[n][numTags];
        int[][]    backptr  = new int[n][numTags];

        for (double[] row : viterbi) Arrays.fill(row, Double.NEGATIVE_INFINITY);

        // ── Initialise (position 0) ─────────────────────────────────────────
        int startIdx = tagIndex.getOrDefault(START_TAG, -1);
        for (int j = 0; j < numTags; j++) {
            String tag = contentTag(j);
            double transProb = logTransition(startIdx, j + contentTagOffset());
            double emitProb  = logEmission(j + contentTagOffset(), tokens.get(0));
            viterbi[0][j] = transProb + emitProb;
            backptr[0][j] = -1;
        }

        // ── Recursion ───────────────────────────────────────────────────────
        for (int t = 1; t < n; t++) {
            for (int j = 0; j < numTags; j++) {
                int tagId = j + contentTagOffset();
                double emitProb = logEmission(tagId, tokens.get(t));
                double bestProb = Double.NEGATIVE_INFINITY;
                int    bestPrev = 0;
                for (int i = 0; i < numTags; i++) {
                    int prevTagId = i + contentTagOffset();
                    double prob = viterbi[t - 1][i]
                            + logTransition(prevTagId, tagId)
                            + emitProb;
                    if (prob > bestProb) {
                        bestProb = prob;
                        bestPrev = i;
                    }
                }
                viterbi[t][j] = bestProb;
                backptr[t][j] = bestPrev;
            }
        }

        // ── Termination: choose best last tag ──────────────────────────────
        int endIdx = tagIndex.getOrDefault(END_TAG, -1);
        double bestFinal = Double.NEGATIVE_INFINITY;
        int    bestLast  = 0;
        for (int i = 0; i < numTags; i++) {
            int prevTagId = i + contentTagOffset();
            double prob = viterbi[n - 1][i] + logTransition(prevTagId, endIdx);
            if (prob > bestFinal) {
                bestFinal = prob;
                bestLast  = i;
            }
        }

        // ── Back-trace ─────────────────────────────────────────────────────
        int[] bestPath = new int[n];
        bestPath[n - 1] = bestLast;
        for (int t = n - 2; t >= 0; t--) {
            bestPath[t] = backptr[t + 1][bestPath[t + 1]];
        }

        // ── Build result ───────────────────────────────────────────────────
        List<TaggedToken> result = new ArrayList<>(n);
        for (int t = 0; t < n; t++) {
            String tagStr = contentTag(bestPath[t]);
            POSTag posTag = labelToTag(tagStr, tokens.get(t));
            result.add(new TaggedToken(tokens.get(t), posTag));
        }
        return result;
    }

    // ── Matrix helpers ─────────────────────────────────────────────────────

    private void buildVocabAndMatrices() {
        // Tag vocabulary: START, content tags, END
        tagVocab.clear();
        tagIndex.clear();     // reset on every (re-)train
        tagVocab.add(START_TAG);
        for (String t : tagCounts.keySet()) {
            if (!t.equals(START_TAG) && !t.equals(END_TAG)) tagVocab.add(t);
        }
        tagVocab.add(END_TAG);

        for (int i = 0; i < tagVocab.size(); i++) {
            tagIndex.put(tagVocab.get(i), i);
        }

        int numTags = tagVocab.size();

        // Transition matrix (log-prob, add-k smoothed)
        transition = new double[numTags][numTags];
        for (int i = 0; i < numTags; i++) {
            String fromTag    = tagVocab.get(i);
            int fromTotal     = transitionCounts.getOrDefault(fromTag, Map.of())
                    .values().stream().mapToInt(Integer::intValue).sum();
            for (int j = 0; j < numTags; j++) {
                String toTag = tagVocab.get(j);
                int count    = transitionCounts
                        .getOrDefault(fromTag, Map.of())
                        .getOrDefault(toTag, 0);
                double prob = (count + SMOOTHING_K)
                        / (fromTotal + SMOOTHING_K * numTags);
                transition[i][j] = Math.log(prob);
            }
        }

        // Emission maps (per tag, sparse)
        // Compute total vocabulary size across all emission counts for smoothing
        long totalVocabSize = emissionCounts.values().stream()
                .flatMap(m -> m.keySet().stream())
                .distinct()
                .count();
        int vocabSize = (int) Math.max(totalVocabSize, 1);

        emission.clear();
        for (int i = 0; i < numTags; i++) {
            String tagStr = tagVocab.get(i);
            Map<String, Integer> wMap = emissionCounts.getOrDefault(tagStr, Map.of());
            int tagTotal = wMap.values().stream().mapToInt(Integer::intValue).sum();
            Map<String, Double> logEmit = new HashMap<>();
            for (Map.Entry<String, Integer> e : wMap.entrySet()) {
                double prob = (e.getValue() + SMOOTHING_K)
                        / (tagTotal + SMOOTHING_K * vocabSize);
                logEmit.put(e.getKey(), Math.log(prob));
            }
            // Store smoothed unknown-word probability
            double unkProb = SMOOTHING_K / (tagTotal + SMOOTHING_K * vocabSize);
            logEmit.put("<UNK>", Math.log(unkProb));
            emission.add(logEmit);
        }
    }

    private double logTransition(int fromTagId, int toTagId) {
        if (transition == null) return Math.log(SMOOTHING_K);
        return transition[fromTagId][toTagId];
    }

    private double logEmission(int tagId, String word) {
        if (emission.isEmpty()) return Math.log(SMOOTHING_K);
        Map<String, Double> eMap = emission.get(tagId);
        return eMap.getOrDefault(word, eMap.getOrDefault("<UNK>", Math.log(1e-10)));
    }

    /** Number of content (non-START/END) tags. */
    private int contentTagCount() {
        return tagVocab.size() - 2; // minus START and END
    }

    /** Offset of content tags within tagVocab (START is index 0). */
    private int contentTagOffset() {
        return 1; // START is at 0, content begins at 1
    }

    /** Returns the content-tag string at position {@code j} (0-based content index). */
    private String contentTag(int j) {
        return tagVocab.get(j + contentTagOffset());
    }

    /**
     * Converts a tag label string back to a {@link POSTag} enum constant.
     * For unknown or unmapped labels, falls back to the rule-based tagger.
     */
    private POSTag labelToTag(String label, String token) {
        for (POSTag t : POSTag.values()) {
            if (t.label().equals(label)) return t;
        }
        // Fall back to rule-based tagging for this token
        List<TaggedToken> fb = ruleFallback.tag(List.of(token));
        return fb.isEmpty() ? POSTag.UNKNOWN : fb.get(0).getTag();
    }
}
