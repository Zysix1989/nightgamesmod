package nightgames.match.ftc;

import nightgames.characters.Character;
import nightgames.match.Participant;

public class Prey extends Participant {
    public static int INITIAL_GRACE_PERIOD_ROUNDS = 3;

    public int gracePeriod;
    public int flagCounter;

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
        return gracePeriod <= 0 && super.canStartCombat(p2);
    }
}
