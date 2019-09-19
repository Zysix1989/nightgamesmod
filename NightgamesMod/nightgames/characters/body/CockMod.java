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
    public static final CockMod slimy = new SlimyCockMod();
    public static final CockMod runic= new RunicCockMod();
    public static final CockMod blessed = new BlessedCockMod();
    public static final CockMod incubus= new IncubusCockMod();
    public static final CockMod primal = new PrimalCockMod();
    public static final CockMod bionic = new BionicCockMod();
    public static final CockMod enlightened = new EnlightenedCockMod();
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