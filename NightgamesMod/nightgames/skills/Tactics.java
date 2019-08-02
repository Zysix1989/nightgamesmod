package nightgames.skills;

import java.awt.Color;

public enum Tactics {
    damage(new Color(150, 0, 0)),
    pleasure(Color.PINK),
    fucking(new Color(255, 100, 200)),
    positioning(new Color(0, 100, 0)),
    stripping(new Color(0, 100, 0)),
    recovery( Color.WHITE),
    calming(Color.WHITE),
    debuff(Color.CYAN),
    summoning(Color.YELLOW),
    misc(new Color(200, 200, 200));

    private final Color color;
    Tactics(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}
