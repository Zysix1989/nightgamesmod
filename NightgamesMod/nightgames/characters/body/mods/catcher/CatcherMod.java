package nightgames.characters.body.mods.catcher;

import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.pitcher.CockMod;

public abstract class CatcherMod extends PartMod {

    CatcherMod(String modType, double hotness, double pleasure, double sensitivity) {
        super(modType, hotness, pleasure, sensitivity);
    }

    public abstract CockMod getCorrespondingCockMod();
}
