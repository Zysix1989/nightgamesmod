package nightgames.modifier.item;

/**
 * TODO: Write class-level documentation.
 */
public final class ItemModifierCombiner {
    public static final ItemModifier NULL_MODIFIER = new ItemModifier() {
        private static final String name = "null-item-modifier";

        @Override public String toString() {
            return name;
        }
    };

    private ItemModifierCombiner() {
    }
}
