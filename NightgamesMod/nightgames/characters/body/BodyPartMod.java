package nightgames.characters.body;

public interface BodyPartMod {
    String getModType();

    default boolean countsAs(String type) {
        return getModType().equals(type);
    }
}
