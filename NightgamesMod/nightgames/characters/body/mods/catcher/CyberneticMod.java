package nightgames.characters.body.mods.catcher;

import nightgames.characters.Character;
import nightgames.characters.body.AssPart;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.PussyPart;
import nightgames.characters.body.mods.pitcher.BionicCockMod;
import nightgames.characters.body.mods.pitcher.CockMod;
import nightgames.characters.body.mods.pitcher.EnlightenedCockMod;
import nightgames.characters.body.mods.pitcher.IncubusCockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Shamed;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class CyberneticMod extends CatcherMod {
    public static final String TYPE = "cybernetic";

    public static final CyberneticMod INSTANCE = new CyberneticMod();

    public CyberneticMod() {
        super(TYPE, -.1, .8, -.5);
    }


    public String adjective(GenericBodyPart part) {
        if (part.getType().equals(PussyPart.TYPE)) {
            return "cybernetic";
        }
        if (part.getType().equals(AssPart.TYPE)) {
            return "biomech";
        }
        return "prosthetic";
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        int bonus = 0;

        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part)) {
            var model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target)
                .with("incubusType", IncubusCockMod.TYPE);
            if (target.moddedPartCountsAs(EnlightenedCockMod.TYPE)) {
                c.write(self, BONUS_AGAINST_ENLIGHTENED_TEMPLATE.render(model));
                bonus -= 5;
            } else {
                if (Global.random(3) == 0 || target.moddedPartCountsAs(IncubusCockMod.TYPE)) {
                    c.write(self, BONUS_TEMPLATE.render(model));
                    bonus += 15;
                    if (target.moddedPartCountsAs(IncubusCockMod.TYPE) || Global.random(4) == 0) {
                        opponent.add(c, new Shamed(opponent));
                    }
                }
            }
        }
        return bonus;
    }

    public int counterValue(BodyPart part, BodyPart otherPart, Character self, Character other) { 
        return otherPart.moddedPartCountsAs(IncubusCockMod.TYPE) ? 1 : otherPart.moddedPartCountsAs(
            EnlightenedCockMod.TYPE) ? -1 : 0;
    }

    @Override
    public String describeAdjective(String partType) {
        return "cybernetics";
    }

    private static final JtwigTemplate BONUS_AGAINST_ENLIGHTENED_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Despite {{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }}'s best "
            + "efforts, {{ opponent.nameOrPossessivePronoun() }} focus does not waver, "
            + "and {{ opponent.pronounAction('feel') }} barely a thing.");

    private static final JtwigTemplate BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{% if ( target.moddedPartCountsAs(incubusType) -%}"
            + "Eager to gain a sample of {{ opponent.nameOrPossessivePronoun() }}"
            + "exotic, demonic sperm, "
            + "{% endif %}"
            + "{{ self.possessiveAdjective() }} {{ part.describe(self) }}"
            + "whirls to life and starts attempting to extract all the semen "
            + "packed inside {{ opponent.possessiveAdjective }} "
            + "{{ target.describe(opponent) }}. At the same time "
            + "{{ opponent.pronounAction('feel') }} a thin filament sliding "
            + "into opponent.possessiveAdjective(), filling "
            + "{{ opponent.objectPronoun }} with both pleasure and shame.");

    @Override
    public CockMod getCorrespondingCockMod() {
        return new BionicCockMod();
    }
}
