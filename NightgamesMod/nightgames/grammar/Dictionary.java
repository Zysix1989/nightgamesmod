package nightgames.grammar;

public class Dictionary {
    private static class GenderedNoun implements Noun {
        private String masculine;
        private String feminine;
        private GenderedNoun(String masculine, String feminine) {
            this.masculine = masculine;
            this.feminine = feminine;
        }

        @Override
        public String masculine() {
            return masculine;
        }

        @Override
        public String feminine() {
            return feminine;
        }
    }

    public static Noun YOUTHFUL_GENDERED_NOUN = new GenderedNoun("boy", "girl");
    public static Noun INFORMAL_GENDERED_NOUN = new GenderedNoun("guy", "girl");
    public static Noun FORMAL_GENDERED_NOUN = new GenderedNoun("gentleman", "lady");
    public static Noun DEROGATORY_GENDERED_NOUN = new GenderedNoun("bastard", "bitch");
}
