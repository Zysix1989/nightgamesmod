package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.CockBound;
import nightgames.status.Stsflag;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class TentacledMod extends PartMod {
    public static final String TYPE = "tentacled";
    public static final TentacledMod INSTANCE = new TentacledMod();


    public TentacledMod() {
        super(TYPE, 0, 1, .2, 4);
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        double strength = 0;
        if (c.getStance().isPartFuckingPartInserted(c, opponent, target, self, part)) {
            JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target);
            JtwigTemplate template;
            if (!opponent.is(Stsflag.cockbound)) {
                if (!self.human()) {
                    template = APPLY_BONUS_NOT_HUMAN_TEMPLATE;
                } else {
                    template = APPLY_BONUS_HUMAN_TEMPLATE;
                }
                opponent.add(c, new CockBound(opponent, 10, self.nameOrPossessivePronoun() + " " + part.adjective() + " tentacles"));
            } else {
                if (!self.human()) {
                    template = APPLY_BONUS_COCKBOUND_NOT_HUMAN_TEMPLATE;
                } else {
                    template = APPLY_BONUS_COCKBOUND_HUMAN_TEMPLATE;
                }
                strength = 5 + Global.random(4);
            }
            c.write(self, template.render(model));
        }
        return strength;
    }

    @Override
    public String describeAdjective(String partType) {
        return "inner-tentacles";
    }

    private static final JtwigTemplate APPLY_BONUS_NOT_HUMAN_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Deep inside {{ self.nameOrPossessivePronoun() }} {{ part.getType() }}, "
            + "soft walls pulse and strain against your cock. You suddenly feel "
            + "hundreds of thin tentacles, probing like fingers, dancing over "
            + "every inch of your pole. A thicker tentacle wraps around "
            + "your cock, preventing any escape");
    private static final JtwigTemplate APPLY_BONUS_HUMAN_TEMPLATE = JtwigTemplate.inlineTemplate(
        "As {{ other.nameOrPossessivePronoun() }} cock pumps into you, "
            + "you focus your mind on your "
            + "{{ part.isType('ass') ? 'lower' : 'rear' }} entrance. You mentally "
            + "command the tentacles inside your tunnel to constrict and massage "
            + "{{ other.possessiveAdjective() }} cock. {{ opponent.getName() }} "
            + "almost starts hyperventilating from the sensations.");
    private static final JtwigTemplate APPLY_BONUS_COCKBOUND_NOT_HUMAN_TEMPLATE = JtwigTemplate.inlineTemplate(
        "As you thrust into {{ self.nameOrPossessivePronoun() }} "
            + "{{ part.getType() }}, hundreds of tentacles squirm against "
            + "the motions of your cock, making each motion feel like it will "
            + "push you over the edge.");
    private static final JtwigTemplate APPLY_BONUS_COCKBOUND_HUMAN_TEMPLATE = JtwigTemplate.inlineTemplate(
        "As {{ other.nameOrPossessivePronoun() }} cock pumps into you, your "
            + "{{ part.adjective() }} tentacles reflexively curl around "
            + "the intruding object, rhythmically squeezing to milk the hot cum "
            + "out of it.");
}
