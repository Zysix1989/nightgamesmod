package nightgames.skills;

import javafx.scene.paint.Color;

;

public enum Tactics {
    damage(new Color(.5, 0, 0, 1)),
    pleasure(Color.PINK),
    fucking(new Color(1, .33, .66, 1)),
    positioning(new Color(0, 1, 0, 1)),
    stripping(new Color(0, 1, 0, 1)),
    recovery( Color.WHITE),
    calming(Color.WHITE),
    debuff(Color.CYAN),
    summoning(Color.YELLOW),
    misc(new Color(.66, .66, .66, 1));

    private final Color color;
    Tactics(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}
