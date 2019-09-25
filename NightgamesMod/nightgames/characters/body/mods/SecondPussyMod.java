package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class SecondPussyMod extends PartMod {
    public static final String TYPE = "secondpussy";
    public static final SecondPussyMod INSTANCE = new SecondPussyMod();

    public SecondPussyMod() {
        super(TYPE, .2, .2, .3, 999);
    }

    @Override
    public String adjective(GenericBodyPart part) {
        return "";
    }

    public Optional<String> getFluids() {
        return Optional.of("juices");
    }

    public String getLongDescriptionOverride(Character self, BodyPart part, String previousDescription) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("part", part);
        JtwigTemplate template;
        if (part.isType("ass")) {
            template = LONG_DESCRIPTION_ASS_TEMPLATE;
        } else if (part.isType("mouth")) {
            template = LONG_DESCRIPTION_MOUTH_TEMPLATE;
        } else {
            template = JtwigTemplate.inlineTemplate(previousDescription);
        }
        return template.render(model);
    }

    public Optional<String> getDescriptionOverride(Character self, BodyPart part) {
        return Optional.of(part.adjective() + " pussy");
    }

    public Optional<Boolean> getErogenousOverride() {
        return Optional.of(true);
    }

    @Override
    public String describeAdjective(String partType) {
        return "vaginal aspects";
    }

    private static final JtwigTemplate LONG_DESCRIPTION_ASS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Instead of a normal sphincter, {{ self.possessiveAdjective() }} round butt "
            + "is crowned by a slobbering second pussy.");

    private static final JtwigTemplate LONG_DESCRIPTION_MOUTH_TEMPLATE = JtwigTemplate.inlineTemplate("When {{ self.pronoun() }} opens "
        + "{{ self.possessiveAdjective() }} mouth, you can see soft pulsating folds "
        + "lining {{ self.possessiveAdjective() }} inner mouth, tailor made to suck cocks.");

}
