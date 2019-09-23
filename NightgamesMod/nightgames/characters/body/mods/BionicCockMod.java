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
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

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
            var model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            var template = JtwigTemplate.inlineTemplate(
                "{{ self.subject() }} {{ self.action('bottom') }} out inside "
                    + "{{ opponent.nameOrPossessivePronoun() }} {{ target.describe(opponent) }}, "
                    + "pressing the metallic head of "
                    + "{{ self.possessiveAdjective() }} {{ part.describe(self) }} tightly against "
                    + "{{ opponent.possessiveAdjective() }} cervix. "
                    + "Then, a thin tube extends from {{ self.possessiveAdjective() }} urethra and "
                    + "into {{ opponent.possessiveAdjective() }} womb, pumping in a powerful "
                    + "aphrodisiac that soon has {{ opponent.objectPronoun() }} sensitive and"
                    + " gasping for more."
            );
            message += template.render(model);
            opponent.add(c, new Hypersensitive(opponent));
            // Instantly addict
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            bonus -= 3; // Didn't actually move around too much
        } else if (!target.moddedPartCountsAs(FieryMod.TYPE)) {
            var model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            var template = JtwigTemplate.inlineTemplate(
                "Sensing the flesh around it, {{ self.nameOrPossessivePronoun }} "
                    + "{{ part.describe(self) }} starts spinning rapidly, vastly increasing the "
                    + "friction against the walls of {{ opponent.nameOrPossessivePronoun() }} "
                    + "{{ target.describe(opponent) }}."
            );
            message += template.render(model);
            bonus += 5;
            if (Global.random(5) == 0) {
                var template2 = JtwigTemplate.inlineTemplate(
                    "The intense sensations cause {{ opponent.subject() }} to forget to breathe "
                        + "for a moment, leaving {{ opponent.objectPronoun() }} literally breathless."
                );
                message += " " + template2.render(model);
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
