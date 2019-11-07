package nightgames.characters.corestats;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.Range;

public class MojoStat extends CoreStat {
    public MojoStat(int max) {
        super(max);
    }

    private MojoStat(MojoStat original) {
        super(original);
    }

    public MojoStat(JsonObject js) { super(js); }

    @Override
    public MojoStat copy() {
        return new MojoStat(this);
    }

    public void renew() {
        current = 0;
    }

    public void build(int i) {
        current = Math.min(current + i, max());
    }

    public void deplete(int i) {
        current = Math.max(current - i, 0);
    }

    @Override
    public Range<Integer> observe(int perception) {
        return Range.between(0, 100);
    }
}
