package nightgames.daytime;

import java.util.function.Function;

import nightgames.characters.Character;
import nightgames.characters.body.Body;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.mods.PartMod;
import nightgames.requirements.RequirementShortcuts;

public class ApplyPartModOption extends TransformationOption {
    public static Function<Character, Integer> createCostForNumberOfMods(String type) {
        return (c) -> {
            BodyPart part = c.body.getRandom(type);
            long nMods = 0;
            if (part != null) {
                nMods = part.getMods().size();
            }
            return 1600 * (int)Math.pow(5, nMods);
        };
    }

    public ApplyPartModOption(String type, PartMod mod) {
        super();
        moneyCost = createCostForNumberOfMods(type);
        addRequirement(RequirementShortcuts.bodypart(type), "Have " + Body.partArticle(type) + " " + type);
        addRequirement(RequirementShortcuts.noPartmod(type, mod), "Mod not applied");
        effect = (c, self, other) -> {
            GenericBodyPart target = (GenericBodyPart) self.body.getRandom(type);
            target.addMod(mod);
            return true;
        };
    }
}
