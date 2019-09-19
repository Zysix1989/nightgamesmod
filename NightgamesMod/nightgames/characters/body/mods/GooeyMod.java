package nightgames.characters.body.mods;

import java.util.Optional;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.CockBound;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class GooeyMod extends PartMod {
    public static final String TYPE = "gooey";
    public static final GooeyMod INSTANCE = new GooeyMod();

    public GooeyMod() {
        super(TYPE, .2, .5, .2, 2);
    }

    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part) && !selfCame) {
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            JtwigTemplate template = JtwigTemplate.inlineTemplate(
                "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }} clenches down hard"
                    + " on {{ other.nameOrPossessivePronoun() }} {{ target.describe(opponent) }}. "
                    + "The suction is so strong that the cum leaves "
                    + "{{ opponent.possessiveAdjective() }} shaft in a constant flow rather than "
                    + "spurts. When {{ other.possessiveAdjective() }} orgasm is over, "
                    + "{{ other.subject() }}{{{ other.action('are') }} much more drained of cum "
                    + "than usual.");
            c.write(self, template.render(model));
            opponent.loseWillpower(c, 10 + Global.random(Math.min(20, self.get(Attribute.Bio))));
        }
    }

    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part)) {
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            JtwigTemplate template = JtwigTemplate.inlineTemplate(
                "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }} envelops"
                + " {{ other.possessiveAdjective() }} {{ target.describe(opponent) }} in a "
                    + "sticky grip, making pulling out more difficult.");
            JtwigTemplate bindingTemplate = JtwigTemplate.inlineTemplate(
                "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }}"
            );
            c.write(self, template.render(model));
            opponent.add(c, new CockBound(opponent, 7, bindingTemplate.render(model)));
        }
    }

    public void tickHolding(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
            "The slimy filaments inside {{ self.possessiveAdjective() }} {{ part.describe(self) }} "
                + "constantly massage {{ other.possessiveAdjective() }} "
                + "{{ target.describe(opponent) }}, filling every inch of it with pleasure."
        );
        c.write(self, template.render(model));
        opponent.body.pleasure(self, part, target, 1 + Global.random(7), c);
    }

    public Optional<String> getFluids() {
        return Optional.of("slime");
    }

    @Override
    public String describeAdjective(String partType) {
        return "gooey consistency";
    }
}
