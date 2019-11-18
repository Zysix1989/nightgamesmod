package nightgames.grammar;

import nightgames.characters.Character;

public class SingularFeminineThirdPerson implements Person {
    private String name;

    private class Subject implements Person.Subject {
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
            return "she";
        }
    }

    private class Object implements Person.Object {
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
            return "her";
        }
    }

    public SingularFeminineThirdPerson(Character c) {
        name = c.getName();
    }

    @Override
    public Subject subject() {
        return new Subject();
    }

    @Override
    public Object object() { return new Object(); }

    @Override
    public String replaceWithNoun(Noun n) {
        return n.feminine();
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
