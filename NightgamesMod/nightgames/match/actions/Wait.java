package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.match.Action;
import nightgames.match.Participant;

public class Wait extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath(Participant usedAction) {
            super(usedAction);
        }

    }

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            messageOthersInLocation(user.getCharacter().getGrammar().subject().defaultNoun() + " loitering nearby");
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
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
