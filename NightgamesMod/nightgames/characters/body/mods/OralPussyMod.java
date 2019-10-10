package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class OralPussyMod extends AdditionalPussyMod {

    public String getLongDescriptionOverride(Character self, BodyPart part, String previousDescription) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("part", part);
        return LONG_DESCRIPTION_MOUTH_TEMPLATE.render(model);
    }

    private static final JtwigTemplate LONG_DESCRIPTION_MOUTH_TEMPLATE = JtwigTemplate.inlineTemplate("When {{ self.pronoun() }} opens "
        + "{{ self.possessiveAdjective() }} mouth, you can see soft pulsating folds "
        + "lining {{ self.possessiveAdjective() }} inner mouth, tailor made to suck cocks.");

}
