package com.amharic.postagger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Tokenizer for Amharic text.
 *
 * Amharic is written in the Ethiopic (Ge'ez) script (Unicode block U+1200–U+137F).
 * Sentence boundaries are often marked with "፡" (word separator, U+1361) and
 * "።" (full stop, U+1362).  This tokenizer:
 *   1. Splits on whitespace and Ethiopic punctuation while keeping punctuation as
 *      separate tokens.
 *   2. Handles ASCII punctuation the same way.
 *   3. Preserves Ethiopic numerals and ASCII digits as single tokens.
 */
public class AmharicTokenizer {

    // Ethiopic word separator · and punctuation characters
    private static final String ETHIOPIC_PUNCT =
            "[\u1360-\u1368\\.,!?;:\"'()\\[\\]{}<>]";

    private static final Pattern SPLIT_PATTERN = Pattern.compile(
            // Keep Ethiopic punct as standalone tokens, split on whitespace
            "(?<=" + ETHIOPIC_PUNCT + ")|(?=" + ETHIOPIC_PUNCT + ")|\\s+"
    );

    /**
     * Tokenizes a raw Amharic string into a list of tokens.
     * Empty strings produced by splitting are discarded.
     *
     * @param text raw input text (may contain multiple words)
     * @return list of token strings
     */
    public List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String[] parts = SPLIT_PATTERN.split(text.strip());
        List<String> tokens = new ArrayList<>(parts.length);
        for (String part : parts) {
            String t = part.strip();
            if (!t.isEmpty()) {
                tokens.add(t);
            }
        }
        return tokens;
    }
}
