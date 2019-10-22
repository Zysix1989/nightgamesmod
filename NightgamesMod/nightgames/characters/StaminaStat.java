package nightgames.characters;

public class StaminaStat extends Meter {

    public StaminaStat(int max) {
        super(max);
    }

    private StaminaStat(StaminaStat original) {
        super(original);
    }

    @Override
    public StaminaStat copy() {
        return new StaminaStat(this);
    }
}
