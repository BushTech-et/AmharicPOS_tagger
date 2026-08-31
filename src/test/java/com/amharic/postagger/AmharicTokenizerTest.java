package com.amharic.postagger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Amharic tokenizer.
 */
class AmharicTokenizerTest {

    private AmharicTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new AmharicTokenizer();
    }

    @Test
    @DisplayName("Tokenizes simple whitespace-separated Amharic words")
    void testBasicTokenization() {
        List<String> tokens = tokenizer.tokenize("እኔ ቤት ሄድኩ");
        assertEquals(3, tokens.size());
        assertEquals("እኔ", tokens.get(0));
        assertEquals("ቤት", tokens.get(1));
        assertEquals("ሄድኩ", tokens.get(2));
    }

    @Test
    @DisplayName("Handles null and blank input gracefully")
    void testNullAndBlank() {
        assertTrue(tokenizer.tokenize(null).isEmpty());
        assertTrue(tokenizer.tokenize("").isEmpty());
        assertTrue(tokenizer.tokenize("   ").isEmpty());
    }

    @Test
    @DisplayName("Splits Ethiopic punctuation into separate tokens")
    void testEthiopicPunctuation() {
        // ። is Ethiopic full stop (U+1362)
        List<String> tokens = tokenizer.tokenize("ጥሩ ሰው ነው።");
        // Should separate "ነው" and "።"
        assertTrue(tokens.size() >= 3);
        assertTrue(tokens.contains("።"));
    }

    @Test
    @DisplayName("Handles mixed script text")
    void testMixedScript() {
        List<String> tokens = tokenizer.tokenize("POS tagging ለ NLP");
        assertTrue(tokens.size() >= 3);
    }

    @Test
    @DisplayName("Does not produce empty tokens")
    void testNoEmptyTokens() {
        List<String> tokens = tokenizer.tokenize("  አማርኛ   ቋንቋ  ");
        for (String t : tokens) {
            assertFalse(t.isBlank(), "Token must not be blank");
        }
    }
}
