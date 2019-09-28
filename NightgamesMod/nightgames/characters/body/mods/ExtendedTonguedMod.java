package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.MouthPart;
import nightgames.combat.Combat;
import nightgames.status.CockBound;
import nightgames.status.Stsflag;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ExtendedTonguedMod extends PartMod {
    public static final String TYPE = "extendedtongue";
    public static final PartMod INSTANCE = new ExtendedTonguedMod();

    private ExtendedTonguedMod() {
        super(TYPE, .3, 1.2, 0, 4);
    }

    @Override
    public String adjective(GenericBodyPart part) {
        return "";
    }

    public String getLongDescriptionOverride(Character self, BodyPart part, String previousDescription) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("part", part);
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
            "{% if part.isType('mouth') %}"
                + "When {{ self.pronoun() }} {{ self.action('open') }} "
                + "{{ self.possessiveAdjective() }} mouth, you see an unnaturally long tongue."
                + "{% else %}"
                + "Occasionally, a pink tongue slides out of {{ self.possessiveAdjective }} "
                + "{{ part.getType() }} and licks {{ self.possessiveAdjective() }} second lips."
                + "{% endif %}"
        );
        return previousDescription + " " + template.render(model);
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { 
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part) && part.isType("pussy")) {
            if (target.isType(CockPart.TYPE) && !opponent.hasStatus(Stsflag.cockbound)) {
                JtwigModel model = JtwigModel.newModel()
                    .with("self", self)
                    .with("opponent", opponent)
                    .with("part", part)
                    .with("target", target);
                JtwigTemplate bindingTemplate = JtwigTemplate.inlineTemplate(
                    "{{ self.nameOrPossessivePronoun() }} {{ part.adjective() }}-tongue"
                );
                JtwigTemplate messageTemplate = JtwigTemplate.inlineTemplate(
                    "{{ self.nameOrPossessivePronoun() }} long sinuous {{ part.adjective() }} "
                        + "tongue wraps around {{ opponent.nameOrPossessivePronoun() }}"
                        + "{{ target.describe(opponent) }}, preventing any escape."
                );
                opponent.add(c, new CockBound(opponent, 5,  bindingTemplate.render(model)));
                c.write(self, messageTemplate.render(model));
            }
        }
        return 0;
    }

    @Override
    public String describeAdjective(String partType) {
        if (partType.equals(MouthPart.TYPE)) {
            return "extra length on its tongue";
        }
        return "tongue";
    }
}