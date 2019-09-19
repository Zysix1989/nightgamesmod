package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.CockBound;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;

public class RunicCockMod extends CockMod {

    public RunicCockMod() {
        super("runic", 2.0, 1.0, 1.0);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);

        String message = "";
        if (target.moddedPartCountsAs(DemonicMod.INSTANCE)) {
            message += String.format(
                "The fae energies inside %s %s radiate outward and into %s, causing %s %s to grow much more sensitive. ",
                self.nameOrPossessivePronoun(), part.describe(self),
                opponent.nameOrPossessivePronoun(),
                opponent.possessiveAdjective(), target.describe(opponent));
            bonus += damage * 0.5; // +50% damage
        }
        if (Global.random(8) == 0 && !opponent.wary()) {
            message += String
                .format("Power radiates out from %s %s, seeping into %s and subverting %s will. ",
                    self.nameOrPossessivePronoun(), part.describe(self),
                    opponent.nameOrPossessivePronoun(),
                    opponent.directObject());
            opponent.add(c, new Enthralled(opponent, self, 3));
        }
        if (self.hasStatus(Stsflag.cockbound)) {
            String binding = ((CockBound) self.getStatus(Stsflag.cockbound)).binding;
            message += String.format(
                "With the merest of thoughts, %s %s out a pulse of energy from %s %s, freeing it from %s %s. ",
                self.subject(), self.human() ? "send" : "sends", self.possessiveAdjective(),
                part.describe(self), opponent.nameOrPossessivePronoun(), binding);
            self.removeStatus(Stsflag.cockbound);
        }
        c.write(self, message);
        return bonus;
    }

    @Override
    public String describeAdjective(String partType) {
        return "runic symbols";
    }

}