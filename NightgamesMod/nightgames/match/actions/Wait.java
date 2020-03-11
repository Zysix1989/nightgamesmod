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

    public Wait() {
        super("Wait");
    }

    @Override
    public boolean usable(Participant user) {
        return true;
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        return new Aftermath();
    }
}
