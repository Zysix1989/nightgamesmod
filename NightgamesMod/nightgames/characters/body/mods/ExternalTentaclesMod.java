package nightgames.characters.body.mods;

import java.util.List;
import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.AssPart;
import nightgames.characters.body.Body;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.FeetPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.HandsPart;
import nightgames.characters.body.MouthPart;
import nightgames.characters.body.PussyPart;
import nightgames.characters.body.TailPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ExternalTentaclesMod extends PartMod {
    public static final String TYPE = "external tentacles";

    private static List<String> synonyms = List.of("mass", "clump", "nest", "group");

    private static final List<String> usableBodyPartTypes = List.of(
        AssPart.TYPE,
        MouthPart.TYPE,
        PussyPart.TYPE,
        HandsPart.TYPE,
        FeetPart.TYPE,
        TailPart.TYPE,
        CockPart.TYPE
    );

    public static Optional<GenericBodyPart> newTemporaryModForValidPart(Body target, int duration) {
        var targetPart = usableBodyPartTypes.stream().
            flatMap(t -> target.get(t).stream())
            .filter(part -> part.getMods().stream().noneMatch(mod -> mod.getModType().equals(TYPE)))
            .filter(part -> part instanceof GenericBodyPart)
            .map(part -> (GenericBodyPart) part)
            .findAny();
        targetPart.ifPresent(t -> {
            assert t instanceof GenericBodyPart;
            t.addTemporaryMod(new ExternalTentaclesMod(), duration);
        });
        return targetPart;
    }

    public ExternalTentaclesMod() {
        super(TYPE, 0, 1, .2);
    }

    @Override
    public String getLongDescriptionOverride(Character self, BodyPart part, String previousDescription) {
        return "A " + Global.pickRandom(synonyms) + " of tentacles sprouts from " + previousDescription;
    }

    @Override
    public String describeAdjective(String partType) {
        return "tentacled";
    }

    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        var model = JtwigModel.newModel()
            .with("self", self)
            .with("opponent", opponent)
            .with("target", target);
        c.write(APPLY_BONUS_TEMPLATE.render(model));
        return 5;
    }

    private static final JtwigTemplate APPLY_BONUS_TEMPLATE = JtwigTemplate.inlineTemplate(
        "Additionally, {{ self.nameOrPossessivePronoun() }} tentacles take the opportunity to squirm "
            + "against {{ opponent.nameOrPossessivePronoun() }} {{ target.describe(opponent) }}."
    );
}
