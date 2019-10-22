package nightgames.characters;

public class MojoStat extends Meter {
    public MojoStat(int max) {
        super(max);
    }

    private MojoStat(MojoStat original) {
        super(original);
    }

    @Override
    public MojoStat copy() {
        return new MojoStat(this);
    }

    public void renew() {
        current = 0;
    }
}
