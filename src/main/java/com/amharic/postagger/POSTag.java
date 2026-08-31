package com.amharic.postagger;

/**
 * POS tag set for Amharic.
 *
 * Tags follow broadly accepted Amharic NLP conventions:
 *   NN   - Noun
 *   NNP  - Proper Noun
 *   VB   - Verb (base / infinitive)
 *   VBP  - Verb (present / imperfect)
 *   VBD  - Verb (past / perfect)
 *   VBG  - Verb (gerund / verbal noun)
 *   JJ   - Adjective
 *   RB   - Adverb
 *   PRP  - Personal Pronoun
 *   PRP$ - Possessive Pronoun
 *   DT   - Determiner / Article
 *   IN   - Preposition / Postposition
 *   CC   - Coordinating Conjunction
 *   CD   - Cardinal Number
 *   UH   - Interjection
 *   PUNC - Punctuation
 *   NEG  - Negation particle
 *   AUX  - Auxiliary verb (copula)
 *   CONJ - Subordinating conjunction / complementizer
 *   UNKNOWN - Unrecognised token
 */
public enum POSTag {
    NN,
    NNP,
    VB,
    VBP,
    VBD,
    VBG,
    JJ,
    RB,
    PRP,
    PRP_POSS,   // PRP$
    DT,
    IN,
    CC,
    CD,
    UH,
    PUNC,
    NEG,
    AUX,
    CONJ,
    UNKNOWN;

    /** Human-readable label used in output. */
    public String label() {
        return switch (this) {
            case PRP_POSS -> "PRP$";
            default       -> name();
        };
    }
}
