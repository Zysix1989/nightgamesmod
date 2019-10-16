package nightgames.traits;

public class Wrassler {

    public static int inflictedPainArousalLossModifier(int staminaDamage, int arousalLossThreshold) {
        return (2 * staminaDamage - 3 * arousalLossThreshold) / 4;
    }
}
