package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Pheromones;

public class PrimalCockMod extends CockMod {
    public static final String TYPE = "primal";
    public PrimalCockMod() {
        super(TYPE, 1.0, 1.4, 1.2);
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan,
        BodyPart part) {
        c.write(self,
            String.format("Raw sexual energy flows from %s %s into %s %s, enflaming %s lust",
                self.nameOrPossessivePronoun(), part.describe(self),
                opponent.nameOrPossessivePronoun(),
                otherOrgan.describe(opponent), opponent.possessiveAdjective()));
        opponent.add(c, Pheromones
            .getWith(self, opponent, Global.random(3) + 1, 3, " primal passion"));
    }

    @Override
    public String describeAdjective(String partType) {
        return "primal musk";
    }
}
