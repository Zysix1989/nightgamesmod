package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Pheromones;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class PrimalCockMod extends CockMod {
    public static final String TYPE = "primal";

    public PrimalCockMod() {
        super(TYPE, 1.0, 1.4, 1.2);
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target) {
        var model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        c.write(self, TICK_HOLDING_TEMPLATE.render(model));
        opponent.add(c, Pheromones
            .getWith(self, opponent, Global.random(3) + 1, 3, " primal passion"));
    }

    @Override
    public String describeAdjective(String partType) {
        return "primal musk";
    }

    private static final JtwigTemplate TICK_HOLDING_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Raw sexual energy flows from {{ self.nameOrPossessivePronoun() }} "
            + "{{ part.describe(self) }} into {{ opponent.nameOrPossessivePronoun() }} "
            + "{{ target.describe(opponent) }}, inflaming {{ opponent.possessiveAdjective() }} "
            + "lust. "
    );
}
