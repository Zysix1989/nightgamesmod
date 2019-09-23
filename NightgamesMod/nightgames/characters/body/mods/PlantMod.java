package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Trance;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class PlantMod extends PartMod {
    public static final String TYPE = "plant";
    public static final PlantMod INSTANCE = new PlantMod();

    public PlantMod() {
        super(TYPE, .3, 1, .2, 10);
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        if (damage > self.getArousal().max()/ 5.0 && Global.random(4) == 0) {
            c.write(self, String.format("An intoxicating scent emanating from %s %s leaves %s in a trance!",
            self.possessiveAdjective(), part.describe(self), opponent.objectPronoun()));
            opponent.add(c, new Trance(opponent));
        }
        return 0;
    }

    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {}

    public void tickHolding(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part)) {
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            JtwigTemplate template = JtwigTemplate.inlineTemplate(
                "The small rough fibrous filaments inside {{ self.nameOrPossessivePronoun() }} "
                    + "flower {{ part.getType() }} wrap around "
                    + "{{ other.nameOrPossessivePronoun() }} cock. A profound exhaustion settles "
                    + "on {{ other.objectPronoun() }}, as {{ other.subject() }} "
                    + "{{ other.action('feel') }} {{ self.nameOrPossessivePronoun() }} insidious "
                    + "flower leeching {{ other.possessiveAdjective() }} strength.");
            c.write(self, template.render(model));
            opponent.drainStaminaAsMojo(c, self, 20, 1.25f);
            opponent.loseWillpower(c, 5);
        }
    }

    @Override
    public String describeAdjective(String partType) {
        return "floral appearance";
    }
}
