package nightgames.characters;

import java.io.Serializable;
import nightgames.global.Global;

public class Meter implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2L;
    protected int current;
    protected int max;

    public Meter(int max) {
        this.max = max;
        current = 0;
    }

    protected Meter(Meter original) {
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

    public float trueMax() {
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

    public Meter copy() {
        return new Meter(this);
    }
}
