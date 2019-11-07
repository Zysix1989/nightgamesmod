package nightgames.characters;

import nightgames.characters.corestats.ArousalStat;
import nightgames.characters.corestats.StaminaStat;
import nightgames.characters.corestats.WillpowerStat;

public class CoreStatsGrowth {
    CoreStatGrowth<StaminaStat> stamina = new CoreStatGrowth<>();
    CoreStatGrowth<ArousalStat> arousal = new CoreStatGrowth<>();
    CoreStatGrowth<WillpowerStat> willpower = new CoreStatGrowth<>();

    private CoreStatsGrowth() {};

    static CoreStatsGrowth newDefault() {
        var g = new CoreStatsGrowth();
        g.stamina.baseIncrease = 2;
        g.arousal.baseIncrease = 4;
        g.willpower.baseIncrease = 1.0f;
        return g;
    }
}
