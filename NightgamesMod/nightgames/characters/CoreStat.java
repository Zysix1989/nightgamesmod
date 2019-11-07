package nightgames.characters;

import java.io.Serializable;
import nightgames.global.Global;
import org.apache.commons.lang3.Range;

public abstract class CoreStat implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2L;

    protected int current;
    protected int max;

    public CoreStat(int max) {
        this.max = max;
        current = 0;
    }

    protected CoreStat(CoreStat original) {
        current = original.current;
        max = original.max;
    }

    public int get() {
        return Math.min(current, max());
    }

    public int getReal() {
        return current;
    }

    public int getOverflow() {
        return Math.max(0, current - max());
    }

    public int max() {
        return max;
    }

    public void gain(float i) {
        max += i;
        if (current > max()) {
            current = max();
        }
    }

    public void setMax(int i) {
        max = i;
        current = max();
    }

    public int percent() {
        return Math.min(100, 100 * current / max());
    }

    @Override
    public String toString() {
        return String.format("current: %s / max: %s", Global.formatDecimal(current), Global.formatDecimal(max()));
    }

    public double remaining() {
        return max() - getReal();
    }

    public abstract CoreStat copy();

    public abstract Range<Integer> observe(int perception);
}
