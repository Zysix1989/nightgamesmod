package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
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
        if (target.isType("cock")) {
            if (self.getStatus(Stsflag.divinecharge) != null) {
                var model = JtwigModel.newModel()
                    .with("self", self)
                    .with("other", opponent);
                var template = JtwigTemplate.inlineTemplate(
                    "{{ self.nameOrPossessivePronoun() }} concentrated divine energy in"
                        + " {{ self.possessiveAdjective }} cock rams into "
                        + "{{ other.nameOrPossessivePronoun }} pussy, sending unimaginable "
                        + "pleasure directly into {{ other.possessiveAdjective }} soul."
                );
                c.write(self, template.render(model));
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
                var template = JtwigTemplate.inlineTemplate(
                    "{{ self.nameOrPossessivePronoun() }} {{ part.fullDescribe(self) }} radiates "
                        + "a golden glow as {{ self.subjectAction('groan') }}. "
                        + "{{ opponent.subjectAction('realize') }} "
                        + "{{ self.subjectAction('are', 'is') }} feeding on "
                        + "{{ self.possessiveAdjective }} own pleasure to charge up "
                        + "{{ self.possessiveAdjective }} divine energy."
                    );
                c.write(self, template.render(model));
                self.add(c, new DivineCharge(self, .25));
            } else {
                var template = JtwigTemplate.inlineTemplate(
                    "{{ self.subjectAction('continue') }} feeding on {{ self.possessiveAdjective }}"
                        + "own pleasure to charge up {{ self.possessiveAdjective }} divine energy."
                );
                c.write(self, template.render(model));
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
                var template = JtwigTemplate.inlineTemplate(
                    "As soon as {{ self.subject() }} penetrates {{ opponent.objectPronoun() }}, "
                        + "{{ opponent.subject() }} realize "
                        + "{{ opponent.subjectAction('are', 'is') }} screwed. "
                        + "Both literally and figuratively. While it looks innocuous enough, "
                        + "{{ self.nameOrPossessivePronoun }} {{ part.describe(self) }} "
                        + "feels like pure ecstasy. {{ self.subject() }} hasn't even begun moving yet, "
                        + "but {{ self.nameOrPossessivePronoun() }} cock simply sitting within {{ opponent.objectPronoun() }} "
                        + "radiates a heat that has {{ opponent.objectPronoun() }} squirming uncontrollably."
                );
                c.write(self, template.render(model));
            }
        }
    }

    @Override
    public String describeAdjective(String partType) {
            return "holy aura";
    }
}
