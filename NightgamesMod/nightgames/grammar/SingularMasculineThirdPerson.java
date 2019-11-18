package nightgames.grammar;

public class SingularMasculineThirdPerson implements Person {

    @Override
    public String subjectPronoun() {
        return "he";
    }

    @Override
    public String objectPronoun() {
        return "him";
    }

    @Override
    public String possessivePronoun() {
        return "his";
    }

    @Override
    public String possessiveAdjective() {
        return "his";
    }

    @Override
    public String reflexivePronoun() {
        return "himself";
    }
}
