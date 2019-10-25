package nightgames.characters;

import java.util.List;
import org.apache.commons.lang3.Range;

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

    float trueMax() {
        return max;
    }

    @Override
    public Range<Integer> observe(int perception) {
        var percentage = percent();
        if (perception >= 9) {
            return Range.is(percentage);
        }
        if (perception >= 7) {
            for (var i : List.of(
                Range.between(0, 24),
                Range.between(25, 49),
                Range.between(50, 74),
                Range.between(75, 100))) {
                if (i.contains(percentage)) {
                    return i;
                }
            }
            throw new IllegalStateException(String.format("percentage %s not between 0 and 100", percentage));
        }
        if (perception >= 3) {
            for (var i : List.of(
                Range.between(0, 49),
                Range.between(50, 100))) {
                if (i.contains(percentage)) {
                    return i;
                }
            }
            throw new IllegalStateException(String.format("percentage %s not between 0 and 100", percentage));
        }
        return Range.between(0, 100);
    }
}
