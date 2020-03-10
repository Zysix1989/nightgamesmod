package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;

public class MasturbateAction extends Action {
    private static final long serialVersionUID = 3479886040422510833L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            String mast;
            if (c.hasDick()) {
                mast = String.format(" starts to stroke %s cock ", c.possessiveAdjective());
            } else if (c.hasPussy()) {
                mast = String.format(" starts to stroke %s pussy ", c.possessiveAdjective());
            } else {
                mast = String.format(" starts to finger %s ass ", c.possessiveAdjective());
            }
            return mast + "while trying not to make much noise. It's quite a show.";
        }
    }

    public MasturbateAction() {
        super("Masturbate");
    }

    @Override
    public boolean usable(Participant user) {
        return user.getCharacter().getArousal().get() >= 15 && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (user.getCharacter().hasDick()) {
            user.getCharacter().message("You desperately need to deal with your erection before you run into " +
                    "an opponent. You find an isolated corner and quickly jerk off.");
            if (Global.checkFlag(Flag.masturbationSemen)) {
                if (user.getCharacter().getArousal().percent() > 50) {
                    user.getCharacter().message("You remember that Reyka asked you to bring back some semen for " +
                            "her transformation rituals, and you catch your semen with one of her magic bottles.");
                    user.getCharacter().gain(Item.semen);
                } else {
                    user.getCharacter().message("You remember that Reyka asked you to bring back some semen for " +
                            "her transformation rituals, and you catch your semen with one of her magic bottles. " +
                            "However it seems like you aren't quite aroused enough to provide the thick cum " +
                            "that she needs as the bottles seem to vomit back the cum you put in it.");
                }
            }
        } else if (user.getCharacter().hasPussy()) {
            user.getCharacter().message(
                    "You desperately need to deal with your throbbing pussy before you run into an opponent. You find an isolated corner and quickly finger yourself to a quick orgasm.");
        } else {
            user.getCharacter().message(
                    "You desperately need to deal with your throbbing body before you run into an opponent. You find an isolated corner and quickly finger your ass to a quick orgasm.");
        }
        user.state = new Participant.MasturbatingState();
        user.waitRounds(1);
        return new Aftermath();
    }

}
