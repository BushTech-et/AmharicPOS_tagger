package com.amharic.postagger;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule-based POS tagger for Amharic.
 *
 * Strategy (applied in order for each token):
 *   1. Direct lexicon lookup (fastest, most precise).
 *   2. Stem generation + lexicon lookup (handles inflected forms).
 *   3. Morphological rule engine (suffix/prefix pattern matching).
 *   4. Contextual fall-back rules (use surrounding tags to disambiguate).
 *   5. Default to {@link POSTag#UNKNOWN}.
 *
 * This class is the primary API for single-pass tagging without training data.
 * For supervised tagging with training data, use {@link HMMTagger} instead.
 */
public class RuleBasedTagger {

    private final AmharicLexicon lexicon;
    private final MorphologicalAnalyzer morphAnalyzer;

    public RuleBasedTagger() {
        this.lexicon      = new AmharicLexicon();
        this.morphAnalyzer = new MorphologicalAnalyzer();
    }

    /**
     * Tags a pre-tokenized sentence.
     *
     * @param tokens list of tokens (surface forms)
     * @return list of {@link TaggedToken} in the same order
     */
    public List<TaggedToken> tag(List<String> tokens) {
        // Pass 1: assign a preliminary tag to each token independently
        POSTag[] tags = new POSTag[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            tags[i] = tagSingle(tokens.get(i));
        }

        // Pass 2: contextual corrections (simple bigram / trigram rules)
        applyContextRules(tokens, tags);

        // Build result
        List<TaggedToken> result = new ArrayList<>(tokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            result.add(new TaggedToken(tokens.get(i), tags[i]));
        }
        return result;
    }

    // ── Single-token tagging ───────────────────────────────────────────────

    private POSTag tagSingle(String token) {
        // 1. Direct lookup
        POSTag tag = lexicon.lookup(token);
        if (tag != null) return tag;

        // 2. Stem variants + lookup
        for (String stem : morphAnalyzer.generateStems(token)) {
            if (!stem.equals(token)) {
                tag = lexicon.lookup(stem);
                if (tag != null) return tag;
            }
        }

        // 3. Morphological rules
        tag = morphAnalyzer.analyze(token);
        return tag;
    }

    // ── Contextual post-processing ─────────────────────────────────────────

    /**
     * Applies a small set of context-sensitive correction rules over the
     * preliminary tag sequence.
     */
    private void applyContextRules(List<String> tokens, POSTag[] tags) {
        int n = tags.length;
        for (int i = 0; i < n; i++) {

            // Rule: after a determiner (DT), an UNKNOWN token is likely a noun
            if (i > 0 && tags[i - 1] == POSTag.DT && tags[i] == POSTag.UNKNOWN) {
                tags[i] = POSTag.NN;
            }

            // Rule: after a preposition (IN), an UNKNOWN token is likely a noun
            if (i > 0 && tags[i - 1] == POSTag.IN && tags[i] == POSTag.UNKNOWN) {
                tags[i] = POSTag.NN;
            }

            // Rule: after a possessive pronoun, UNKNOWN → NN
            if (i > 0 && tags[i - 1] == POSTag.PRP_POSS && tags[i] == POSTag.UNKNOWN) {
                tags[i] = POSTag.NN;
            }

            // Rule: UNKNOWN between two NOUNs is likely a preposition or CC
            if (i > 0 && i < n - 1
                    && isNounLike(tags[i - 1]) && isNounLike(tags[i + 1])
                    && tags[i] == POSTag.UNKNOWN) {
                tags[i] = POSTag.IN;
            }

            // Rule: UNKNOWN after auxiliary verb → likely a main verb form
            if (i > 0 && tags[i - 1] == POSTag.AUX && tags[i] == POSTag.UNKNOWN) {
                tags[i] = POSTag.VBP;
            }

            // Rule: token is Ethiopic punctuation character → PUNC
            if (isEthiopicPunct(tokens.get(i))) {
                tags[i] = POSTag.PUNC;
            }
        }
    }

    private boolean isNounLike(POSTag t) {
        return t == POSTag.NN || t == POSTag.NNP || t == POSTag.PRP;
    }

    private boolean isEthiopicPunct(String token) {
        for (int cp : (Iterable<Integer>) () -> token.codePoints().iterator()) {
            if (cp >= 0x1360 && cp <= 0x1368) return true;
        }
        return false;
    }
}
