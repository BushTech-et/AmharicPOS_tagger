package com.amharic.postagger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the rule-based POS tagger.
 */
class RuleBasedTaggerTest {

    private RuleBasedTagger tagger;
    private AmharicTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tagger    = new RuleBasedTagger();
        tokenizer = new AmharicTokenizer();
    }

    @Test
    @DisplayName("Tags a simple pronoun correctly")
    void testPronoun() {
        List<TaggedToken> result = tagger.tag(List.of("እኔ"));
        assertEquals(1, result.size());
        assertEquals(POSTag.PRP, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags an auxiliary verb correctly")
    void testAux() {
        List<TaggedToken> result = tagger.tag(List.of("ነው"));
        assertEquals(POSTag.AUX, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags a proper noun correctly")
    void testProperNoun() {
        List<TaggedToken> result = tagger.tag(List.of("ኢትዮጵያ"));
        assertEquals(POSTag.NNP, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags a common noun correctly")
    void testNoun() {
        List<TaggedToken> result = tagger.tag(List.of("ቤት"));
        assertEquals(POSTag.NN, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags an adjective correctly")
    void testAdjective() {
        List<TaggedToken> result = tagger.tag(List.of("ጥሩ"));
        assertEquals(POSTag.JJ, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags a past-tense verb (VBD) via morphology")
    void testPastVerbMorphology() {
        // ሄድኩ = I went (perfective 1st sing. → ends in ኩ)
        List<TaggedToken> result = tagger.tag(List.of("ሄድኩ"));
        assertEquals(POSTag.VBD, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags a gerund (VBG) via morphology – infinitive prefix መ")
    void testGerund() {
        List<TaggedToken> result = tagger.tag(List.of("መሄድ"));
        assertEquals(POSTag.VBG, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags negation prefix pattern correctly")
    void testNegation() {
        List<TaggedToken> result = tagger.tag(List.of("አይሄዱም"));
        assertEquals(POSTag.NEG, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags Ethiopic punctuation as PUNC")
    void testPunctuation() {
        // ። Ethiopic full stop
        List<TaggedToken> result = tagger.tag(List.of("።"));
        assertEquals(POSTag.PUNC, result.get(0).getTag());
    }

    @Test
    @DisplayName("Tags a cardinal number correctly")
    void testCardinalNumber() {
        List<TaggedToken> result = tagger.tag(List.of("ሁለት"));
        assertEquals(POSTag.CD, result.get(0).getTag());
    }

    @Test
    @DisplayName("Full sentence tagging preserves token count")
    void testFullSentence() {
        List<String> tokens = tokenizer.tokenize("እኔ ትምህርት ቤት ሄድኩ");
        List<TaggedToken> tagged = tagger.tag(tokens);
        assertEquals(tokens.size(), tagged.size());
    }

    @Test
    @DisplayName("Context rule: UNKNOWN after DT becomes NN")
    void testContextRuleDtFollowedByUnknown() {
        // ያ (DT) + an unknown word → the unknown should become NN
        List<TaggedToken> result = tagger.tag(List.of("ያ", "ዘሐቤ"));
        assertEquals(POSTag.DT, result.get(0).getTag());
        // Second token is unknown in lexicon, should be upgraded to NN by context rule
        assertEquals(POSTag.NN, result.get(1).getTag());
    }

    @Test
    @DisplayName("Returns non-null tag for every token")
    void testNoNullTags() {
        List<String> tokens = tokenizer.tokenize(
                "አዲስ አበባ የኢትዮጵያ ዋና ከተማ ናት");
        List<TaggedToken> tagged = tagger.tag(tokens);
        for (TaggedToken tt : tagged) {
            assertNotNull(tt.getTag());
        }
    }
}
