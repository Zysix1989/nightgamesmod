package nightgames.modifier.action;

/**
 * TODO: Write class-level documentation.
 */
public final class ActionModifierCombiner {
    public static final ActionModifier NULL_MODIFIER = new ActionModifier() {

        @Override public String toString() {
            return "null-action-modifier";
        }

    };

    private ActionModifierCombiner() {
    }
}
