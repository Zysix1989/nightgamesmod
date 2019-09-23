package nightgames.characters.body.mods;

import java.util.ArrayList;
import java.util.EnumSet;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.pet.PetCharacter;
import nightgames.skills.damage.DamageType;
import nightgames.status.Drained;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class DemonicMod extends PartMod {
    public static final String TYPE = "demonic";
    public static final DemonicMod INSTANCE = new DemonicMod();

    public DemonicMod() {
        super(TYPE, .1, .5, .2, 5);
    }

    public String adjective(GenericBodyPart part) {
        if (part.getType().equals("pussy")) {
            return "succubus";
        }
        if (part.getType().equals("ass")) {
            return "devilish";
        }
        if (part.getType().equals("mouth")) {
            return "tainted";
        }
        return "demonic";
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        ArrayList<JtwigTemplate> templates = new ArrayList<>();
        if (opponent.has(Trait.succubus)) {
            c.write(BONUS_AGAINST_SUCCUBUS_TEMPLATE.render(model));
            return 0;
        }
        if (target.moddedPartCountsAs(RunicCockMod.TYPE)) {
            templates.add(BONUS_AGAINST_RUNIC_TEMPLATE);
        } else {
            boolean bottomless = self.has(Trait.BottomlessPit);
            model.with("fucking", c.getStance()
                .isPartFuckingPartInserted(c, opponent, target, self, part));
            model.with("dom", c.getStance().dom(self));
            model.with("bottomless", bottomless);
            templates.add(BONUS_TEMPLATE);
            int strength;
            if (target.moddedPartCountsAs(EnlightenedCockMod.TYPE)) {
                templates.add(BONUS_AGAINST_ENLIGHTENED_TEMPLATE);
                strength = Global.random(20, 31);
            } else {
                strength = Global.random(10, 21);
            }
            if (bottomless) {
                strength = strength * 3 / 2;
            }
            strength = (int) self.modifyDamage(DamageType.drain, opponent, strength);
            opponent.drain(c, self, strength);
            if (self.isPet()) {
                Character master = ((PetCharacter) self).getSelf().owner();
                model.with("master", master);
                templates.add(BONUS_PET_TEMPLATE);
                master.heal(c, strength);
            }
            for (int i = 0; i < 10; i++) {
                Attribute[] canBeStolen =
                    EnumSet.complementOf(EnumSet.of(Attribute.Speed, Attribute.Perception))
                        .stream().filter(a -> opponent.get(a) > 0).toArray(Attribute[]::new);
                Attribute stolen = Global.pickRandom(canBeStolen).orElse(null);
                if (stolen != null) {
                    int stolenStrength = Math.min(strength / 10, opponent.get(stolen));
                    Drained.drain(c, self, opponent, stolen, stolenStrength, 20, true);
                    if (self.isPet()) {
                        Character master = ((PetCharacter) self).getSelf().owner();
                        master.add(c, new Drained(master, opponent, stolen, stolenStrength, 20));
                    }
                    break;
                }
            }
        }
        for (JtwigTemplate template : templates) {
            c.write(self, template.render(model));
        }
        return 0;
    }

    public int counterValue(BodyPart part, BodyPart otherPart, Character self, Character other) {
        if (otherPart.moddedPartCountsAs(RunicCockMod.TYPE)) {
            return -1;
        } else if (otherPart.moddedPartCountsAs(EnlightenedCockMod.TYPE)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String describeAdjective(String partType) {
        return "demonic nature";
    }

    private static final JtwigTemplate BONUS_AGAINST_SUCCUBUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }} does "
            + "nothing special against one of {{ self.possessiveAdjective() }} own kind."
    );

    private static final JtwigTemplate BONUS_AGAINST_RUNIC_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Putting in great effort, {{ self.nameOrPossessivePronoun() }} "
            + "{{ self.action('try', 'tries') }} to draw upon "
            + "{{ opponent.nameOrPossessivePronoun() }} power, but the fae enchantments "
            + "in {{ opponent.possessiveAdjective() }} {{ target.describe(opponent) }} "
            + "keep it locked away.");

    private static final JtwigTemplate BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{% if (fucking) %}"
            + "{{ self.possessiveAdjective() }} hot flesh kneads "
            + "{{ opponent.possessiveAdjective() }} {{ target.describe(opponent) }} as "
            + "{% if part.isType('mouth') %}"
            + "{{ ((dom) ? self : opponent).pronoun() }} "
            + "{{ ((dom) ? self : opponent).action('suck') }} "
            + "{{ ((dom) ? opponent : self).pronoun() }} "
            + "{% else %}"
            + "{{ self.pronoun() }} "
            + "{{ self.action((dom) ? 'fuck' : 'ride') }} "
            + "{{ opponent.possessivePronoun() }} "
            + "{% endif %}"
            + ", drawing "
            + "{% else %}"
            + "As {{ self.possessiveAdjective() }} "
            + "touches {{ opponent.possessivePronoun() }} {{ target.describe(opponent) }} "
            + ", {{ self.pronoun() }} {{ self.action('draw') }} large "
            + "{% endif %}"
            + "gouts of life energy out of "
            + "{{ opponent.possessiveAdjective() }} {{ target.describe(opponent) }}, "
            + "which is {{ (bottomless) ? 'greedily ' : '' -}} absorbed by "
            + "{{ self.possessiveAdjective() }} "
            + "{{ bottomless ? 'seemingly bottomless ' : ''\"\"'' }}"
            + "{{ part.describe(self) }}.");

    private static final JtwigTemplate BONUS_AGAINST_ENLIGHTENED_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Since {{ opponent.subject() }} had focused so much of "
            + "{{ opponent.reflexivePronoun() }} in "
            + "{{ opponent.possessiveAdjective() }} "
            + "{{ target.describe(opponent) }}, there is much more for "
            + "{{ self.subject() }} to take.");

    private static final JtwigTemplate BONUS_PET_TEMPLATE = JtwigTemplate.inlineTemplate(
        "The stolen strength seems to be shared with "
            + "{{ self.possessiveAdjective() }} "
            + "{{ (master.useFemalePronouns()) ? 'mistress' : 'master' }} through "
            + "{{ self.possessiveAdjective() }} infernal connection.");
}
