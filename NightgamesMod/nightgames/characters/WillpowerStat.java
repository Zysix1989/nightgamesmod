package nightgames.characters;

public class WillpowerStat extends Meter {
    public WillpowerStat(int max) {
        super(max);
    }

    private WillpowerStat(WillpowerStat original) {
        super(original);
    }

    @Override
    public WillpowerStat copy() {
        return new WillpowerStat(this);
    }
}
