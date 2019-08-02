package nightgames.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.border.LineBorder;
import nightgames.global.Global;
import nightgames.skills.Tactics;

public class SwitchTacticsButton extends KeyableButton {
    private static final long serialVersionUID = -3949203523669294068L;
    private String label;
    SwitchTacticsButton(Tactics tactic, ActionListener response) {
        super(Global.capitalizeFirstLetter(tactic.name()));
        label = Global.capitalizeFirstLetter(tactic.name());
        getButton().setBorderPainted(false);
        getButton().setOpaque(true);
        getButton().setFont(new Font("Baskerville Old Face", Font.PLAIN, 14));

        getButton().setBackground(tactic.getColor());
        getButton().setMinimumSize(new Dimension(0, 20));
        getButton().setForeground(foregroundColor(tactic.getColor()));
        setBorder(new LineBorder(getButton().getBackground(), 3));

        getButton().addActionListener(response);
        setLayout(new BorderLayout());
        add(getButton());
    }

    private static Color foregroundColor(Color bgColor) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getRed(), hsb);
        if (hsb[2] < .6) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    @Override
    public String getText() {
        return label;
    }
}
