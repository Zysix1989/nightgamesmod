package nightgames.characters.body.mods.catcher;

import java.util.ArrayList;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.MouthPart;
import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.pitcher.BionicCockMod;
import nightgames.characters.body.mods.pitcher.PrimalCockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.pet.PetCharacter;
import nightgames.status.Enthralled;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ArcaneMod extends PartMod {
    public static final String TYPE = "arcane";
    public static final ArcaneMod INSTANCE = new ArcaneMod();

    public ArcaneMod() {
        super(TYPE, .05, .1, 0, -5);
    }

    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { 
        int strength;
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        ArrayList<JtwigTemplate> templates = new ArrayList<>();
        if (!target.moddedPartCountsAs(BionicCockMod.TYPE)) {
            if (target.moddedPartCountsAs(PrimalCockMod.TYPE)) {
                templates.add(APPLY_BONUS_PRIMAL_TEMPLATE);
                strength = 10 + self.get(Attribute.Arcane) / 4;
            } else {
                model = model.with("fucking",
                    c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part));
                templates.add(APPLY_BONUS_TEMPLATE);
                strength = 5 + self.get(Attribute.Arcane) / 6;
            }

            opponent.drainMojo(c, self, strength);
            if (self.isPet()) {
                Character master = ((PetCharacter) self).getSelf().owner();
                model = model.with("master", master);
                templates.add(APPLY_BONUS_PET_TEMPLATE);
                master.buildMojo(c, strength);
            }
            if (Global.random(8) == 0 && !opponent.wary()) {
                templates.add(APPLY_BONUS_ENTHRALL_TEMPLATE);
                opponent.add(c, new Enthralled(opponent, self, 3));
            }
        } else {
            templates.add(APPLY_BONUS_BIONIC_TEMPLATE);
        }
        for (JtwigTemplate template : templates) {
            c.write(self, template.render(model));
        }
        return 0;
    }

    public int counterValue(BodyPart part, BodyPart otherPart, Character self, Character other) { 
        return otherPart.moddedPartCountsAs(PrimalCockMod.TYPE) ? 1 : otherPart.moddedPartCountsAs(
            BionicCockMod.TYPE) ? -1 : 0;
    }

    @Override
    public String describeAdjective(String partType) {
        if (partType.equals(MouthPart.TYPE)) {
            return "arcane luster";
        }
        return "arcane tattoos";
    }

    private static final JtwigTemplate APPLY_BONUS_PRIMAL_TEMPLATE = JtwigTemplate.inlineTemplate(
        " {{ if part.isType('mouth') -}} The arcane lipstick painted on "
            + "{{ else - }}The tattoos around {{ endif -}}"
            + "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }} flare up "
            + "with a new intensity, responding to the energy flow from "
            + "{{ opponent.nameOrPossessivePronoun() }} {{ target.describe(opponent) }}. "
            + "The magic within them latches onto it and pulls fiercly, drawing "
            + "{{ opponent.possessiveAdjective() }} strength into "
            + "{{ opponent.possessiveAdjective() }} with great gulps.");

    private static final JtwigTemplate APPLY_BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} "
            + "{{ (part.isType('mouth')) ? 'lipstick on' : 'tattoos surrounding' }} "
            + "{{ self.possessiveAdjective() }} {{ part.getType() }} light up with "
            + "arcane energy as "
            + "{% if (fucking) %} {{ opponent.subjectAction('are', 'is') }} inside "
            + "{{ self.objectPronoun() }}"
            + "{% else %}{{ self.subjectAction('touch') }} {{ opponent.objectPronoun() }}"
            + "{% endif %} channeling some of {{ opponent.possessiveAdjective() }} "
            + "energies back to its master.");

    private static final JtwigTemplate APPLY_BONUS_PET_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The energy seems to flow through {{ self.objectPronoun() }} and into "
            + "{{ self.possessiveAdjective() }} "
            + "{{ (master.useFemalePronouns()) ? 'mistress' : 'master' }}." );

    private static final JtwigTemplate APPLY_BONUS_ENTHRALL_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The light seems to seep into {{ opponent.possessiveAdjective() }} "
            + " {{ target.describe(opponent) }} leaving {{ opponent.objectPronoun() }} "
            + "enthralled to {{ self.possessiveAdjective() }} will.");

    private static final JtwigTemplate APPLY_BONUS_BIONIC_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} "
            + "{{ (part.isType('mouth')) ? \"lipstick\" : \"tattoos\" }} shine with an "
            + "eldritch light, but they do not seem to be able to affect "
            + "{{ opponent.nameOrPossessivePronoun() }} only partially-organic "
            + "{{ target.describe(opponent) }}");
}
