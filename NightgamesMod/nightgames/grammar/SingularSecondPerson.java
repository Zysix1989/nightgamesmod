package nightgames.grammar;

import nightgames.characters.Character;

public class SingularSecondPerson implements Person {
    private String name;

    private class Subject implements Person.Subject {
        @Override
        public String properNoun() {
            return name;
        }

        @Override
        public String defaultNoun() {
            return pronoun();
        }

        @Override
        public String pronoun() {
            return "you";
        }
    }

    private class Object implements Person.Object {
        @Override
        public String properNoun() {
            return name;
        }

        @Override
        public String defaultNoun() {
            return pronoun();
        }

        @Override
        public String pronoun() {
            return "you";
        }
    }

    public SingularSecondPerson(Character c) {
        name = c.getName();
    }

    @Override
    public Subject subject() {
        return new Subject();
    }

    @Override
    public Object object() {
        return new Object();
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
