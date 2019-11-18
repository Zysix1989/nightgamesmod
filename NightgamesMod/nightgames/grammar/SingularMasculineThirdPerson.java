package nightgames.grammar;

import nightgames.characters.Character;

public class SingularMasculineThirdPerson implements Person {
    private String name;

    private class Subject implements Person.Subject{
        @Override
        public String properNoun() {
            return name;
        }

        @Override
        public String defaultNoun() {
            return properNoun();
        }

        @Override
        public String pronoun() {
            return "he";
        }
    }

    public SingularMasculineThirdPerson(Character c) {
        name = c.getName();
    }

    @Override
    public Subject subject() {
        return new Subject();
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
