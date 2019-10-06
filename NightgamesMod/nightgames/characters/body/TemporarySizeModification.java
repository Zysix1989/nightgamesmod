package nightgames.characters.body;

class TemporarySizeModification {
    private final int modifier;
    private int duration;

    TemporarySizeModification(int modifier, int duration) {
        this.modifier = modifier;
        this.duration = duration;
    }

    void reduceDuration() {
        duration--;
    }

    boolean isExpired() {
        return duration < 0;
    }

    int getModifier() {
        return modifier;
    }
}
