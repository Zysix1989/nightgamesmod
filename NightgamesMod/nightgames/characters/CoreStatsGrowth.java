package nightgames.characters;

import nightgames.characters.corestats.ArousalStat;
import nightgames.characters.corestats.StaminaStat;
import nightgames.characters.corestats.WillpowerStat;

public class CoreStatsGrowth {
    CoreStatGrowth<StaminaStat> stamina = new CoreStatGrowth<>();
    CoreStatGrowth<ArousalStat> arousal = new CoreStatGrowth<>();
    CoreStatGrowth<WillpowerStat> willpower = new CoreStatGrowth<>();
}
