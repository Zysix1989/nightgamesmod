package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
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
        var model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        if (Global.random(5) == 0 && target.getType().equals("pussy")) {
            message += APPLY_BONUS_APHRODISIAC_TEMPLATE.render(model);
            opponent.add(c, new Hypersensitive(opponent));
            // Instantly addict
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
            bonus -= 3; // Didn't actually move around too much
        } else if (!target.moddedPartCountsAs(FieryMod.TYPE)) {
            message += APPLY_BONUS_TEMPLATE.render(model);
            bonus += 5;
            if (Global.random(5) == 0) {
                message += " " + APPLY_BONUS_WINDED_TEMPLATE.render(model);
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

    private static final JtwigTemplate APPLY_BONUS_APHRODISIAC_TEMPLATE = JtwigTemplate.inlineTemplate(
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

    private static final JtwigTemplate APPLY_BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Sensing the flesh around it, {{ self.nameOrPossessivePronoun }} "
            + "{{ part.describe(self) }} starts spinning rapidly, vastly increasing the "
            + "friction against the walls of {{ opponent.nameOrPossessivePronoun() }} "
            + "{{ target.describe(opponent) }}."
    );

    private static final JtwigTemplate APPLY_BONUS_WINDED_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The intense sensations cause {{ opponent.subject() }} to forget to breathe "
            + "for a moment, leaving {{ opponent.objectPronoun() }} literally breathless."
    );
}
