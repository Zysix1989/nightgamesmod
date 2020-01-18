package nightgames.grammar;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public class Engine {
    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory factory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    public static NLGFactory getFactory() {
        return factory;
    }

    public static Realiser getRealiser() {
        return realiser;
    }
}
