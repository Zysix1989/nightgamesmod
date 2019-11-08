package nightgames.global;

import nightgames.characters.Character;
/**
 * Some static mechanic configuration. This should probably go in a json somewhere, but for now, it'll be here.
 */
public class Configuration {
    public static int getMaximumStaminaPossible(Character c) {
        return 100 + c.getLevel() * 5;
    }
    public static int getMaximumArousalPossible(Character c) {
        return 100 + c.getLevel() * 6;
    }
}
