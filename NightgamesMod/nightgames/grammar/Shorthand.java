package nightgames.grammar;

import nightgames.characters.Character;

public class Shorthand {
    public static String guyOrGirl(Character c) {
        return c.getGrammar().replaceWithNoun(Dictionary.INFORMAL_GENDERED_NOUN);
    }

    public static String boyOrGirl(Character c) {
        return c.getGrammar().replaceWithNoun(Dictionary.YOUTHFUL_GENDERED_NOUN);
    }

    public static String gentlemanOrLady(Character c) {
        return c.getGrammar().replaceWithNoun(Dictionary.FORMAL_GENDERED_NOUN);
    }

    public static String bitchOrBastard(Character c) {
        return c.getGrammar().replaceWithNoun(Dictionary.DEROGATORY_GENDERED_NOUN);
    }

}
