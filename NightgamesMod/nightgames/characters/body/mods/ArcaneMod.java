package nightgames.characters.body.mods;

import java.util.ArrayList;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.pet.PetCharacter;
import nightgames.status.Enthralled;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ArcaneMod extends PartMod {
    public static final ArcaneMod INSTANCE = new ArcaneMod();

    public ArcaneMod() {
        super("arcane", .05, .1, 0, -5);
    }

    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {
        /* TODO
        if (c.getStance().partsForStanceOnly(c, self, opponent).contains(part)) {
            if (c.getStance().partsForStanceOnly(c, opponent, self).stream().anyMatch(otherPart -> otherPart.isType("cock"))) {
                c.write(self, Global.format(
                                "The intricate tattoos surrounding %s lights up as you pump your semen into into it. TODO",
                                self, opponent, part.describe(self)));
                
            }
        }
        */
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { 
        int strength;
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        ArrayList<JtwigTemplate> templates = new ArrayList<>();
        if (!target.moddedPartCountsAs(opponent, CockMod.bionic)) {
            if (target.moddedPartCountsAs(opponent, CockMod.primal)) {
                templates.add(JtwigTemplate.inlineTemplate(
                    " {{ if part.isType('mouth') -}} The arcane lipstick painted on "
                        + "{{ else - }}The tattoos around {{ endif -}}"
                        + "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }} flare up "
                        + "with a new intensity, responding to the energy flow from "
                        + "{{ opponent.nameOrPossessivePronoun() }} {{ target.describe(opponent) }}. "
                        + "The magic within them latches onto it and pulls fiercly, drawing "
                        + "{{ opponent.possessiveAdjective() }} strength into "
                        + "{{ opponent.possessiveAdjective() }} with great gulps."));
                strength = 10 + self.get(Attribute.Arcane) / 4;
            } else {
                model.with("fucking", c.getStance()
                    .isPartFuckingPartInserted(c, opponent, target, self, part));
                templates.add(JtwigTemplate.inlineTemplate(
                    "{{ self.nameOrPossessivePronoun() }} "
                        + "{{ (part.isType('mouth')) ? 'lipstick on' : 'tattoos surrounding' }} "
                        + "{{ self.possessiveAdjective() }} {{ part.getType() }} light up with "
                        + "arcane energy as "
                        + "{% if (fucking) %} {{ opponent.subjectAction('are', 'is') }} inside "
                        + "{{ self.directObject() }}"
                        + "{% else %}{{ self.subjectAction('touch') }} {{ opponent.directObject() }}"
                        + "{% endif %} channeling some of {{ opponent.possessiveAdjective() }} "
                        + "energies back to its master."));
                strength = 5 + self.get(Attribute.Arcane) / 6;
            }
            opponent.drainMojo(c, self, strength);
            if (self.isPet()) {
                Character master = ((PetCharacter) self).getSelf().owner();
                model.with("master", master);
                templates.add(JtwigTemplate.inlineTemplate(
                    "The energy seems to flow through {{ self.directObject() }} and into "
                        + "{{ self.possessiveAdjective() }} "
                        + "{{ (master.useFemalePronouns()) ? 'mistress' : 'master' }}." ));
                master.buildMojo(c, strength);
            }
            if (Global.random(8) == 0 && !opponent.wary()) {
                templates.add(JtwigTemplate.inlineTemplate(
                    "The light seems to seep into {{ opponent.possessiveAdjective() }} "
                        + " {{ target.describe(opponent) }} leaving {{ opponent.directObject() }} "
                        + "enthralled to {{ self.possessiveAdjective() }} will."));
                opponent.add(c, new Enthralled(opponent, self, 3));
            }
        } else {
            templates.add(JtwigTemplate.inlineTemplate(
                "{{ self.nameOrPossessivePronoun() }} "
                    + "{{ (part.isType('mouth')) ? \"lipstick\" : \"tattoos\" }} shine with an "
                    + "eldritch light, but they do not seem to be able to affect "
                    + "{{ opponent.nameOrPossessivePronoun() }} only partially-organic "
                    + "{{ target.describe(opponent) }}"));
        }
        for (JtwigTemplate template : templates) {
            c.write(self, template.render(model));
        }
        return 0;
    }

    public int counterValue(BodyPart part, BodyPart otherPart, Character self, Character other) { 
        return otherPart.moddedPartCountsAs(other, CockMod.primal) ? 1 : otherPart.moddedPartCountsAs(other, CockMod.bionic) ? -1 : 0;
    }

    @Override
    public String describeAdjective(String partType) {
        if (partType.equals("mouth")) {
            return "arcane luster";
        }
        return "arcane tattoos";
    }
}
