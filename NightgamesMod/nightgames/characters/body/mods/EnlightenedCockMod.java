package nightgames.characters.body.mods;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Abuff;

public class EnlightenedCockMod extends CockMod {
    public EnlightenedCockMod() {
        super("enlightened", 1.0, 1.2, .8);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);

        String message = "";
        if (target.moddedPartCountsAs(opponent, DemonicMod.INSTANCE)) {
            message = String.format(
                "Almost instinctively, %s %s entire being into %s %s. While this would normally be a good thing,"
                    + " whilst fucking a succubus it is very, very bad indeed.",
                self.subjectAction("focus", "focuses"), self.possessiveAdjective(),
                self.possessiveAdjective(), part.describe(self));
            c.write(self, message);
            // Actual bad effects are dealt with in PussyPart
        } else {
            message = String.format(
                "Drawing upon %s extensive training, %s %s will into %s %s, enhancing %s own abilities",
                self.possessiveAdjective(), self.subjectAction("concentrate", "concentrates"),
                self.possessiveAdjective(), self.possessiveAdjective(), part.describe(self),
                self.possessiveAdjective());
            c.write(self, message);
            for (int i = 0; i < Math.max(2, (self.get(Attribute.Ki) + 5) / 10); i++) { // +5
                // for
                // rounding:
                // 24->29->20,
                // 25->30->30
                Attribute attr = new Attribute[]{Attribute.Power, Attribute.Cunning,
                    Attribute.Seduction}[Global
                    .random(3)];
                self.add(c, new Abuff(self, attr, 1, 10));
            }
            self.buildMojo(c, 5);
            self.restoreWillpower(c, 1);
        }
        return bonus;
    }

    @Override
    public String describeAdjective(String partType) {
            return "imposing presence";
    }
}