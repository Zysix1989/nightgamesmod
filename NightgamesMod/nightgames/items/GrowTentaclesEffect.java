package nightgames.items;

import java.util.concurrent.atomic.AtomicBoolean;
import nightgames.characters.Character;
import nightgames.characters.body.mods.ExternalTentaclesMod;
import nightgames.combat.Combat;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class GrowTentaclesEffect extends ItemEffect {
    int selfDuration;

    public GrowTentaclesEffect(String verb, String otherverb) {
        this(verb, otherverb, -1);
    }

    public GrowTentaclesEffect(String verb, String otherverb, int duration) {
        super(verb, otherverb, true, true);
        selfDuration = duration;
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        int duration = selfDuration >= 0 ? selfDuration : item.duration;
        var targetPart = ExternalTentaclesMod.newTemporaryModForValidPart(user.body, duration);

        AtomicBoolean res = new AtomicBoolean();
        targetPart.ifPresentOrElse(part -> {
            var b = new StringBuilder();
            part.describeLong(b, user);
            c.write(b.toString());
            res.set(true);
        }, () -> {
            var model = JtwigModel.newModel()
                .with("self", user);
            c.write(NOTHING_TO_DO.render(model));
            res.set(false);
        });
        return res.get();
    }

    private static final JtwigTemplate NOTHING_TO_DO = JtwigTemplate.inlineTemplate(
        "{{ self.subject() }} is already covered in tentacles.  {{ self.pronoun() }} can't grow "
            + "any more!"
    );
}
