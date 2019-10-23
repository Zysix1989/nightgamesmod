package nightgames.characters;

public class WillpowerStat extends Meter {
    private int temporaryMax;

    public WillpowerStat(int max) {
        super(max);
        resetCapacity();
        renew();
    }

    private WillpowerStat(WillpowerStat original) {
        super(original);
        temporaryMax = original.temporaryMax;
    }

    @Override
    public WillpowerStat copy() {
        return new WillpowerStat(this);
    }

    public void renew() {
        current = max;
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

    public void reduceCapacity(double percentage) {
        temporaryMax = (int) ((float) max() * percentage);
    }

    public void resetCapacity() {
        temporaryMax = Integer.MAX_VALUE;
    }

    public int max() {
        return Math.min(max, temporaryMax);
    }
}
