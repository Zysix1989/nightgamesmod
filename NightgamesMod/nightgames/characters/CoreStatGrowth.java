package nightgames.characters;

import nightgames.characters.corestats.CoreStat;

class CoreStatGrowth<T extends CoreStat> {
    float baseIncrease;

    void levelUp(T stat) {
       stat.gain(baseIncrease);
    }
}
