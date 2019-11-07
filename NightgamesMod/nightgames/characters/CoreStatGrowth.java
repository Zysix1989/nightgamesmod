package nightgames.characters;

import nightgames.characters.corestats.CoreStat;

class CoreStatGrowth<T extends CoreStat> {
    float baseIncrease;

    CoreStatGrowth(float baseIncrease) {
        this.baseIncrease = baseIncrease;
    }

    void levelUp(T stat) {
       stat.gain(baseIncrease);
    }
}
