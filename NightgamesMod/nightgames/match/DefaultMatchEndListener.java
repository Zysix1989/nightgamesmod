package nightgames.match;

import java.util.Optional;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.CombatListener;
import nightgames.global.Global;

public class DefaultMatchEndListener extends CombatListener {

    
    public DefaultMatchEndListener(Combat c) {
        super(c);
    }

    @Override
    public void postEnd(Optional<Character> winner) {

        if (winner.isPresent() && winner.get() != Global.noneCharacter()) {
            Global.getMatch().invalidateTarget(winner.get(), c.getOpponent(winner.get()));
            //match.score(winner.get(), 1, Optional.of(" for defeating " + c.getOpponent(winner.get()).getName()));

        } else {
            Global.getMatch().invalidateTarget(c.p1, c.p2);
            Global.getMatch().invalidateTarget(c.p2, c.p1);
        }
    }
}
