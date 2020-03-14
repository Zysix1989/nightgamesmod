package nightgames.modifier.status;

/**
 * TODO: Write class-level documentation.
 */
public final class StatusModifierCombiner {
    public static final StatusModifier NULL_MODIFIER = new StatusModifier() {
        @Override public String toString() {
            return "null-status-modifier";
        }
    };

    private StatusModifierCombiner() {
    }
}
