package nightgames.match.ftc;

import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Participant;

public class Prey extends Participant {
    public static int INITIAL_GRACE_PERIOD_ROUNDS = 3;

    public int gracePeriod = INITIAL_GRACE_PERIOD_ROUNDS;
    public int flagCounter = 0;

    public Prey(Character c) {
        super(c);
    }

    @Override
    public boolean canStartCombat(Participant p2) {
        return gracePeriod <= 0 && super.canStartCombat(p2);
    }

    @Override
    public void timePasses() {
        gracePeriod = Math.min(0, gracePeriod - 1);
        if (character.has(Item.Flag) && gracePeriod == 0 && (++flagCounter % 3) == 0) {
            incrementScore(1, "for holding the flag");
        }
    }

    public void grabFlag() {
        gracePeriod = INITIAL_GRACE_PERIOD_ROUNDS;
        flagCounter = 0;
        character.gain(Item.Flag);
    }

    @Override
    public int pointsForVictory(Participant loser) {
        return super.pointsForVictory(loser);
    }

    @Override
    protected int pointsGivenToVictor() {
        return 0;
    }
}
