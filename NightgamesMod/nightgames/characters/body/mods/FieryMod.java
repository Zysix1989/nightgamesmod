package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class FieryMod extends PartMod {
    public static final FieryMod INSTANCE = new FieryMod();

    public FieryMod() {
        super("fiery", 0, .3, .2, -11);
    }

    public String adjective(GenericBodyPart part) {
        if (part.getType().equals("pussy")) {
            return "fiery";
        }
        if (part.getType().equals("ass")) {
            return "molten";
        }
        return "red-hot";
    }

    private JtwigTemplate tickDamage(Combat c, Character self, Character opponent, BodyPart target) {

        JtwigTemplate template;
        if (target.moddedPartCountsAs(CockMod.primal)) {
            template = JtwigTemplate.inlineTemplate(
                "The intense heat emanating from {{ self.nameOrPossessivePronoun() }} "
                    + "{{ part.describe(self) }} only serves to enflame "
                    + "{{ opponent.nameOrPossessivePronoun() }} primal passion.");
            opponent.buildMojo(c, 7);
        } else if (target.moddedPartCountsAs(CockMod.bionic)) {
            template = JtwigTemplate.inlineTemplate(
                "The heat emanating from {{ self.nameOrPossessivePronoun() }} "
                    + "{{ part.describe(self) }} is extremely hazardous for "
                    + "{{ opponent.nameOrPossessivePronoun() }} {{ target.describe(opponent) }}, "
                    + "nearly burning through its circuitry and definitely causing intense pain.");
            opponent.pain(c, self, Math.max(30, 20 + self.get(Attribute.Ki)));
        } else {
            template = JtwigTemplate.inlineTemplate(
                "Plunging {{ opponent.possessiveAdjective() }} {{ target.describe(opponent) }} "
                    + "into {{ self.possessiveAdjective() }} {{ part.describe(self) }} leaves "
                    + "{{ opponent.directObject() }} gasping from the heat.");
            opponent.pain(c, self, 20 + self.get(Attribute.Ki) / 2);
        }
        return template;
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { 
        if (target.isType("strapon")) {
            return 0;
        }
        double strength = 0;
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        JtwigTemplate template;
        if (opponent.stunned()) {
            template = JtwigTemplate.inlineTemplate(
                "The intense heat emanating from {{ self.possessiveAdjective() }} " 
                    + "{{ part.getType() }} overpowers {{ opponent.possessiveAdjective() }} senses " 
                    + "now and {{ opponent.pronoun() }} cannot respond.");
            strength = 20;
        } else {
            template = tickDamage(c, self, opponent, target);
        }
        c.write(self, template.render(model));
        return strength;
    }

    public void tickHolding(Combat c, Character self, Character opponent, BodyPart part, BodyPart opponentOrgan) {
        Optional<BodyPart> targetPart = c.getStance().getPartsFor(c, opponent, self).stream().findAny();
        if (targetPart.isPresent()) {
            BodyPart target = targetPart.get();
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            JtwigTemplate template = tickDamage(c, self, opponent, target);
            c.write(self, template.render(model));
        }
    }

    public int counterValue(BodyPart part, BodyPart opponentPart, Character self, Character opponent) { 
        return opponentPart.moddedPartCountsAs(CockMod.bionic) ? 1 : opponentPart.moddedPartCountsAs(
            CockMod.primal) ? -1 : 0;
    }

    @Override
    public String describeAdjective(String partType) {
        return "molten depths";
    }
}
