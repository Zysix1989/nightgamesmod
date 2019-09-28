package nightgames.characters.body.mods.pitcher;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.mods.catcher.CatcherMod;
import nightgames.characters.body.mods.catcher.DemonicMod;
import nightgames.characters.body.mods.catcher.FieryMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Abuff;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class EnlightenedCockMod extends CockMod {
    public static final String TYPE = "enlightened";
    public EnlightenedCockMod() {
        super(TYPE, 1.0, 1.2, .8);
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part,
        BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);

        var model = JtwigModel.newModel()
            .with("self", self)
            .with("part", part);
        if (target.moddedPartCountsAs(DemonicMod.TYPE)) {
            c.write(self, APPLY_BONUS_DEMONIC_TEMPLATE.render(model));
            // Actual bad effects are dealt with in PussyPart
        } else {
            c.write(self, APPLY_BONUS_TEMPLATE.render(model));
            for (int i = 0; i < Math.max(2, (self.get(Attribute.Ki) + 5) / 10); i++) { // +5
                // for
                // rounding:
                // 24->29->20,
                // 25->30->30
                Attribute attr = new Attribute[]{Attribute.Power, Attribute.Cunning,
                    Attribute.Seduction}[Global
                    .random(3)];
                self.add(c, new Abuff(self, attr, 1, 10));
            }
            self.buildMojo(c, 5);
            self.restoreWillpower(c, 1);
        }
        return bonus;
    }

    @Override
    public String describeAdjective(String partType) {
            return "imposing presence";
    }

    @Override
    public CatcherMod getCorrespondingCatcherMod() {
        return new FieryMod();
    }

    private static final JtwigTemplate APPLY_BONUS_DEMONIC_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Almost instinctively, {{ self.subject() }} {{ self.action('focus') }} "
            + "{{ self.possessiveAdjective() }} entire being into "
            + "{{ self.possessiveAdjective() }} {{ part.describe(self) }}.  This "
            + "would normally be a good thing, but whilst fucking a succubus it is very, "
            + "very bad indeed."
    );

    private static final JtwigTemplate APPLY_BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Drawing upon {{ self.possessiveAdjective() }} extensive training, "
            + "{{ self.subject() }} {{ self.action('concentrate') }} "
            + "{{ self.possessiveAdjective() }} will into {{ self.possessiveAdjective() }}"
            + "{{ part.describe(self) }}, enhancing {{ self.possessiveAdjective() }} own "
            + "abilities."
    );
}