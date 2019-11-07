package nightgames.characters;

import nightgames.characters.corestats.ArousalStat;
import nightgames.characters.corestats.StaminaStat;
import nightgames.characters.corestats.WillpowerStat;

public class CoreStatsGrowth {
    public CoreStatGrowth<StaminaStat> stamina = new CoreStatGrowth<>();
    public CoreStatGrowth<ArousalStat> arousal = new CoreStatGrowth<>();
    public CoreStatGrowth<WillpowerStat> willpower = new CoreStatGrowth<>();
}
