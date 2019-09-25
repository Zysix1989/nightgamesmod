package nightgames.characters.body.mods;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.pet.PetCharacter;
import nightgames.skills.damage.DamageType;
import nightgames.status.Drained;
import nightgames.status.Enthralled;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class IncubusCockMod extends CockMod {
    public static final String TYPE = "incubus";



    public IncubusCockMod() {
        super(TYPE, 1.25, 1.3, .9);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);
        var model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent);
        String message = APPLY_BONUS_TEMPLATE.render(model);
        int amtDrained;
        if (target.moddedPartCountsAs(FeralMod.TYPE)) {
            message += " " + APPLY_BONUS_FERAL_TEMPLATE.render(model);
            amtDrained = 5;
            bonus += 2;
        } else if (target.moddedPartCountsAs(CyberneticMod.TYPE)) {
            message += APPLY_BONUS_CYBERNETIC_TEMPLATE.render(model);
            self.pain(c, opponent, Global.random(9) + 4);
            amtDrained = 0;
        } else {
            message += APPLY_BONUS_2_TEMPLATE.render(model);
            amtDrained = 3;
        }
        int strength = (int) self.modifyDamage(DamageType.drain, opponent, amtDrained);
        if (amtDrained != 0) {
            if (self.isPet()) {
                Character master = ((PetCharacter) self).getSelf().owner();
                model = model.with("master", master);
                c.write(self, APPLY_BONUS_PET_TEMPLATE.render(model));
                opponent.drainWillpower(c, master, strength);
            } else {
                opponent.drainWillpower(c, self, strength);
            }
        }
        c.write(self, message);
        return bonus;
    }

    @Override
    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {
        if (this.equals(incubus) && c.getStance().inserted(self)) {
            var model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            if (selfCame) {
                if (target.moddedPartCountsAs(CyberneticMod.TYPE)) {
                    c.write(self, ON_ORGASM_WITH_CYBERNETIC_TEMPLATE.render(model));
                } else {
                    int duration = Global.random(3) + 2;
                    String message = ON_ORGASM_WITH_FERAL_TEMPLATE.render(model);
                    if (target.moddedPartCountsAs(FeralMod.TYPE)) {
                        var template2 = JtwigTemplate.inlineTemplate("{{ opponent.subject() }} {{ opponent.action('offer') }} "
                            + "no resistance to the subversive seed."
                        );
                        message += template2.render(model);
                        duration += 2;
                    }
                    opponent.add(c, new Enthralled(opponent, self, duration));
                    c.write(self, message);
                }
            } else {
                if (!target.moddedPartCountsAs(CyberneticMod.TYPE)) {
                    c.write(self, ON_ORGASM_WITH_TEMPLATE.render(model));
                    int attDamage = target.moddedPartCountsAs(FeralMod.TYPE) ? 10 : 5;
                    int willDamage = target.moddedPartCountsAs(FeralMod.TYPE) ? 10 : 5;
                    Drained.drain(c, self, opponent, Attribute.Power, attDamage, 20, true);
                    Drained.drain(c, self, opponent, Attribute.Cunning, attDamage, 20, true);
                    Drained.drain(c, self, opponent, Attribute.Seduction, attDamage, 20, true);
                    opponent.drainWillpower(c, self, (int) self.modifyDamage(DamageType.drain, opponent, willDamage));
                }
            }
        }
    }

    @Override
    public String describeAdjective(String partType) {
        return "corruption";
    }

    private static final JtwigTemplate APPLY_BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} demonic appendage latches onto "
            + "{{ opponent.nameOrPossessivePronoun() }} will, trying to draw it "
            + "into {{ self.reflexivePronoun() }}."
    );

    private static final JtwigTemplate APPLY_BONUS_FERAL_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ opponent.possessiveAdjective() }} {{ target.describe(opponent) }} gladly "
            + "gives it up, eager for more pleasure."
    );

    private static final JtwigTemplate APPLY_BONUS_CYBERNETIC_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ opponent.nameOrPossessivePronoun() }} {{ target.describe(opponent) }} does "
            + "not oblige, instead sending a pulse of electricity through "
            + "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }} and up "
            + "{{ self.possessiveAdjective() }} spine."
    );

    private static final JtwigTemplate APPLY_BONUS_2_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Despite {{ opponent.nameOrPossessivePronoun() }} best efforts, some of the "
            + "elusive energy passes into {{ self.nameDirectObject() }}."
    );

    private static final JtwigTemplate APPLY_BONUS_PET_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The stolen strength seems to flow through to {{ self.possessiveAdjective }} "
            + "{{ master.masterOrMistress() }} through their infernal connection."
    );

    private static final JtwigTemplate ON_ORGASM_WITH_CYBERNETIC_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} demonic seed splashes pointlessly "
            + "against the walls of {{ opponent.nameOrPossessivePronoun() }} "
            + "{{ target.describe(opponent) }}, failing even in "
            + " {{ self.possessiveAdjective() }} moment of defeat."
    );

    private static final JtwigTemplate ON_ORGASM_WITH_FERAL_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The moment {{ self.subject }} {{ self.action('erupt') }} inside "
            + "{{ opponent.subject() }}, {{ opponent.possessiveAdjective}} mind "
            + "goes completely blank, leaving {{ opponent.possessiveAdjective() }} "
            + "pliant and ready."
    );

    private static final JtwigTemplate ON_ORGASM_WITH_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Sensing {{ opponent.nameOrPossessivePronoun() }} moment of passion, "
            + "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }} "
            + "greedily draws upon the rampant flows of orgasmic energy within "
            + "{{ opponent.objectPronoun() }} transferring the power back into "
            + "{{ self.objectPronoun() }}."
    );
}