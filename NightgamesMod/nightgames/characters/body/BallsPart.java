package nightgames.characters.body;

public class BallsPart extends GenericBodyPart {
    public static final String TYPE = "balls";

    public BallsPart() {
        super("balls", 0, 1.0, 1.5, TYPE, "");
    }

    @Override
    public boolean isMultipleObjects() {
        return true;
    }
}
