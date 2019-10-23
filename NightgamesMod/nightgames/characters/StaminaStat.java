package nightgames.characters;

public class StaminaStat extends Meter {

    public StaminaStat(int max) {
        super(max);
        renew();
    }

    private StaminaStat(StaminaStat original) {
        super(original);
    }

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
        current = Math.max(current - 1, 0);
    }

    public boolean isAtUnfavorableExtreme() {
        return current <= 0;
    }
}
