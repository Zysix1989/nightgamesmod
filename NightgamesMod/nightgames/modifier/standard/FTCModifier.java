package nightgames.modifier.standard;

import nightgames.characters.Character;
import nightgames.modifier.BaseModifier;
import nightgames.modifier.clothing.NudeModifier;
import nightgames.modifier.item.FlagOnlyModifier;

public class FTCModifier extends BaseModifier {

    public static final String NAME = "ftc";
    private final NudeModifier nudeMod;
    private final Character prey;

    public FTCModifier(Character prey) {
        nudeMod = new NudeModifier();
        this.items = new FlagOnlyModifier();
        this.prey = prey;
    }

    @Override
    public int bonus() {
        return prey.human() ? 200 : 0;
    }

    @Override
    public void handleOutfit(Character c) {
        if (c.equals(prey)) {
            nudeMod.apply(c.outfit);
        }
    }

    @Override
    public String name() {
        return NAME;
    }

    public Character getPrey() {
        return prey;
    }

    // NOTE: scenes are in nightgames.ftc.FTCPrematch
    // This Modifier should not be in Global.modifierPool

    @Override
    public String intro() {
        return null;
    }

    @Override
    public String acceptance() {
        return null;
    }

}
