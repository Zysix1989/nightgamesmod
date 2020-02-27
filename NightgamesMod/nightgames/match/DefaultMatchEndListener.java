package nightgames.match;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.CombatListener;
import nightgames.global.Global;

import java.util.Optional;

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
            Global.getMatch().invalidateTarget(c.getP1Character(), c.getP2Character());
            Global.getMatch().invalidateTarget(c.getP2Character(), c.getP2Character());
        }
    }
}
