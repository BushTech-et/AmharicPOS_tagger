package com.amharic.postagger;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Command-line entry point for the Amharic POS Tagger.
 *
 * Usage:
 * <pre>
 *   java -jar amharic-pos-tagger-exec.jar [OPTIONS]
 *
 * Options:
 *   --train &lt;file&gt;   Path to CoNLL-format training file.  When supplied the
 *                    HMM tagger is trained and used for decoding; otherwise
 *                    the rule-based tagger is used.
 *   --text  &lt;text&gt;   Amharic text to tag (if omitted, reads from stdin).
 *   --help           Print this help message.
 *
 * Output format:
 *   TOKEN/TAG  TOKEN/TAG  ...
 * </pre>
 */
public class Main {

    public static void main(String[] args) throws IOException {

        // Force stdout/stderr to use UTF-8 so Ethiopic characters render correctly
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        System.setOut(out);

        String trainFile  = null;
        String inputText  = null;

        // ── Argument parsing ───────────────────────────────────────────────
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--train" -> {
                    if (i + 1 >= args.length) die("--train requires a file path");
                    trainFile = args[++i];
                }
                case "--text" -> {
                    if (i + 1 >= args.length) die("--text requires a string argument");
                    inputText = args[++i];
                }
                case "--help", "-h" -> {
                    printHelp();
                    return;
                }
                default -> die("Unknown option: " + args[i]);
            }
        }

        // ── Build tagger ───────────────────────────────────────────────────
        AmharicTokenizer tokenizer = new AmharicTokenizer();

        if (trainFile != null) {
            // Supervised HMM path
            HMMTagger hmmTagger = new HMMTagger();
            try (Reader r = new FileReader(trainFile, StandardCharsets.UTF_8)) {
                TrainingDataLoader loader = new TrainingDataLoader();
                List<List<TaggedToken>> corpus = loader.load(r);
                if (corpus.isEmpty()) {
                    System.err.println("Warning: training file produced 0 sentences. " +
                            "Falling back to rule-based tagger.");
                } else {
                    hmmTagger.train(corpus);
                    System.err.println("Trained HMM on " + corpus.size() + " sentence(s).");
                }
            }
            tagAndPrint(hmmTagger, tokenizer, inputText);
        } else {
            // Unsupervised rule-based path
            RuleBasedTagger ruleTagger = new RuleBasedTagger();
            tagAndPrint(ruleTagger, tokenizer, inputText);
        }
    }

    // ── Tag a source (stdin or provided string) ────────────────────────────

    private static void tagAndPrint(HMMTagger tagger,
                                    AmharicTokenizer tokenizer,
                                    String inputText) {
        if (inputText != null) {
            tagSentence(tagger, tokenizer, inputText);
        } else {
            readFromStdin(s -> tagSentence(tagger, tokenizer, s));
        }
    }

    private static void tagAndPrint(RuleBasedTagger tagger,
                                    AmharicTokenizer tokenizer,
                                    String inputText) {
        if (inputText != null) {
            tagSentence(tagger, tokenizer, inputText);
        } else {
            readFromStdin(s -> tagSentence(tagger, tokenizer, s));
        }
    }

    // ── Per-sentence helpers ───────────────────────────────────────────────

    private static void tagSentence(HMMTagger tagger,
                                    AmharicTokenizer tokenizer,
                                    String text) {
        for (String sentence : splitSentences(text)) {
            List<String> tokens = tokenizer.tokenize(sentence);
            if (tokens.isEmpty()) continue;
            List<TaggedToken> tagged = tagger.tag(tokens);
            printTagged(tagged);
        }
    }

    private static void tagSentence(RuleBasedTagger tagger,
                                    AmharicTokenizer tokenizer,
                                    String text) {
        for (String sentence : splitSentences(text)) {
            List<String> tokens = tokenizer.tokenize(sentence);
            if (tokens.isEmpty()) continue;
            List<TaggedToken> tagged = tagger.tag(tokens);
            printTagged(tagged);
        }
    }

    private static void printTagged(List<TaggedToken> tagged) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tagged.size(); i++) {
            if (i > 0) sb.append("  ");
            sb.append(tagged.get(i));
        }
        System.out.println(sb);
    }

    // ── Split on Ethiopic full-stop or newline ─────────────────────────────

    private static String[] splitSentences(String text) {
        // Split on Ethiopic full stop (።, U+1362), half-stop (፡, U+1361), or newline
        return text.split("[\u1361\u1362\n\r]+");
    }

    // ── Stdin reader ───────────────────────────────────────────────────────

    @FunctionalInterface
    interface LineConsumer {
        void accept(String line);
    }

    private static void readFromStdin(LineConsumer consumer) {
        System.err.println("Reading from stdin (Ctrl+D to end):");
        try (Scanner sc = new Scanner(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.isBlank()) consumer.accept(line);
            }
        }
    }

    // ── Utility ────────────────────────────────────────────────────────────

    private static void die(String msg) {
        System.err.println("Error: " + msg);
        printHelp();
        System.exit(1);
    }

    private static void printHelp() {
        System.err.println("""
                Amharic POS Tagger
                ==================
                Usage: java -jar amharic-pos-tagger-exec.jar [OPTIONS]

                Options:
                  --train <file>   CoNLL-format training corpus (TOKEN  TAG per line,
                                   blank lines between sentences). Enables HMM tagging.
                  --text  <text>   Text to tag. If omitted, reads line-by-line from stdin.
                  --help           Show this message.

                Output:
                  TOKEN/TAG  TOKEN/TAG  ...   (one sentence per line)

                POS Tags:
                  NN=Noun  NNP=Proper  VB=Verb  VBP=Present  VBD=Past  VBG=Gerund
                  JJ=Adj   RB=Adverb   PRP=Pronoun  DT=Determiner  IN=Preposition
                  CC=CoordConj  CONJ=SubordConj  NEG=Negation  AUX=Auxiliary
                  CD=Number  UH=Interjection  PUNC=Punctuation  UNKNOWN=Unknown
                """);
    }
}
