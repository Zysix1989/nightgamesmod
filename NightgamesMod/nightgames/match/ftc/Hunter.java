package nightgames.match.ftc;

import nightgames.characters.Character;
import nightgames.match.Participant;

public class Hunter extends Participant {
    public Hunter(Character c) {
        super(c);
    }

    @Override
    protected int pointsGivenToVictor() {
        return 2;
    }
}
