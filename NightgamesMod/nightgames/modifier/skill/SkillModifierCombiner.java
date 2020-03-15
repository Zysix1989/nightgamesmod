package nightgames.modifier.skill;

/**
 * TODO: Write class-level documentation.
 */
public final class SkillModifierCombiner {
    public static final SkillModifier NULL_MODIFIER = new SkillModifier() {
        private static final String name = "null-skill-modifier";

        @Override public String toString() {
            return name;
        }
    };

    private SkillModifierCombiner() {
    }
}
