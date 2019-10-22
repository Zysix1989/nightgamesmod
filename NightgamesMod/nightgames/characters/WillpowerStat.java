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

    public void exhaust(int i) {
        current = Math.max(current - i, 0);
    }

    public boolean isAtUnfavorableExtreme() {
        return current <= 0;
    }

    public void setTemporaryMax(int i) {
        if (i <= 0) {
            i = Integer.MAX_VALUE;
        }
        temporaryMax = i;
        current = Math.min(current, max());
    }
}
