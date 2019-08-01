package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ParasitedMod extends PartMod {
    public static final ParasitedMod INSTANCE = new ParasitedMod();

    public ParasitedMod() {
        super("parasited", 0, 0, 0, -1000);
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
            "The parasite inhabiting {{ self.nameOrPossessivePronoun() }} {{ part.getType() }} "
                + "is making {{ self.directObject() }} <b>extremely sensitive</b>.");
        c.write(self, template.render(model));
        return 10;
    }

    public void onOrgasm(Combat c, Character self, Character opponent, BodyPart part) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part);
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
            "The force of {{ self.nameOrPossessivePronoun() }} orgasm ejects the slimy lifeform "
                + "from {{ self.possessiveAdjective() }} {{ part.describe(self) }}.");
        c.write(self, template.render(model));
        self.body.removeTemporaryPartMod(part.getType(), this);
    }

    public Optional<String> getFluids() {
        return Optional.of("slime parasite");
    }

    @Override
    public String describeAdjective(String partType) {
        return "parasite";
    }
}
