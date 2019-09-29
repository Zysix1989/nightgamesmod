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
    public static final List<CockMod> ALL_MODS = Arrays.asList(slimy, runic, blessed, incubus, primal, bionic, enlightened);

    protected CockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity);
    }

    public static Optional<CockMod> getFromType(String type) {
        return ALL_MODS.stream().filter(mod -> mod.getModType().equals(type)).findAny();
    }

    @Override
    public String describeAdjective(String partType) {
        if (this.equals(bionic)) {
            return "bionic implants";
        } else
        return "weirdness (ERROR)";
    }

    public abstract CatcherMod getCorrespondingCatcherMod();
}