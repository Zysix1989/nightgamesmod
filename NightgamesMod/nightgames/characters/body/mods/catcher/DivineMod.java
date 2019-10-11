package nightgames.characters.body.mods.catcher;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.AssPart;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.PussyPart;
import nightgames.characters.body.mods.pitcher.BlessedCockMod;
import nightgames.characters.body.mods.pitcher.CockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.DivineCharge;
import nightgames.status.Stsflag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class DivineMod extends CatcherMod {
    public static final String TYPE = "divine";

    public DivineMod() {
        super(TYPE, 0, 1.0, 0.0);
    }

    public String adjective(GenericBodyPart part) {
        if (part.getType().equals(PussyPart.TYPE)) {
            return "divine";
        }
        if (part.getType().equals(AssPart.TYPE)) {
            return "sacred";
        }
        return "holy";
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        if (self.getStatus(Stsflag.divinecharge) != null) {
            JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
            c.write(self, APPLY_BONUS_TEMPLATE.render(model));
        }
        // no need for any effects, the bonus is in the pleasure attributeModifier
        return 0;
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        DivineCharge charge = (DivineCharge) self.getStatus(Stsflag.divinecharge);
        JtwigModel model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("part", part)
            .with("target", target);
        JtwigTemplate template;
        if (charge == null) {
            template = RECEIVE_NO_CHARGE_TEMPLATE;
            self.add(c, new DivineCharge(self, .25));
        } else {
            template = RECEIVE_CHARGE_TEMPLATE;
            self.add(c, new DivineCharge(self, charge.magnitude));
        }
        c.write(self, template.render(model));
        return 0;
    }

    @Override
    public void receiveCum(Combat c, Character self, BodyPart part, Character opponent, BodyPart target) {
        if (self.has(Trait.zealinspiring) && Global.random(4) > -10) {
            
            if (c.getStance().partsForStanceOnly(c, self, opponent).contains(part) && c.getStance().partsForStanceOnly(c, opponent, self).stream().anyMatch(otherPart -> otherPart.isType(
                CockPart.TYPE))) {
                JtwigModel model = JtwigModel.newModel()
                    .with("self", self)
                    .with("opponent", opponent)
                    .with("part", part)
                    .with("target", target);
                c.write(self, RECEIVE_CUM_TEMPLATE.render(model));
                opponent.addict(c, AddictionType.ZEAL, self, Addiction.MED_INCREASE);
            }
        }
    }

    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
        JtwigModel model = JtwigModel.newModel()
                .with("self", self)
                .with("opponent", opponent)
                .with("part", part)
                .with("target", target)
                .with("dom", c.getStance().dom(self));
        c.write(self, ON_PENETRATION_TEMPLATE.render(model));
    }

    @Override
    public double modPleasure(Character self) {
        DivineCharge charge = (DivineCharge) self.getStatus(Stsflag.divinecharge);
        double pleasureMod = super.modPleasure(self);
        if (charge != null) {
            pleasureMod += charge.magnitude;
        }
        return pleasureMod;
    }

    @Override
    public String describeAdjective(String partType) {
        return "divine aura";
    }

    private static final JtwigTemplate APPLY_BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} concentrated divine energy in "
            + "{{ self.possessiveAdjective() }} {{ part.getType() }} "
            + "seeps into {{ opponent.nameOrPossessivePronoun() }} {{ target.getType() }}, "
            + "sending unimaginable pleasure directly into "
            + "{{ opponent.possessiveAdjective() }} soul."
    );

    private static final JtwigTemplate RECEIVE_NO_CHARGE_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.nameOrPossessivePronoun() }} {{ part.fullDescribe(self) }} "
            + "radiates a golden glow when {{ self.pronoun() }} {{ self.action('moan') }}. "
            + "{% if (self != opponent) %} "
            + "{{ opponent.pronoun() }} {{ opponent.action('realize') }} "
            + "{% endif %}"
            + "{{ self.pronoun() }} {{ self.action('are', 'is' }} "
            + "feeding on {{ self.possessiveAdjective() }} own pleasure to charge up "
            + "{{ self.possessiveAdjective() }} divine energy.");
    private static final JtwigTemplate RECEIVE_CHARGE_TEMPLATE = JtwigTemplate.inlineTemplate(
        "{{ self.subject() }} {{ self.action('continue') }} feeding on "
            + "{{ self.possessiveAdjective() }} own pleasure to charge up "
            + "{{ self.possessiveAdjective() }} divine energy.");

    private static final JtwigTemplate RECEIVE_CUM_TEMPLATE = JtwigTemplate.inlineTemplate(
        "As {{ opponent.possessiveAdjective() }} cum floods "
            + "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }}, "
            + "{opponent:subject-action:are} assaulted by a feeling of inner peace "
            + "and belonging. The soothing emotions washes over {{ opponent.pronoun() }}, "
            + "before settling in as an unadulterated adoration for "
            + "{{ opponent.possessiveAdjective() }} divine partner.");

    private static final JtwigTemplate ON_PENETRATION_TEMPLATE = JtwigTemplate.inlineTemplate(
        "<b>As soon as you penetrate {{ self.nameDirectObject() }}, you realize it "
            + "was a bad idea. While it looks innocuous enough, "
            + "{{ self.possessiveAdjective() }} {{ part.getType() }} "
            + "feels like pure ecstasy. {{ opponent.nameOrPossessivePronoun }} not sure why "
            + "{{ opponent.subject() }} thought fucking a bonafide sex goddess was a good "
            + "idea. {{ self.subject() }} {{ self.action('are', 'is' }}n't even moving "
            + "yet, but {{ self.possessiveAdjective() }} {{ part.describe(self) }} massages "
            + "{{ opponent.possessiveAdjective() }} {{ target.getType() }}, bringing you "
            + "waves of pleasure.</b>");

    @Override
    public CockMod getCorrespondingCockMod() {
        return new BlessedCockMod();
    }
}