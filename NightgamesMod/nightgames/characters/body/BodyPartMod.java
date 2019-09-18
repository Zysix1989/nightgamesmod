package nightgames.characters.body;

import nightgames.characters.Character;
import nightgames.characters.body.mods.PartMod;

public interface BodyPartMod {
    String getModType();

    default boolean countsAs(Character self, PartMod part) {
        return getModType().equals(part.getModType());
    }
}
