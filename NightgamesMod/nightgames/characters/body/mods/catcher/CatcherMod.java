package nightgames.characters.body.mods.catcher;

import nightgames.characters.body.mods.PartMod;

abstract class CatcherMod extends PartMod {

    CatcherMod(String modType, double hotness, double pleasure, double sensitivity,
        int sortOrder) {
        super(modType, hotness, pleasure, sensitivity, sortOrder);
    }
}
