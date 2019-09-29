package nightgames.characters.body.mods.pitcher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.catcher.CatcherMod;

public abstract class CockMod extends PartMod {
    public static final CockMod slimy = new SlimyCockMod();
    public static final CockMod runic= new RunicCockMod();
    public static final CockMod blessed = new BlessedCockMod();
    public static final CockMod incubus= new IncubusCockMod();
    public static final CockMod primal = new PrimalCockMod();
    public static final CockMod bionic = new BionicCockMod();
    public static final CockMod enlightened = new EnlightenedCockMod();

    protected CockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity);
    }

    @Override
    public String describeAdjective(String partType) {
        return "weirdness (ERROR)";
    }

    public abstract CatcherMod getCorrespondingCatcherMod();
}