# Amharic POS Tagger

A Part-of-Speech (POS) tagger for the Amharic language, implemented in Java.

## Architecture

```
src/main/java/com/amharic/postagger/
├── POSTag.java              – Amharic POS tag set (enum)
├── TaggedToken.java         – Token + tag pair value object
├── AmharicTokenizer.java    – Ethiopic script-aware tokenizer
├── AmharicLexicon.java      – Curated word → POS lexicon (300+ entries)
├── MorphologicalAnalyzer.java – Rule-based morphological analysis
├── RuleBasedTagger.java     – Combined lexicon + morphology tagger
├── HMMTagger.java           – Viterbi HMM tagger (trains on CoNLL data)
├── TrainingDataLoader.java  – CoNLL-format annotated corpus loader
└── Main.java                – CLI entry point
```

### Tag Set

| Tag     | Description                         |
|---------|-------------------------------------|
| NN      | Common Noun                         |
| NNP     | Proper Noun                         |
| VB      | Verb (base form)                    |
| VBP     | Verb – present / imperfect          |
| VBD     | Verb – past / perfect               |
| VBG     | Verb – gerund / verbal noun         |
| JJ      | Adjective                           |
| RB      | Adverb                              |
| PRP     | Personal Pronoun                    |
| PRP$    | Possessive Pronoun                  |
| DT      | Determiner / Article                |
| IN      | Preposition / Postposition          |
| CC      | Coordinating Conjunction            |
| CONJ    | Subordinating Conjunction           |
| NEG     | Negation particle                   |
| AUX     | Auxiliary verb / Copula             |
| CD      | Cardinal Number                     |
| UH      | Interjection                        |
| PUNC    | Punctuation                         |
| UNKNOWN | Unrecognised token                  |

## Building

Requires **Java 11+** and **Maven 3.6+**.

```bash
mvn package -q
```

This produces two JARs inside `target/`:
- `amharic-pos-tagger-1.0.0.jar` – library jar
- `amharic-pos-tagger-1.0.0-exec.jar` – executable fat-jar

## Running

### Rule-based tagger (no training required)

```bash
java -jar target/amharic-pos-tagger-1.0.0-exec.jar \
     --text "እኔ ኢትዮጵያ ዜጋ ነኝ"
```

Output:
```
እኔ/PRP  ኢትዮጵያ/NNP  ዜጋ/NN  ነኝ/AUX
```

### HMM tagger (train on annotated data)

Training data must be in **CoNLL format** – one `TOKEN<TAB>TAG` pair per line,
blank lines between sentences:

```
እኔ     PRP
ቤት     NN
ሄድኩ    VBD
።      PUNC

አማርኛ   NNP
ቋንቋ    NN
ነው     AUX
```

```bash
java -jar target/amharic-pos-tagger-1.0.0-exec.jar \
     --train src/main/resources/sample_corpus.conll \
     --text  "አማርኛ ቋንቋ ነው"
```

### Stdin mode (interactive / pipe)

```bash
echo "ልጁ ትምህርት ቤት ሄደ" | \
  java -jar target/amharic-pos-tagger-1.0.0-exec.jar
```

## Testing

```bash
mvn test
```

## Extending the Lexicon

Open `AmharicLexicon.java` and add entries in the `loadEntries()` method:

```java
tag("ቃሉ", POSTag.NN);   // the word
tag("ፃፈ", POSTag.VBD);  // he wrote
```

## Adding Training Data

Supply a CoNLL-formatted file with manually annotated Amharic text.
The larger the corpus, the better the HMM transition and emission
probabilities, and the higher the overall accuracy.  The seed corpus
at `src/main/resources/sample_corpus.conll` provides a small working
example.

## How It Works

1. **Tokenizer** splits Ethiopic text on whitespace and Ethiopic
   punctuation characters (U+1360–U+1368), keeping punctuation as
   separate tokens.

2. **Rule-based tagger** applies three layers in order:
   - Direct lexicon lookup
   - Stem generation (strip definiteness/plural/preposition affixes) + re-lookup
   - Morphological rules (prefix/suffix pattern matching for verb tense, negation, etc.)
   - Context rules (e.g. UNKNOWN after DT → NN)

3. **HMM tagger** reads a CoNLL corpus, estimates bigram transition and
   per-tag emission probabilities with Laplace smoothing, then runs
   Viterbi decoding.  Unknown words are resolved via the rule-based tagger.
