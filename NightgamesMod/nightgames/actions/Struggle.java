package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.global.Global;
import nightgames.match.Participant;
import nightgames.status.Bound;
import nightgames.status.Stsflag;

public class Struggle extends Action {
    private static final long serialVersionUID = -644996487174479671L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {
            super(Movement.struggle);
        }
    }

    public Struggle() {
        super("Struggle");
    }

    @Override
    public boolean usable(Participant user) {
        return user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        Bound status = (Bound) user.getCharacter().getStatus(Stsflag.bound);
        int difficulty = 20 - user.getCharacter().getEscape(null, null);
        if (user.getCharacter().check(Attribute.Power, difficulty)) {
            if (user.getCharacter().human()) {
                if (status != null) {
                    Global.gui().message("You manage to break free from the " + status.getVariant() + ".");
                } else {
                    Global.gui().message("You manage to snap the restraints that are binding your hands.");
                }
            }
            user.getCharacter().free();
        } else {
            if (user.getCharacter().human()) {
                if (status != null) {
                    Global.gui().message("You struggle against the " + status.getVariant() + ", but can't get free.");
                } else {
                    Global.gui().message("You struggle against your restraints, but can't get free.");
                }
            }
            user.getCharacter().struggle();
        }
        return new Aftermath();
    }

}
