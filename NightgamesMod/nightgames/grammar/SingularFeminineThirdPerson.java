package nightgames.grammar;

public class SingularFeminineThirdPerson implements PersonalGrammar {

    @Override
    public String subjectPronoun() {
        return "she";
    }

    @Override
    public String objectPronoun() {
        return "her";
    }

    @Override
    public String possessivePronoun() {
        return "hers";
    }

    @Override
    public String possessiveAdjective() {
        return "her";
    }

    @Override
    public String reflexivePronoun() {
        return "herself";
    }
}
