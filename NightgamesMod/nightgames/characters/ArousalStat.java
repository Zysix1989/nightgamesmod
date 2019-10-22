package nightgames.characters;

public class ArousalStat extends Meter {
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
}
