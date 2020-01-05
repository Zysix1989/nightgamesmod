package nightgames.traits;

import nightgames.grammar.Person;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class Slime {
    public static int painModifier(int pain) { return -pain / 2; }
    public static String textOnPain(Person target) {
        var model = JtwigModel.newModel().with("target", target);
        return ON_PAIN.render(model);
    }
    private static final JtwigTemplate ON_PAIN = JtwigTemplate.inlineTemplate(
            "The blow glances off {{ target.possessivePronoun() }} slimy body."
    );
}
