package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class TrainedMod extends PartMod {
    public static final String TYPE = "trained";
    public static final TrainedMod INSTANCE = new TrainedMod();

    public TrainedMod() {
        super(TYPE, .2, .2, -.2, -100);
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { 
        if (opponent.human()) {
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            JtwigTemplate template = JtwigTemplate.inlineTemplate(
                "{{ self.possessiveAdjective() }} trained {{ part.getType() }} feels positively exquisite. "
                    + "It's taking all your concentration not to instantly shoot your load.");
            c.write(self, template.render(model));
        }
        return 0;
    }

    @Override
    public String describeAdjective(String partType) {
        return "expertly-trained appearance";
    }
}
