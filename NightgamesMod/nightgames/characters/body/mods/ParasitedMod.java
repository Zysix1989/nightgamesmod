package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ParasitedMod extends PartMod {
    public static final String TYPE = "parasited";

    public ParasitedMod() {
        super(TYPE, 0, 0, 0);
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        c.write(self, RECEIVE_TEMPLATE.render(model));
        return 10;
    }

    public void onOrgasm(Combat c, Character self, Character opponent, BodyPart part) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part);
        c.write(self, ON_ORGASM_TEMPLATE.render(model));
        ((GenericBodyPart) part).removeTemporaryMod(this);
    }

    public Optional<String> getFluids() {
        return Optional.of("slime parasite");
    }

    @Override
    public String describeAdjective(String partType) {
        return "parasite";
    }

    private static final JtwigTemplate RECEIVE_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The parasite inhabiting {{ self.nameOrPossessivePronoun() }} {{ part.getType() }} "
            + "is making {{ self.objectPronoun() }} <b>extremely sensitive</b>.");

    private static final JtwigTemplate ON_ORGASM_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The force of {{ self.nameOrPossessivePronoun() }} orgasm ejects the slimy lifeform "
            + "from {{ self.possessiveAdjective() }} {{ part.describe(self) }}.");

}
