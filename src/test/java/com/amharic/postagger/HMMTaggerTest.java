package com.amharic.postagger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the HMM tagger and training-data loader.
 */
class HMMTaggerTest {

    private static final String SAMPLE_CORPUS =
            "እኔ\tPRP\n"
          + "ኢትዮጵያ\tNNP\n"
          + "ዜጋ\tNN\n"
          + "ነኝ\tAUX\n"
          + "።\tPUNC\n"
          + "\n"
          + "አማርኛ\tNNP\n"
          + "ቋንቋ\tNN\n"
          + "ነው\tAUX\n"
          + "።\tPUNC\n"
          + "\n"
          + "ልጁ\tNN\n"
          + "ሄደ\tVBD\n"
          + "።\tPUNC\n";

    private HMMTagger tagger;
    private TrainingDataLoader loader;

    @BeforeEach
    void setUp() throws Exception {
        loader = new TrainingDataLoader();
        tagger = new HMMTagger();
        List<List<TaggedToken>> corpus = loader.load(new StringReader(SAMPLE_CORPUS));
        tagger.train(corpus);
    }

    @Test
    @DisplayName("Loader reads the correct number of sentences")
    void testLoaderSentenceCount() throws Exception {
        List<List<TaggedToken>> corpus =
                loader.load(new StringReader(SAMPLE_CORPUS));
        assertEquals(3, corpus.size());
    }

    @Test
    @DisplayName("Loader reads token/tag pairs correctly")
    void testLoaderTokenTag() throws Exception {
        List<List<TaggedToken>> corpus =
                loader.load(new StringReader(SAMPLE_CORPUS));
        TaggedToken first = corpus.get(0).get(0);
        assertEquals("እኔ",  first.getToken());
        assertEquals(POSTag.PRP, first.getTag());
    }

    @Test
    @DisplayName("HMM tag result has same token count as input")
    void testTagCount() {
        List<TaggedToken> result = tagger.tag(List.of("እኔ", "ነኝ", "።"));
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("HMM returns non-null tag for every token")
    void testNoNullTags() {
        List<TaggedToken> result = tagger.tag(List.of("አማርኛ", "ቋንቋ", "ነው"));
        for (TaggedToken tt : result) {
            assertNotNull(tt.getTag());
        }
    }

    @Test
    @DisplayName("Untrained HMM falls back to rule-based tagger gracefully")
    void testUntrainedFallback() {
        HMMTagger untrained = new HMMTagger();
        List<TaggedToken> result = untrained.tag(List.of("እኔ", "ቤት"));
        assertEquals(2, result.size());
        // Rule-based knows these, so should not be UNKNOWN
        assertEquals(POSTag.PRP, result.get(0).getTag());
        assertEquals(POSTag.NN,  result.get(1).getTag());
    }

    @Test
    @DisplayName("Loader handles corpus with comment lines")
    void testLoaderIgnoresComments() throws Exception {
        String corpus = "# comment\nእኔ\tPRP\nቤት\tNN\n";
        List<List<TaggedToken>> result = loader.load(new StringReader(corpus));
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).size());
    }

    @Test
    @DisplayName("TaggedToken.toString produces TOKEN/TAG format")
    void testTaggedTokenToString() {
        TaggedToken tt = new TaggedToken("እኔ", POSTag.PRP);
        assertEquals("እኔ/PRP", tt.toString());
    }
}
