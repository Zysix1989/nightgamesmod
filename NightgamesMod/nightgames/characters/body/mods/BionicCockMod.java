package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.FluidAddiction;
import nightgames.status.Hypersensitive;
import nightgames.status.Winded;

public class BionicCockMod extends CockMod {
    public static final String TYPE = "bionic";

    public BionicCockMod() {
        super(TYPE, .8, 1.3, .5);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);
        String message = "";
        if (Global.random(5) == 0 && target.getType().equals("pussy")) {
            message += String.format(
                "%s %s out inside %s %s, pressing the metallic head of %s %s tightly against %s cervix. "
                    + "Then, a thin tube extends from %s uthera and into %s womb, pumping in a powerful aphrodisiac that soon has %s sensitive and"
                    + " gasping for more.",
                self.subject(), self.human() ? "bottom" : "bottoms",
                opponent.nameOrPossessivePronoun(),
                target.describe(opponent), self.possessiveAdjective(), part.describe(self),
                opponent.possessiveAdjective(), self.possessiveAdjective(),
                opponent.possessiveAdjective(),
                opponent.directObject());
            opponent.add(c, new Hypersensitive(opponent));
            // Instantly addict
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            bonus -= 3; // Didn't actually move around too much
        } else if (target.moddedPartCountsAs(FieryMod.INSTANCE)) {
            message += String.format(
                "Sensing the flesh around it, %s %s starts spinning rapidly, vastly increasing the friction against the walls of %s %s.",
                self.nameOrPossessivePronoun(), part.describe(self),
                opponent.nameOrPossessivePronoun(),
                target.describe(opponent));
            bonus += 5;
            if (Global.random(5) == 0) {
                message += String.format(
                    " The intense sensations cause %s to forget to breathe for a moment, leaving %s literally breathless.",
                    opponent.subject(), opponent.directObject());
                opponent.add(c, new Winded(opponent, 1));
            }
        }
        c.write(self, message);
        return bonus;
    }

    public Optional<String> getFluids() {
        return Optional.of("artificial lubricant");
    }

    @Override
    public String describeAdjective(String partType) {
        return "bionic implants";
    }
}
