package nightgames.characters;

import nightgames.characters.corestats.CoreStat;
import nightgames.global.Flag;
import nightgames.global.Global;

class CoreStatGrowth<T extends CoreStat> {
    private final float baseIncrease;
    private float hardModeBonusIncrease;

    CoreStatGrowth(float baseIncrease, float hardModeBonusIncrease) {
        this.baseIncrease = baseIncrease;
        this.hardModeBonusIncrease = hardModeBonusIncrease;
    }

    void levelUp(T stat) {
       stat.gain(baseIncrease);
        if (Global.checkFlag(Flag.hardmode)) {
            stat.gain(hardModeBonusIncrease);
        }
    }
}
