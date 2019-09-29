package nightgames.characters.body.mods.pitcher;

import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.catcher.CatcherMod;

public abstract class CockMod extends PartMod {

    protected CockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity);
    }

    @Override
    public abstract String describeAdjective(String partType);

    public abstract CatcherMod getCorrespondingCatcherMod();
}