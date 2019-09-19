package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Pheromones;

public class PrimalCockMod extends CockMod {

    public PrimalCockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity);
    }

    public Optional<String> getFluids() {
        return Optional.empty();
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, double damage) {
        return 0;
    }

    @Override
    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, boolean selfCame) {
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan,
        BodyPart part) {
        if (this.equals(primal)) {
            c.write(self,
                String.format("Raw sexual energy flows from %s %s into %s %s, enflaming %s lust",
                    self.nameOrPossessivePronoun(), part.describe(self),
                    opponent.nameOrPossessivePronoun(),
                    otherOrgan.describe(opponent), opponent.possessiveAdjective()));
            opponent.add(c, Pheromones
                .getWith(self, opponent, Global.random(3) + 1, 3, " primal passion"));

        }
    }

    @Override
    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target) {
    }

    public void onEndPenetration(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target) {
    }


    @Override
    public String describeAdjective(String partType) {
        return "primal musk";
    }
}
