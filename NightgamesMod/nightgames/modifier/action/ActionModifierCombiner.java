package nightgames.modifier.action;

/**
 * TODO: Write class-level documentation.
 */
public final class ActionModifierCombiner {
    public static final ActionModifier NULL_MODIFIER = new ActionModifier() {
        private final String name = "null-action-modifier";

        @Override public String toString() {
            return name;
        }

        @Override public String name() {
            return name;
        }
    };

    private ActionModifierCombiner() {
    }
}
