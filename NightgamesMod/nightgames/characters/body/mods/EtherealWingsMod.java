package nightgames.characters.body.mods;

public class EtherealWingsMod extends AngelicWingsMod {
    public static final String TYPE = "ethereal wings mod";

    public EtherealWingsMod() {
        super(TYPE);
    }

    @Override
    public String describeAdjective(String partType) {
        return "ghostly, ethereal";
    }
}
