package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class AnalPussyMod extends AdditionalPussyMod {

    public String getLongDescriptionOverride(Character self, BodyPart part, String previousDescription) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("part", part);
        return LONG_DESCRIPTION_ASS_TEMPLATE.render(model);
    }

    private static final JtwigTemplate LONG_DESCRIPTION_ASS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Instead of a normal sphincter, {{ self.possessiveAdjective() }} round butt "
            + "is crowned by a slobbering second pussy.");
}
