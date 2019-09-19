package nightgames.characters.body;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.body.mods.BionicCockMod;
import nightgames.characters.body.mods.BlessedCockMod;
import nightgames.characters.body.mods.EnlightenedCockMod;
import nightgames.characters.body.mods.IncubusCockMod;
import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.PrimalCockMod;
import nightgames.characters.body.mods.RunicCockMod;
import nightgames.characters.body.mods.SlimyCockMod;
import nightgames.combat.Combat;

public class CockMod extends PartMod {
    public static final CockMod error = new CockMod("error", 1.0, 1.0, 1.0);
    public static final CockMod slimy = new SlimyCockMod("slimy", .5, 1.5, .7);
    public static final CockMod runic= new RunicCockMod("runic", 2.0, 1.0, 1.0);
    public static final CockMod blessed = new BlessedCockMod("blessed", 1.0, 1.0, .75);
    public static final CockMod incubus= new IncubusCockMod("incubus", 1.25, 1.3, .9);
    public static final CockMod primal = new PrimalCockMod("primal", 1.0, 1.4, 1.2);
    public static final CockMod bionic = new BionicCockMod("bionic", .8, 1.3, .5);
    public static final CockMod enlightened = new EnlightenedCockMod("enlightened", 1.0, 1.2, .8);
    public static final List<CockMod> ALL_MODS = Arrays.asList(slimy, runic, blessed, incubus, primal, bionic, enlightened);

    protected CockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity, 0);
    }

    @Override
    public void loadData(JsonElement element) {
        Optional<CockMod> other = getFromType(element.getAsString());
        other.ifPresent(otherMod -> {
            this.modType = otherMod.modType;
            this.pleasure = otherMod.pleasure;
            this.hotness = otherMod.hotness;
            this.sensitivity = otherMod.sensitivity;
        });
    }

    public JsonElement saveData() {
        return new JsonPrimitive(getModType());
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
}