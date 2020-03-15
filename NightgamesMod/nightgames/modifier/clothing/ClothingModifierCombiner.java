package nightgames.modifier.clothing;

/**
 * TODO: Write class-level documentation.
 */
public final class ClothingModifierCombiner {

    public static final ClothingModifier NULL_MODIFIER = new ClothingModifier() {
        private static final String name = "null-clothing-modifier";

        @Override public String toString() {
            return name;
        }

    };

    private ClothingModifierCombiner() {
    }
}
