package nightgames.characters;

import com.google.gson.JsonObject;
import java.util.List;
import org.apache.commons.lang3.Range;

public class StaminaStat extends CoreStat {

    public StaminaStat(int max) {
        super(max);
        renew();
    }

    private StaminaStat(StaminaStat original) {
        super(original);
    }

    public StaminaStat(JsonObject js) { super(js); }

    @Override
    public StaminaStat copy() {
        return new StaminaStat(this);
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

    @Override
    public Range<Integer> observe(int perception) {
        var percentage = percent();
        if (perception >= 8) {
            return Range.is(percentage);
        }
        if (perception >= 6) {
            for (var i : List.of(
                Range.between(0, 33),
                Range.between(34, 66),
                Range.between(67, 100))) {
                if (i.contains(percentage)) {
                    return i;
                }
            }
            throw new IllegalStateException(String.format("percentage %s not between 0 and 100", percentage));
        }
        if (perception >= 5) {
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
