package nightgames.match.actions;

import nightgames.characters.Character;
import nightgames.match.Action;
import nightgames.match.Participant;

public class Wait extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " loitering nearby";
        }
    }

    public final class Instance extends Action.Instance {

        private Instance(Participant user) {
            super(user);
        }

        @Override
        public Action.Aftermath execute() {
            return executeOuter(user);
        }
    }

    public Wait() {
        super("Wait");
    }

    @Override
    public boolean usable(Participant user) {
        return true;
    }

    @Override
    public Instance newInstance(Participant user) {
        return new Instance(user);
    }

    @Override
    public Action.Aftermath executeOuter(Participant user) {
        return new Aftermath();
    }
}
