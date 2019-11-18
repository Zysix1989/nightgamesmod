package nightgames.grammar;

public class SingularSecondPerson implements Person {

    @Override
    public String subjectPronoun() {
        return "you";
    }

    @Override
    public String objectPronoun() {
        return "you";
    }

    @Override
    public String possessivePronoun() {
        return "yours";
    }

    @Override
    public String possessiveAdjective() {
        return "your";
    }

    @Override
    public String reflexivePronoun() {
        return "yourself";
    }
}
