package nightgames.match.ftc;

import nightgames.characters.Character;
import nightgames.match.Action;
import nightgames.match.Participant;
import nightgames.modifier.action.DescribablePredicate;

public class Hunter extends Participant {
    public Hunter(Character c, DescribablePredicate<Action.Instance> actionFilter) {
        super(c, actionFilter);
    }

    @Override
    protected int pointsGivenToVictor() {
        return 2;
    }
}
