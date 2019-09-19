package nightgames.characters.body.mods;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Tactics;
import nightgames.status.DivineCharge;
import nightgames.status.Stsflag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class DivineMod extends PartMod {
    public static final String TYPE = "divine";
    public static final DivineMod INSTANCE = new DivineMod();

    public DivineMod() {
        super(TYPE, 0, 1.0, 0.0, -10);
    }

    public String adjective(GenericBodyPart part) {
        if (part.getType().equals("pussy")) {
            return "divine";
        }
        if (part.getType().equals("ass")) {
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
            JtwigTemplate template = JtwigTemplate.inlineTemplate( 
                "{{ self.nameOrPossessivePronoun() }} concentrated divine energy in " 
                    + "{{ self.possessiveAdjective() }} {{ part.getType() }} " 
                    + "seeps into {{ opponent.nameOrPossessivePronoun() }} {{ target.getType() }}, "
                    + "sending unimaginable pleasure directly into " 
                    + "{{ opponent.possessiveAdjective() }} soul."
                );
            c.write(self, template.render(model));
        }
        // no need for any effects, the bonus is in the pleasure mod
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
            template = JtwigTemplate.inlineTemplate(
                "{{ self.nameOrPossessivePronoun() }} {{ part.fullDescribe(self) }} " 
                    + "radiates a golden glow when {{ self.pronoun() }} {{ self.action('moan') }}. "
                    + "{% if (self != opponent) %} "
                    + "{{ opponent.pronoun() }} {{ opponent.action('realize') }} "
                    + "{% endif %}"
                    + "{{ self.pronoun() }} {{ self.action('are', 'is' }} " 
                    + "feeding on {{ self.possessiveAdjective() }} own pleasure to charge up " 
                    + "{{ self.possessiveAdjective() }} divine energy.");
            self.add(c, new DivineCharge(self, .25));
        } else {
            template = JtwigTemplate.inlineTemplate(
                "{{ self.subject() }} {{ self.action('continue') }} feeding on " 
                    + "{{ self.possessiveAdjective() }} own pleasure to charge up " 
                    + "{{ self.possessiveAdjective() }} divine energy.");
            self.add(c, new DivineCharge(self, charge.magnitude));
        }
        c.write(self, template.render(model));
        return 0;
    }

    @Override
    public void receiveCum(Combat c, Character self, BodyPart part, Character opponent, BodyPart target) {
        if (self.has(Trait.zealinspiring) && Global.random(4) > -10) {
            
            if (c.getStance().partsForStanceOnly(c, self, opponent).contains(part) && c.getStance().partsForStanceOnly(c, opponent, self).stream().anyMatch(otherPart -> otherPart.isType("cock"))) {
                JtwigModel model = JtwigModel.newModel()
                    .with("self", self)
                    .with("opponent", opponent)
                    .with("part", part)
                    .with("target", target);
                JtwigTemplate template = JtwigTemplate.inlineTemplate(
                    "As {{ opponent.possessiveAdjective() }} cum floods "
                        + "{{ self.nameOrPossessivePronoun() }} {{ part.describe(self) }}, " 
                        + "{opponent:subject-action:are} assaulted by a feeling of inner peace "
                        + "and belonging. The soothing emotions washes over {{ opponent.pronoun() }}, "
                        + "before settling in as an unadulterated adoration for " 
                        + "{{ opponent.possessiveAdjective() }} divine partner.");
                c.write(self, template.render(model));
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
        JtwigTemplate template = JtwigTemplate.inlineTemplate(
                "<b>As soon as you penetrate {{ self.nameDirectObject() }}, you realize it "
                    + "was a bad idea. While it looks innocuous enough, "
                    + "{{ self.possessiveAdjective() }} {{ part.getType() }} "
                    + "feels like pure ecstasy. {{ opponent.nameOrPossessivePronoun }} not sure why "
                    + "{{ opponent.subject() }} thought fucking a bonafide sex goddess was a good "
                    + "idea. {{ self.subject() }} {{ self.action('are', 'is' }}n't even moving "
                    + "yet, but {{ self.possessiveAdjective() }} {{ part.describe(self) }} massages "
                    + "{{ opponent.possessiveAdjective() }} {{ target.getType() }}, bringing you "
                    + "waves of pleasure.</b>");
        c.write(self, template.render(model));
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
}