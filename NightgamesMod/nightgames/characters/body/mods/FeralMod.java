package nightgames.characters.body.mods;

import java.util.ArrayList;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Frenzied;
import nightgames.status.IgnoreOrgasm;
import nightgames.status.Pheromones;
import nightgames.status.Stsflag;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class FeralMod extends PartMod {
    public static final FeralMod INSTANCE = new FeralMod();

    public FeralMod() {
        super("feral", .2, .3, .2, -8);
    }
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { 
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part)) {
            int chance = Math.max(5, 10 - self.getArousal()
                            .getReal() / 50);
            if (!self.is(Stsflag.frenzied) && !self.is(Stsflag.cynical) && target.isType("cock")
                      && Global.random(chance) == 0) {
                JtwigModel model = JtwigModel.newModel()
                    .with("self", self)
                    .with("opponent", opponent);
                JtwigTemplate template = JtwigTemplate.inlineTemplate(
                    "A cloud of lust descends over {{ opponent.getName() }} and " 
                        + "{{ self.getName() }}, clearing both of your thoughts of all matters " 
                        + "except to fuck. Hard."
                );
                c.write(self, template.render(model));
                self.add(c, new IgnoreOrgasm(opponent, 3));
                self.add(c, new Frenzied(self, 3));
                opponent.add(c, new Frenzied(opponent, 3));
            }
        }
        return 0;
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        if (c.getStance().distance() < 2) {
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            ArrayList<JtwigTemplate> templates = new ArrayList<>();
            templates.add(JtwigTemplate.inlineTemplate(
                "Musk emanating from {{ self.possessiveAdjective() }} {{ part.describe(self) }} "
                    + "leaves {{ opponent.directObject() }} reeling."
            ));
            double base = 3;
            if (target.moddedPartCountsAs(opponent, CockMod.runic)) {
                templates.add(JtwigTemplate.inlineTemplate(
                    "The wild scent overwhelms {{ opponent.nameOrPossessivePronoun() }} "
                        + "carefully layered enchantments, instantly sweeping them away."
                ));
                base *= 2.5;
            } else if (target.moddedPartCountsAs(opponent, CockMod.incubus)) {
                templates.add(JtwigTemplate.inlineTemplate(
                    "Whilst certainly invigorating, the scent leaves {{ opponent.subject() }} "
                        + "largely unaffected."
                ));
                base /= 2;
            }
            for (JtwigTemplate template : templates) {
                c.write(self, template.render(model));
            }
            opponent.add(c, Pheromones.getWith(self, opponent, (float) base, 5, " feral musk"));
        }
        return 0;
    }

    public void onOrgasm(Combat c, Character self, Character opponent, BodyPart part) {
        if (c.getStance().distance() < 2) {
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part);
            JtwigTemplate template = JtwigTemplate.inlineTemplate(
                "As {{ self.subject() }} {{{ self.action('cum') }} hard, a literal explosion of "
                    + "pheromones hits {{ other.nameDirectObject() }}. "
                    + "{{ other.possessiveAdjective() }} entire body flushes in arousal; "
                    + "{{ other.subject() }} better finish this fast!"
            );
            c.write(self, template.render(model));
            opponent.add(c, Pheromones.getWith(self, opponent, 10, 5, " orgasmic secretions"));
        }
    }

    public int counterValue(BodyPart part, BodyPart otherPart, Character self, Character other) { 
        return otherPart.moddedPartCountsAs(other, CockMod.runic) ? 1 : otherPart.moddedPartCountsAs(other, CockMod.incubus) ? -1 : 0;
    }

    @Override
    public String describeAdjective(String partType) {
        return "feral musk";
    }
}
