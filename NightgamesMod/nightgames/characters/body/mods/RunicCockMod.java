package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.CockBound;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class RunicCockMod extends CockMod {
    public static final String TYPE = "runic";

    public RunicCockMod() {
        super(TYPE, 2.0, 1.0, 1.0);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);

        String message = "";
        var model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        if (target.moddedPartCountsAs(DemonicMod.TYPE)) {
            message += APPLY_BONUS_DEMONIC_TEMPLATE.render(model);
            bonus += damage * 0.5; // +50% damage
        }
        if (Global.random(8) == 0 && !opponent.wary()) {
            message += APPLY_BONUS_ENTHRALLED_TEMPLATE.render(model);
            opponent.add(c, new Enthralled(opponent, self, 3));
        }
        if (self.hasStatus(Stsflag.cockbound)) {
            String binding = ((CockBound) self.getStatus(Stsflag.cockbound)).binding;
            model = model.with("binding", binding);
            message += APPLY_BONUS_COCKBOUND_TEMPLATE.render(model);
            self.removeStatus(Stsflag.cockbound);
        }
        c.write(self, message);
        return bonus;
    }

    @Override
    public String describeAdjective(String partType) {
        return "runic symbols";
    }

    private static final JtwigTemplate APPLY_BONUS_DEMONIC_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The fae energies inside {{ self.nameOrPossessivePronoun() }} "
            + "{{ part.describe(self) }} radiate outward and into "
            + "{{ opponent.nameOrPossessivePronoun() }} causing "
            + "{{ opponent.possessiveAdjective() }} {{ target.describe(opponent) }} to grow "
            + "much more sensitive. "
    );
    private static final JtwigTemplate APPLY_BONUS_ENTHRALLED_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Power radiates out from {{ self.nameOrPossessivePronoun() }} "
            + "{{ part.describe(self) }}, seeping into {{ opponent.nameOrPossessivePronoun() }} "
            + "and subverting {{ opponent.objectPronoun() }} will. "
    );
    private static final JtwigTemplate APPLY_BONUS_COCKBOUND_TEMPLATE = JtwigTemplate.inlineTemplate(
        "With the merest of thoughts, {{ self.subject() }} {{ self.action('send') }} out "
            + "a pulse of energy from {{ self.possessiveAdjective() }} "
            + "{{ part.describe(self) }}, freeing it from {{ opponent.nameOrPossessivePronoun() }} "
            + "{{ binding }}. "
    );
}