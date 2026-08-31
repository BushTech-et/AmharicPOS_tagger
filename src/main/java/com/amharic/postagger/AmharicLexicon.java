package com.amharic.postagger;

import java.util.HashMap;
import java.util.Map;

/**
 * Amharic lexicon: maps known Amharic words to their most frequent POS tag.
 *
 * The lexicon is hand-crafted and covers a representative set of frequent
 * Amharic words.  In a production system this would be loaded from a file or
 * database; keeping it inline here makes the project self-contained.
 *
 * Entries are stored in lower-case (Ethiopic script is case-insensitive).
 * Lookup is O(1).
 */
public class AmharicLexicon {

    private final Map<String, POSTag> entries = new HashMap<>();

    public AmharicLexicon() {
        loadEntries();
    }

    /**
     * Returns the POS tag for the given token, or {@code null} if the token is
     * not in the lexicon.
     */
    public POSTag lookup(String token) {
        return entries.get(token);
    }

    /**
     * Returns {@code true} if the token is found in the lexicon.
     */
    public boolean contains(String token) {
        return entries.containsKey(token);
    }

    // -----------------------------------------------------------------------
    // Lexicon data
    // -----------------------------------------------------------------------

    private void loadEntries() {

        // ── Personal Pronouns ───────────────────────────────────────────────
        tag("እኔ",    POSTag.PRP);   // I (1st sing.)
        tag("አንተ",   POSTag.PRP);   // you (2nd sing. masc.)
        tag("አንቺ",   POSTag.PRP);   // you (2nd sing. fem.)
        tag("እሱ",    POSTag.PRP);   // he
        tag("እሷ",    POSTag.PRP);   // she
        tag("እኛ",    POSTag.PRP);   // we
        tag("እናንተ",  POSTag.PRP);   // you (2nd plural)
        tag("እነሱ",   POSTag.PRP);   // they
        tag("እነርሱ",  POSTag.PRP);   // they (formal)
        tag("እሱን",   POSTag.PRP);   // him (accusative)
        tag("እሷን",   POSTag.PRP);   // her (accusative)

        // ── Possessive Pronouns ─────────────────────────────────────────────
        tag("የኔ",    POSTag.PRP_POSS);   // mine
        tag("የአንተ",  POSTag.PRP_POSS);   // yours (masc.)
        tag("የእሱ",   POSTag.PRP_POSS);   // his
        tag("የእሷ",   POSTag.PRP_POSS);   // hers
        tag("የእኛ",   POSTag.PRP_POSS);   // ours
        tag("የእነሱ",  POSTag.PRP_POSS);   // theirs

        // ── Auxiliary Verbs / Copulas ───────────────────────────────────────
        tag("ነው",    POSTag.AUX);   // is (3rd sing. masc.)
        tag("ናት",    POSTag.AUX);   // is (3rd sing. fem.)
        tag("ናቸው",   POSTag.AUX);   // are (3rd plural)
        tag("ነኝ",    POSTag.AUX);   // am (1st sing.)
        tag("ነህ",    POSTag.AUX);   // are (2nd sing. masc.)
        tag("ነሽ",    POSTag.AUX);   // are (2nd sing. fem.)
        tag("ነን",    POSTag.AUX);   // are (1st plural)
        tag("ነበር",   POSTag.AUX);   // was/were (past)
        tag("ይሆናል",  POSTag.AUX);   // will be
        tag("ሆነ",    POSTag.AUX);   // became / was (past copula)
        tag("ሆናለሁ",  POSTag.AUX);   // I am (emphatic)
        tag("ሆናለህ",  POSTag.AUX);   // you are (masc.)
        tag("ሆናለሽ",  POSTag.AUX);   // you are (fem.)
        tag("ሆናለው",  POSTag.AUX);   // he is
        tag("ሆናለች",  POSTag.AUX);   // she is
        tag("ሆናለን",  POSTag.AUX);   // we are
        tag("ሆናላቸው", POSTag.AUX);   // they are

        // ── Common Verbs (base / infinitive form) ───────────────────────────
        tag("ሄደ",    POSTag.VBD);   // went
        tag("መጣ",    POSTag.VBD);   // came
        tag("አለ",    POSTag.VBD);   // said / there is
        tag("ሠራ",    POSTag.VBD);   // worked
        tag("ተናገረ",  POSTag.VBD);   // spoke
        tag("አወቀ",   POSTag.VBD);   // knew
        tag("ተማረ",   POSTag.VBD);   // learned
        tag("ዞረ",    POSTag.VBD);   // turned / wandered
        tag("ወሰደ",   POSTag.VBD);   // took
        tag("ሰጠ",    POSTag.VBD);   // gave
        tag("ደረሰ",   POSTag.VBD);   // arrived
        tag("ሄዳ",    POSTag.VBD);   // went (fem. subject)
        tag("ጠቀሰ",   POSTag.VBD);   // mentioned
        tag("አደረገ",  POSTag.VBD);   // did / made
        tag("ተፈፀመ",  POSTag.VBD);   // was accomplished
        tag("ተወ",    POSTag.VBD);   // left / abandoned
        tag("ሸጠ",    POSTag.VBD);   // sold
        tag("ገዛ",    POSTag.VBD);   // bought
        tag("አወጀ",   POSTag.VBD);   // announced
        tag("ፃፈ",    POSTag.VBD);   // wrote
        tag("አነበበ",  POSTag.VBD);   // read

        tag("ይሄዳል",  POSTag.VBP);   // goes
        tag("ይመጣል",  POSTag.VBP);   // comes
        tag("ይናገራል", POSTag.VBP);   // speaks
        tag("ይሰራል",  POSTag.VBP);   // works
        tag("ይሆናል",  POSTag.VBP);   // becomes / will be
        tag("ይጠቀማል", POSTag.VBP);   // uses
        tag("ይወዳል",  POSTag.VBP);   // loves
        tag("ይፈልጋል", POSTag.VBP);   // wants / needs
        tag("ትሄዳለች", POSTag.VBP);   // she goes
        tag("ትሄዳለህ", POSTag.VBP);   // you go (masc.)

        tag("መሄድ",   POSTag.VBG);   // going (infinitive / gerund)
        tag("መምጣት",  POSTag.VBG);   // coming
        tag("መናገር",  POSTag.VBG);   // speaking
        tag("መሥራት",  POSTag.VBG);   // working
        tag("ማወቅ",   POSTag.VBG);   // knowing
        tag("ማተም",   POSTag.VBG);   // printing
        tag("ማንበብ",  POSTag.VBG);   // reading
        tag("መፃፍ",   POSTag.VBG);   // writing

        // ── Common Nouns ────────────────────────────────────────────────────
        tag("ሰው",    POSTag.NN);    // person / people
        tag("ቤት",    POSTag.NN);    // house / home
        tag("ቋንቋ",   POSTag.NN);    // language
        tag("ጽሑፍ",   POSTag.NN);    // text / writing
        tag("ወንድ",   POSTag.NN);    // man / male
        tag("ሴት",    POSTag.NN);    // woman / female
        tag("ልጅ",    POSTag.NN);    // child / son
        tag("ልጆች",   POSTag.NN);    // children
        tag("ቀን",    POSTag.NN);    // day
        tag("ሌሊት",   POSTag.NN);    // night
        tag("ጊዜ",    POSTag.NN);    // time
        tag("ሥራ",    POSTag.NN);    // work / job
        tag("ቦታ",    POSTag.NN);    // place
        tag("ምግብ",   POSTag.NN);    // food
        tag("ውሃ",    POSTag.NN);    // water
        tag("ዋጋ",    POSTag.NN);    // price / value
        tag("ክፍል",   POSTag.NN);    // room / part
        tag("ሀገር",   POSTag.NN);    // country
        tag("ከተማ",   POSTag.NN);    // city
        tag("መንግስት",  POSTag.NN);    // government
        tag("ትምህርት",  POSTag.NN);    // education / lesson
        tag("ትምህርት ቤት", POSTag.NN); // school
        tag("ሕዝብ",   POSTag.NN);    // people / population
        tag("ዓለም",   POSTag.NN);    // world
        tag("ሕይወት",  POSTag.NN);    // life
        tag("ፍቅር",   POSTag.NN);    // love
        tag("ኃይል",   POSTag.NN);    // power / strength
        tag("ዕድል",   POSTag.NN);    // luck / opportunity
        tag("መብት",   POSTag.NN);    // right / permission
        tag("ዓይን",   POSTag.NN);    // eye
        tag("እጅ",    POSTag.NN);    // hand
        tag("እግር",   POSTag.NN);    // foot / leg
        tag("ራስ",    POSTag.NN);    // head / self
        tag("ቃል",    POSTag.NN);    // word / promise
        tag("ሃሳብ",   POSTag.NN);    // idea / thought
        tag("ልብ",    POSTag.NN);    // heart
        tag("ዘር",    POSTag.NN);    // seed / race / generation
        tag("ጌታ",    POSTag.NN);    // master / Lord
        tag("አምላክ",  POSTag.NN);    // God
        tag("ጥያቄ",   POSTag.NN);    // question
        tag("መልስ",   POSTag.NN);    // answer
        tag("ታሪክ",   POSTag.NN);    // history / story
        tag("ሳይንስ",  POSTag.NN);    // science
        tag("ቴክኖሎጂ", POSTag.NN);    // technology
        tag("ፖለቲካ",  POSTag.NN);    // politics
        tag("ኢኮኖሚ",  POSTag.NN);    // economy
        tag("ጤና",    POSTag.NN);    // health
        tag("ሃኪም",   POSTag.NN);    // doctor
        tag("ሆስፒታል", POSTag.NN);    // hospital
        tag("መኪና",   POSTag.NN);    // car
        tag("አውሮፕላን", POSTag.NN);   // airplane
        tag("ወንዝ",   POSTag.NN);    // river
        tag("ተራራ",   POSTag.NN);    // mountain
        tag("ዛፍ",    POSTag.NN);    // tree
        tag("አበባ",   POSTag.NN);    // flower
        tag("ፀሐይ",   POSTag.NN);    // sun
        tag("ጨረቃ",   POSTag.NN);    // moon
        tag("ኮከብ",   POSTag.NN);    // star
        tag("ዝናብ",   POSTag.NN);    // rain
        tag("ነፋስ",   POSTag.NN);    // wind
        tag("መጽሐፍ",  POSTag.NN);    // book
        tag("ደብዳቤ",  POSTag.NN);    // letter
        tag("ዜና",    POSTag.NN);    // news
        tag("ፊልም",   POSTag.NN);    // film / movie
        tag("ሙዚቃ",   POSTag.NN);    // music
        tag("ስፖርት",  POSTag.NN);    // sport
        tag("ጦርነት",  POSTag.NN);    // war
        tag("ሰላም",   POSTag.NN);    // peace
        tag("ፍርድ",   POSTag.NN);    // judgment
        tag("ፍርድ ቤት", POSTag.NN);   // court
        tag("ትዳር",   POSTag.NN);    // marriage
        tag("ቤተሰብ",  POSTag.NN);    // family
        tag("ጓደኛ",   POSTag.NN);    // friend
        tag("ዜጋ",    POSTag.NN);    // citizen
        tag("ምስል",   POSTag.NN);    // image / picture
        tag("ፍቺ",    POSTag.NN);    // definition / divorce
        tag("ዕድሜ",   POSTag.NN);    // age
        tag("ስም",    POSTag.NN);    // name
        tag("ቁጥር",   POSTag.NN);    // number
        tag("ቀለም",   POSTag.NN);    // color / ink
        tag("ሙያ",    POSTag.NN);    // profession
        tag("ደሀ",    POSTag.NN);    // poor person
        tag("ሀብት",   POSTag.NN);    // wealth
        tag("ወታደር",  POSTag.NN);    // soldier
        tag("ፖሊስ",   POSTag.NN);    // police
        tag("ፕሬዝዳንት", POSTag.NN);   // president
        tag("ሚኒስቴር",  POSTag.NN);   // ministry
        tag("ዩኒቨርሲቲ", POSTag.NN);   // university
        tag("ሆቴል",   POSTag.NN);    // hotel
        tag("ገበያ",   POSTag.NN);    // market
        tag("ሱቅ",    POSTag.NN);    // shop
        tag("ኩባንያ",  POSTag.NN);    // company

        // ── Proper Nouns ────────────────────────────────────────────────────
        tag("ኢትዮጵያ",   POSTag.NNP);  // Ethiopia
        tag("አዲስ አበባ",  POSTag.NNP);  // Addis Ababa
        tag("አፍሪካ",    POSTag.NNP);  // Africa
        tag("አሜሪካ",    POSTag.NNP);  // America
        tag("አውሮፓ",    POSTag.NNP);  // Europe
        tag("እስያ",     POSTag.NNP);  // Asia
        tag("ናይሮቢ",    POSTag.NNP);  // Nairobi
        tag("ለንደን",    POSTag.NNP);  // London
        tag("ዋሽንግተን",  POSTag.NNP);  // Washington
        tag("አማርኛ",    POSTag.NNP);  // Amharic (the language name)
        tag("ኦሮምኛ",    POSTag.NNP);  // Oromiffa
        tag("ትግርኛ",    POSTag.NNP);  // Tigrinya
        tag("ኒል",      POSTag.NNP);  // Nile
        tag("አባይ",     POSTag.NNP);  // Abbay (Blue Nile)

        // ── Adjectives ─────────────────────────────────────────────────────
        tag("ጥሩ",    POSTag.JJ);    // good
        tag("መጥፎ",   POSTag.JJ);    // bad
        tag("ትልቅ",   POSTag.JJ);    // big / great
        tag("ትንሽ",   POSTag.JJ);    // small / little  (adverbial use handled by context)
        tag("አዲስ",   POSTag.JJ);    // new
        tag("ያረጀ",   POSTag.JJ);    // old (thing)
        tag("ፈጣን",   POSTag.JJ);    // fast
        tag("ቀጭን",   POSTag.JJ);    // thin / slim
        tag("ወፍራም",  POSTag.JJ);    // fat / thick
        tag("ረጅም",   POSTag.JJ);    // tall / long
        tag("አጭር",   POSTag.JJ);    // short
        tag("ደስተኛ",  POSTag.JJ);    // happy
        tag("ሀዘኛ",   POSTag.JJ);    // sad
        tag("ደስ",    POSTag.JJ);    // pleasant (used predicatively)
        tag("ቆንጆ",   POSTag.JJ);    // beautiful / nice
        tag("ጠቃሚ",   POSTag.JJ);    // useful / important
        tag("አስፈላጊ",  POSTag.JJ);   // necessary
        tag("ክፉ",    POSTag.JJ);    // evil / bad
        tag("ብዙ",    POSTag.JJ);    // many / much
        tag("ጥቂት",   POSTag.JJ);    // few / a little
        tag("ሌላ",    POSTag.JJ);    // other / another
        tag("ሁሉ",    POSTag.JJ);    // all / every
        tag("ሁሉም",   POSTag.JJ);    // all (pronominally)
        tag("ብቸኛ",   POSTag.JJ);    // only / sole
        tag("ዋና",    POSTag.JJ);    // main / chief
        tag("ጥቁር",   POSTag.JJ);    // black / dark
        tag("ነጭ",    POSTag.JJ);    // white
        tag("ቀይ",    POSTag.JJ);    // red
        tag("አረንጓዴ",  POSTag.JJ);   // green
        tag("ሰማያዊ",  POSTag.JJ);    // blue
        tag("ቢጫ",    POSTag.JJ);    // yellow
        tag("ሙሉ",    POSTag.JJ);    // full / complete
        tag("ባዶ",    POSTag.JJ);    // empty
        tag("ሩቅ",    POSTag.JJ);    // far
        tag("ቅርብ",   POSTag.JJ);    // near

        // ── Adverbs ─────────────────────────────────────────────────────────
        tag("አሁን",   POSTag.RB);    // now
        tag("ዛሬ",    POSTag.RB);    // today
        tag("ትናንት",  POSTag.RB);    // yesterday
        tag("ነገ",    POSTag.RB);    // tomorrow
        tag("ብዙ ጊዜ", POSTag.RB);   // often
        tag("አንዳንዴ", POSTag.RB);   // sometimes
        tag("ሁልጊዜ",  POSTag.RB);   // always
        tag("ጭራሽ",  POSTag.RB);    // never / at all
        tag("ፈጥኖ",   POSTag.RB);   // quickly
        tag("ቀስ",    POSTag.RB);    // slowly / gently
        tag("እዚህ",   POSTag.RB);    // here
        tag("እዚያ",   POSTag.RB);    // there
        tag("ደግሞ",   POSTag.RB);    // also / again
        tag("ብቻ",    POSTag.RB);    // only / just
        tag("እጅግ",   POSTag.RB);    // very / extremely
        tag("በጣም",   POSTag.RB);    // very / a lot
        // Note: ትንሽ entered once as JJ above; adverbial "a little" disambiguated by context
        tag("ከዚያ",   POSTag.RB);    // then / from there
        tag("ስለዚህ",  POSTag.RB);    // therefore (adverb; as subordinating conj it becomes CONJ)
        tag("ስለዚህም", POSTag.RB);    // therefore (emphatic)
        tag("ሆኖ",    POSTag.RB);    // however / yet
        tag("ሆኖም",   POSTag.RB);    // however (emphatic)

        // ── Prepositions / Postpositions ─────────────────────────────────────
        tag("ለ",     POSTag.IN);    // for / to
        tag("ከ",     POSTag.IN);    // from / with
        tag("በ",     POSTag.IN);    // in / at / by / with
        tag("ወደ",    POSTag.IN);    // to / towards
        tag("እስከ",   POSTag.IN);    // until / as far as
        tag("ላይ",    POSTag.IN);    // on / at (postpositional)
        tag("ስር",    POSTag.IN);    // under / below
        tag("ፊት",    POSTag.IN);    // before / in front of
        tag("ኋላ",    POSTag.IN);    // after / behind
        tag("ዘንድ",   POSTag.IN);    // near / with (formal); as subordinating conj only in compound constructions
        tag("ምክንያት", POSTag.IN);    // because of
        tag("ውስጥ",   POSTag.IN);    // inside / within
        tag("ውጭ",    POSTag.IN);    // outside

        // ── Determiners ─────────────────────────────────────────────────────
        tag("ያ",     POSTag.DT);    // that (demonstrative)
        tag("ይህ",    POSTag.DT);    // this (masc.)
        tag("ይህች",   POSTag.DT);    // this (fem.)
        tag("እነዚህ",  POSTag.DT);   // these
        tag("እነዚያ",  POSTag.DT);   // those
        // Note: "አንድ" is entered as CD (cardinal number); used as indefinite article
        // by context, not by a separate DT entry, to avoid conflicting tags.
        tag("አንዲት",  POSTag.DT);   // a / one (fem. indefinite article)
        tag("ሌሎቹ",   POSTag.DT);    // the others

        // ── Coordinating Conjunctions ────────────────────────────────────────
        tag("እና",    POSTag.CC);    // and
        tag("ወይ",    POSTag.CC);    // or
        tag("ወይም",   POSTag.CC);    // or (formal)
        tag("ነገር ግን", POSTag.CC);   // but (formal)
        tag("ግን",    POSTag.CC);    // but
        tag("ነዋ",    POSTag.CC);    // for (causal/coordinating)

        // ── Subordinating Conjunctions / Complementizers ──────────────────────
        tag("ምክንያቱም", POSTag.CONJ); // because
        // Note: "ስለዚህ" is primarily RB (therefore); conjunction use is context-dependent
        tag("ከዚህ",   POSTag.CONJ); // from this (conjunctive)
        tag("ሲሆን",   POSTag.CONJ); // when / while
        tag("ስለ",    POSTag.CONJ); // about / because of
        tag("ምንም ቢሆን", POSTag.CONJ); // although / even if
        tag("ምንም",   POSTag.CONJ); // even though / whatever
        tag("ቢሆን",   POSTag.CONJ); // even if / whether
        tag("ካልሆነ",  POSTag.CONJ); // unless
        tag("ከሆነ",   POSTag.CONJ); // if (conditional)
        tag("እስከሆነ", POSTag.CONJ); // as long as
        // Note: "ዘንድ" is entered as IN (near/with); subordinating use handled by context

        // ── Negation ────────────────────────────────────────────────────────
        tag("አይ",    POSTag.NEG);   // no / not (verbal negation prefix stem)
        tag("አይደለም", POSTag.NEG);   // is not / no (copula negation)
        tag("አልነበረም", POSTag.NEG);  // was not
        tag("አይሄዱም", POSTag.NEG);   // they don't go
        tag("አልሄደም", POSTag.NEG);   // did not go
        tag("አልበሉም", POSTag.NEG);   // did not eat
        tag("አይደለህም", POSTag.NEG);  // you are not
        tag("አይደለችም", POSTag.NEG);  // she is not
        tag("አይሆንም", POSTag.NEG);   // will not be / is not possible

        // ── Cardinal Numbers ─────────────────────────────────────────────────
        tag("አንድ",   POSTag.CD);    // one
        tag("ሁለት",   POSTag.CD);    // two
        tag("ሶስት",   POSTag.CD);    // three
        tag("አራት",   POSTag.CD);    // four
        tag("አምስት",  POSTag.CD);    // five
        tag("ስድስት",  POSTag.CD);    // six
        tag("ሰባት",   POSTag.CD);    // seven
        tag("ስምንት",  POSTag.CD);    // eight
        tag("ዘጠኝ",   POSTag.CD);    // nine
        tag("አስር",   POSTag.CD);    // ten
        tag("ሃያ",    POSTag.CD);    // twenty
        tag("ሰላሳ",   POSTag.CD);    // thirty
        tag("አርባ",   POSTag.CD);    // forty
        tag("ሃምሳ",   POSTag.CD);    // fifty
        tag("ሰልሳ",   POSTag.CD);    // sixty
        tag("ሰባ",    POSTag.CD);    // seventy
        tag("ሰማንያ",  POSTag.CD);    // eighty
        tag("ዘጠና",   POSTag.CD);    // ninety
        tag("መቶ",    POSTag.CD);    // hundred
        tag("ሺ",     POSTag.CD);    // thousand
        tag("ሚሊዮን",  POSTag.CD);    // million

        // ── Interjections ────────────────────────────────────────────────────
        tag("አዎ",    POSTag.UH);    // yes
        tag("አዎን",   POSTag.UH);    // yes (emphatic)
        // Note: "አይ" is entered as NEG; its interjection use is context-dependent
        // Note: "ወይ" is entered as CC (or); exclamatory "ወይ!" is disambiguated by context
        tag("ወዮ",    POSTag.UH);    // alas / oh no
        tag("ኧረ",    POSTag.UH);    // hey / come on (exclamative)
        tag("ሃ",     POSTag.UH);    // ah / ha
        tag("ኤ",     POSTag.UH);    // hey (calling attention)
    }

    private void tag(String word, POSTag pos) {
        entries.put(word, pos);
    }
}
