package nightgames.characters;

import nightgames.characters.corestats.ArousalStat;
import nightgames.characters.corestats.StaminaStat;
import nightgames.characters.corestats.WillpowerStat;

public class CoreStatsGrowth {
    final CoreStatGrowth<StaminaStat> stamina;
    final CoreStatGrowth<ArousalStat> arousal;
    final CoreStatGrowth<WillpowerStat> willpower;

    CoreStatsGrowth(CoreStatGrowth<StaminaStat> stamina,
        CoreStatGrowth<ArousalStat> arousal,
        CoreStatGrowth<WillpowerStat> willpower) {
        this.stamina = stamina;
        this.arousal = arousal;
        this.willpower = willpower;
    };

    static CoreStatsGrowth newDefault() {
        var stamina = new CoreStatGrowth<StaminaStat>(2);
        var arousal = new CoreStatGrowth<ArousalStat>(4);
        var willpower = new CoreStatGrowth<WillpowerStat>(1.0f);
        return new CoreStatsGrowth(stamina, arousal, willpower);
    }
}
