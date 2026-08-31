package com.amharic.postagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads annotated training data in CoNLL-style format.
 *
 * Expected format (one token per line, blank lines between sentences):
 * <pre>
 *   TOKEN   POS_TAG
 *   ...
 *
 *   TOKEN   POS_TAG
 *   ...
 * </pre>
 *
 * Columns are separated by one or more whitespace characters (tab or space).
 * Lines starting with '#' are treated as comments and ignored.
 * The POS tag in column 2 must match one of the {@link POSTag} label strings.
 * Unrecognised tags are stored as {@link POSTag#UNKNOWN}.
 */
public class TrainingDataLoader {

    /**
     * Reads the reader until EOF and returns a list of sentences, each
     * represented as a list of {@link TaggedToken}.
     *
     * @param reader character stream containing the annotated corpus
     * @return parsed corpus
     * @throws IOException if an I/O error occurs
     */
    public List<List<TaggedToken>> load(Reader reader) throws IOException {
        List<List<TaggedToken>> corpus = new ArrayList<>();
        List<TaggedToken> current = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.strip();

                // Comment lines
                if (line.startsWith("#")) continue;

                // Blank line → sentence boundary
                if (line.isEmpty()) {
                    if (!current.isEmpty()) {
                        corpus.add(new ArrayList<>(current));
                        current.clear();
                    }
                    continue;
                }

                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2) continue; // skip malformed lines

                String token   = parts[0];
                String tagStr  = parts[1].strip();
                POSTag posTag  = parseTag(tagStr);
                current.add(new TaggedToken(token, posTag));
            }
        }

        // Handle corpus that doesn't end with a blank line
        if (!current.isEmpty()) {
            corpus.add(current);
        }

        return corpus;
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private POSTag parseTag(String label) {
        for (POSTag t : POSTag.values()) {
            if (t.label().equalsIgnoreCase(label)) return t;
        }
        return POSTag.UNKNOWN;
    }
}
