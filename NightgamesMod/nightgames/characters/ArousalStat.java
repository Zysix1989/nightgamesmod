package nightgames.characters;

import java.util.List;
import org.apache.commons.lang3.Range;

public class ArousalStat extends CoreStat {
    public ArousalStat(int max) {
        super(max);
    }

    private ArousalStat(ArousalStat original) {
        super(original);
    }

    @Override
    public ArousalStat copy() {
        return new ArousalStat(this);
    }

    public void pleasure(int i) {
        current += i;
    }

    public void calm(int i) { current = Math.max(current - i, 0); }

    public void renew() {
        current = 0;
    }

    public boolean isAtUnfavorableExtreme() {
        return current >= max();
    }

    @Override
    public int percent() {
        return Math.min(super.percent(), 100);
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
