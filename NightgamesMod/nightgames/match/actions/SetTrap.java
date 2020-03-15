package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.match.Action;
import nightgames.match.Participant;
import nightgames.trap.Trap;

public class SetTrap extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath(Participant usedAction) {
            super(usedAction);
        }

        @Override
        public String describe(Character c) {
            return " start rigging up something weird, probably a trap.";
        }
    }

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            var result = trap.instantiate(user);
            user.getLocation().setTrap(result.instance);
            user.getCharacter().message(result.message);
            messageOthersInLocation(new Aftermath(user).describe());
        }
    }

    private Trap trap;

    public SetTrap(Trap trap) {
        super("Set(" + trap.toString() + ")");
        this.trap = trap;
    }

    @Override
    public boolean usable(Participant user) {
        return trap.recipe(user) && !user.getLocation().open()
                && trap.requirements(user)
                && !user.getCharacter().bound()
                && user.getLocation().getTrap().isEmpty();
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
