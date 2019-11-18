package nightgames.grammar;

public interface Person {
    interface Subject {
        String properNoun();
        String defaultNoun();
        String pronoun();
    }
    Subject subject();

    String objectPronoun();
    String possessivePronoun();
    String possessiveAdjective();
    String reflexivePronoun();
}
