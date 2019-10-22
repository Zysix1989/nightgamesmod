package nightgames.characters;

import java.io.Serializable;
import nightgames.global.Global;

public class Meter implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2L;
    protected int current;
    protected float max;
    protected int temporaryMax;

    public Meter(int max) {
        this.max = max;
        this.temporaryMax = Integer.MAX_VALUE;
        current = 0;
    }

    protected Meter(Meter original) {
        current = original.current;
        max = original.max;
        temporaryMax = original.temporaryMax;
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
        return (int) Math.min(max, temporaryMax);
    }

    public float trueMax() {
        return max;
    }

    public void empty() {
        current = 0;
    }

    public void fill() {
        current = Math.max(max(), current);
    }

    public void set(int i) {
        current = i;
        if (current < 0) {
            current = 0;
        }
    }

    public void gain(float i) {
        max += i;
        if (current > max()) {
            current = max();
        }
    }

    public void setMax(float i) {
        max = i;
        current = max();
    }

    public void setTemporaryMax(int i) {
        if (i <= 0) {
            i = Integer.MAX_VALUE;
        }
        temporaryMax = i;
        current = Math.min(current, max());
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
