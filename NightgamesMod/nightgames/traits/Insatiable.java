package nightgames.traits;

import nightgames.characters.corestats.ArousalStat;

public class Insatiable {

    public static void renewArousal(ArousalStat arousal) {
        arousal.renew();
        arousal.pleasure(Math.round(arousal.max() * .2f));
    }
}
