package nightgames.match.ftc;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.match.MatchType;
import nightgames.match.Participant;

public class Prey extends Participant {
    public static int INITIAL_GRACE_PERIOD_ROUNDS = 3;

    public int gracePeriod;

    public Prey(Character c) {
        super(c);
    }

    public void decrementGracePeriod() {
        gracePeriod = Math.min(0, gracePeriod - 1);
    }

    public void resetGracePeriod() {
        gracePeriod = INITIAL_GRACE_PERIOD_ROUNDS;
    }

    @Override
    public boolean canStartCombat(Participant p2) {
        boolean ftc = true;
        if (Global.getMatch().getType() == MatchType.FTC) {
            FTCMatch match = (FTCMatch) Global.getMatch();
            ftc = !match.inGracePeriod();
        }
        return ftc && super.canStartCombat(p2);
    }
}
