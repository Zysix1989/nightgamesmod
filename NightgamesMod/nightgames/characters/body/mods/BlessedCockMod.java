package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.CockPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.DivineCharge;
import nightgames.status.Stsflag;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class BlessedCockMod extends CockMod {
    public static final String TYPE = "blessed";

    public BlessedCockMod() {
        super(TYPE, 1.0, 1.0, .75);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);
        if (target.isType(CockPart.TYPE)) {
            if (self.getStatus(Stsflag.divinecharge) != null) {
                var model = JtwigModel.newModel()
                    .with("self", self)
                    .with("other", opponent)
                    .with("part", part)
                    .with("target", target);
                c.write(self, APPLY_BONUS_TEMPLATE.render(model));
            }
            // no need for any effects, the bonus is in the pleasure mod
        }
        return bonus;
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        if (c.getStance().inserted(self)) {
            DivineCharge charge = (DivineCharge) self.getStatus(Stsflag.divinecharge);
            var model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part);
            if (charge == null) {
                c.write(self, RECEIVE_NO_CHARGE_TEMPLATE.render(model));
                self.add(c, new DivineCharge(self, .25));
            } else {
                c.write(self, RECEIVE_WITH_CHARGE_TEMPLATE.render(model));
                self.add(c, new DivineCharge(self, charge.magnitude));
            }
        }
        return 0;
    }

    @Override
    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
        if (target.isErogenous()) {
            if (!self.human()) {
                var model = JtwigModel.newModel()
                    .with("self", self)
                    .with("opponent", opponent)
                    .with("part", part);
                c.write(self, ON_START_PENETRATION_TEMPLATE.render(model));
            }
        }
    }

    @Override
    public String describeAdjective(String partType) {
            return "holy aura";
    }

    private static final JtwigTemplate APPLY_BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} concentrated divine energy in"
            + " {{ self.possessiveAdjective }} {{ part.describe(self) }} rams into "
            + "{{ other.nameOrPossessivePronoun }} {{ target.describe(other) }}, sending unimaginable "
            + "pleasure directly into {{ other.possessiveAdjective }} soul."
    );

    private static final JtwigTemplate RECEIVE_NO_CHARGE_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} {{ part.fullDescribe(self) }} radiates "
            + "a golden glow as {{ self.subjectAction('groan') }}. "
            + "{{ opponent.subjectAction('realize') }} "
            + "{{ self.subjectAction('are', 'is') }} feeding on "
            + "{{ self.possessiveAdjective }} own pleasure to charge up "
            + "{{ self.possessiveAdjective }} divine energy."
    );

    private static final JtwigTemplate RECEIVE_WITH_CHARGE_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.subjectAction('continue') }} feeding on {{ self.possessiveAdjective }}"
            + "own pleasure to charge up {{ self.possessiveAdjective }} divine energy."
    );

    private static final JtwigTemplate ON_START_PENETRATION_TEMPLATE = JtwigTemplate.inlineTemplate(
        "As soon as {{ self.subject() }} penetrates {{ opponent.objectPronoun() }}, "
            + "{{ opponent.subject() }} realize "
            + "{{ opponent.subjectAction('are', 'is') }} screwed. "
            + "Both literally and figuratively. While it looks innocuous enough, "
            + "{{ self.nameOrPossessivePronoun }} {{ part.describe(self) }} "
            + "feels like pure ecstasy. {{ self.subject() }} hasn't even begun moving yet, "
            + "but {{ self.nameOrPossessivePronoun() }} cock simply sitting within {{ opponent.objectPronoun() }} "
            + "radiates a heat that has {{ opponent.objectPronoun() }} squirming uncontrollably."
    );
}
