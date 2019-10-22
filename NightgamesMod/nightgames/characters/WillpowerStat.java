package nightgames.characters;

public class WillpowerStat extends Meter {
    public WillpowerStat(int max) {
        super(max);
        renew();
    }

    private WillpowerStat(WillpowerStat original) {
        super(original);
    }

    @Override
    public WillpowerStat copy() {
        return new WillpowerStat(this);
    }

    public void renew() {
        current = (int) max;
    }

    public void recover(int i) {
        current = Math.min(current + i, max());
    }
}
