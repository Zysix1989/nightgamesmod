package nightgames.actions;

import nightgames.characters.Character;
import nightgames.match.Participant;

public class Wait extends Action {
    private static final long serialVersionUID = -644996487174479671L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return Movement.wait.describe(c);
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
