package nightgames.characters.body;

import nightgames.characters.Character;

public interface BodyPartMod {
    String getModType();

    default boolean countsAs(Character self, BodyPartMod part) {
        return getModType().equals(part.getModType());
    }
}
