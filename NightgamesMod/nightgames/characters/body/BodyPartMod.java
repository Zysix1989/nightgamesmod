package nightgames.characters.body;

import nightgames.characters.body.mods.PartMod;

public interface BodyPartMod {
    String getModType();

    default boolean countsAs(PartMod part) {
        return getModType().equals(part.getModType());
    }
}
