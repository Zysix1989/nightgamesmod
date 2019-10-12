package nightgames.characters.body.mods;

public class FallenAngelicWingsMod extends AngelicWingsMod {
    public static final String TYPE = "fallen angel wings";

    public FallenAngelicWingsMod() {
        super(TYPE);
    }

    @Override
    public String describeAdjective(String partType) {
        return "dark, angelic";
    }
}
