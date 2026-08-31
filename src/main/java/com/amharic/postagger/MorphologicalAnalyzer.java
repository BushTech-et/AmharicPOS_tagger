package com.amharic.postagger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Morphological analyzer for Amharic.
 *
 * Amharic is a morphologically rich language with:
 *   - Prefixes indicating subject, tense, negation, prepositions.
 *   - Suffixes indicating object clitics, definiteness, gender, plural,
 *     possessive, and verbal derivation.
 *
 * This analyzer uses ordered rule lists to assign a POS tag based on the
 * surface form when lexicon lookup fails.  Rules are intentionally
 * conservative: only patterns that are highly diagnostic of a particular
 * part of speech are included.
 */
public class MorphologicalAnalyzer {

    // ── Helper: Ethiopic digit range ────────────────────────────────────────
    private static final Pattern ETHIOPIC_NUMERAL =
            Pattern.compile("[\u1369-\u137C]+");   // ፩–፼
    private static final Pattern ASCII_NUMERAL =
            Pattern.compile("[0-9]+([,./][0-9]+)*");
    // Ethiopic full-stop / word separator used as punctuation
    private static final Pattern ETHIOPIC_PUNCT =
            Pattern.compile("[\u1360-\u1368]");
    private static final Pattern ASCII_PUNCT =
            Pattern.compile("[\\.,!?;:\"'()\\[\\]{}<>\\-–—]+");

    /**
     * Attempts to infer the POS tag for {@code token} from its morphological
     * surface features.  Returns {@link POSTag#UNKNOWN} when no rule fires.
     */
    public POSTag analyze(String token) {
        if (token == null || token.isEmpty()) return POSTag.UNKNOWN;

        // ── Numerals ────────────────────────────────────────────────────────
        if (ETHIOPIC_NUMERAL.matcher(token).matches()) return POSTag.CD;
        if (ASCII_NUMERAL.matcher(token).matches())    return POSTag.CD;

        // ── Punctuation ─────────────────────────────────────────────────────
        if (ETHIOPIC_PUNCT.matcher(token).matches()) return POSTag.PUNC;
        if (ASCII_PUNCT.matcher(token).matches())    return POSTag.PUNC;

        // ── Negation prefix patterns ─────────────────────────────────────────
        // Verbal negation: "አይ-" prefix + "ም" suffix (e.g. አይሄዱም, አይሆንም)
        if (token.startsWith("አይ") && token.endsWith("ም")) return POSTag.NEG;
        // Perfective negation: "አለ-" prefix (e.g. አልሄደም, አልበሉም)
        if (token.startsWith("አል") && token.endsWith("ም")) return POSTag.NEG;
        // Copula negation: አይደለ-
        if (token.startsWith("አይደለ")) return POSTag.NEG;

        // ── Infinitive / gerund (verbal noun) suffix patterns ────────────────
        // Amharic infinitives start with "መ" or "ማ" and are gerunds/VBG
        if (startsWithInfinitivePrefix(token)) return POSTag.VBG;

        // ── Imperfect (present) tense: "ይ-" prefix, "-ዋ/-ኛ/-ኛ" or "-ል" suffix
        // 3rd masc. singular: ይ...ዋል, ይ...ኛል, ይ...ል  (e.g. ይሄዳል, ይሰራል)
        if (token.startsWith("ይ") && (token.endsWith("ዋል") || token.endsWith("ኛል")
                || token.endsWith("ኣል") || token.endsWith("ኸዋ")
                || token.endsWith("ዎል") || token.endsWith("አል")
                || token.endsWith("ዋለ") || endsWithImperfectSuffix(token))) {
            return POSTag.VBP;
        }
        // 2nd/3rd fem.: ት...ዋለ, ት...ኛ
        if (token.startsWith("ት") && (token.endsWith("ዋለ") || token.endsWith("ዋለች")
                || token.endsWith("ኛ") || token.endsWith("ሃ")
                || endsWithImperfectSuffix(token))) {
            return POSTag.VBP;
        }

        // ── Perfect (past) tense suffixes ───────────────────────────────────
        // Common perfective suffixes: ε (null-ending root), -ቻ, -ቸ, -ኩ, -ህ
        // Perfective 1st sing.: ends in ኩ (e.g. ሄድኩ)
        if (token.endsWith("ኩ"))   return POSTag.VBD;
        if (token.endsWith("ህ") && !token.startsWith("ነ") && !token.startsWith("ሆ"))
            return POSTag.VBD;  // 2nd sing. masc. perfective
        if (token.endsWith("ሽ") && !token.startsWith("ነ") && !token.startsWith("ሆ"))
            return POSTag.VBD;  // 2nd sing. fem. perfective
        if (token.endsWith("ን") && token.length() > 2)  return POSTag.VBD; // 1st plural

        // ── Possessive suffix: -ዬ, -ህ, -ሽ attached to noun ──────────────────
        // (These are also verbal, so only fire after verbal rules.)
        if (token.endsWith("ዬ")) return POSTag.PRP_POSS;   // e.g. ቤቴ (my house)

        // ── Definite article suffix "-ው" or "-ዋ" (masc./fem.) ─────────────
        // Tokens ending with the definite article -ው or -ዋ tend to be nouns.
        if (token.endsWith("ው") && token.length() > 2) return POSTag.NN;
        if (token.endsWith("ዋ") && token.length() > 2) return POSTag.NN;

        // ── Plural suffix "-ዎች" ─────────────────────────────────────────────
        if (token.endsWith("ዎች") || token.endsWith("ዎቸ")) return POSTag.NN;

        // ── Preposition/prefix clitic "ከ", "ለ", "በ" fused with noun ─────────
        // When a single Ethiopic character preposition is written separately it is
        // handled by the lexicon; here we handle the fused form such as ለሰው.
        if (token.length() > 1) {
            String first = token.substring(0, 1);
            if (first.equals("ለ") || first.equals("ከ") || first.equals("በ")) {
                return POSTag.IN; // preposition fused with its complement
            }
        }

        return POSTag.UNKNOWN;
    }

    // ── Private helpers ────────────────────────────────────────────────────

    /**
     * Ethiopic infinitive / gerund forms start with one of the Ethiopic
     * syllables that represent the consonant "m" (which is the infinitive
     * marker in Amharic): መ (U+1218), ማ (U+121B), ሚ (U+121A), etc.
     * We check the Unicode value of the first character.
     */
    private boolean startsWithInfinitivePrefix(String token) {
        if (token.isEmpty()) return false;
        int cp = token.codePointAt(0);
        // Ethiopic syllables for 'm': U+1218 (መ) through U+121F
        // and U+1218, U+121A, U+121B, U+121C, U+121D, U+121E, U+121F
        // plus the common ማ U+121B, ሚ U+121A, ሜ U+121E, etc.
        // Broadest check: cp in [0x1218, 0x121F]
        return cp >= 0x1218 && cp <= 0x121F;
    }

    /**
     * Returns true if the token ends with one of the common imperfect-tense
     * agreement suffixes in Amharic.
     */
    private boolean endsWithImperfectSuffix(String token) {
        return token.endsWith("ዋል")
                || token.endsWith("ኛል")
                || token.endsWith("ኣል")
                || token.endsWith("ኣሉ")
                || token.endsWith("ሉ");
    }

    /**
     * Returns the tokens obtained by stripping common prefixes and suffixes so
     * that the resulting stem can be looked up in the lexicon again.
     * This is a simple prefix/suffix stripping (not full morphological parsing).
     *
     * @param token original surface token
     * @return a candidate stem, or the original token unchanged
     */
    public List<String> generateStems(String token) {
        List<String> stems = new ArrayList<>();
        stems.add(token);

        // Strip definiteness suffixes "-ው", "-ዋ"
        if (token.endsWith("ው") && token.length() > 2)
            stems.add(token.substring(0, token.length() - 1));
        if (token.endsWith("ዋ") && token.length() > 2)
            stems.add(token.substring(0, token.length() - 1));
        // Strip plural
        if (token.endsWith("ዎች"))
            stems.add(token.substring(0, token.length() - 3));
        // Strip object clitic "-ን"
        if (token.endsWith("ን") && token.length() > 2)
            stems.add(token.substring(0, token.length() - 1));

        // Strip preposition prefixes ለ/ከ/በ
        if (token.length() > 1) {
            String first = token.substring(0, 1);
            if ("ለከበ".contains(first))
                stems.add(token.substring(1));
        }

        return stems;
    }
}
