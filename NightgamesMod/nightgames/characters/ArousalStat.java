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

    public void renew() {
        current = 0;
    }
}
