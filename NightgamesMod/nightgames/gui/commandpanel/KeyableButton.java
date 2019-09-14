package nightgames.gui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import nightgames.global.Global;
import nightgames.skills.Tactics;
import org.apache.commons.lang3.text.WordUtils;

class KeyableButton extends JPanel {
    private static final long serialVersionUID = -2379908542190189603L;
    private final JButton button;

    KeyableButton(String text) {
        this.button = new JButton(text);
        this.setLayout(new BorderLayout());
        this.add(button);
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    private static String formatHTMLMultiline(String original) {
        String out = WordUtils
            .wrap(original.replace("<", "&lt")
                    .replace(">", "&gt"),
                Math.max(30, original.length() * 2 / 3),
                "<br/>",
                false);
        return String.format("<html><center>%s</center></html>", out);
    }

    static KeyableButton BasicButton(String text, ActionListener action) {
        text = formatHTMLMultiline(text);
        var button = new KeyableButton(text);
        var fontSize = 18;
        if (text.contains("<br/>")) {
            fontSize = 14;
        }
        button.button.setFont(new Font("Baskerville Old Face", Font.PLAIN, fontSize));
        button.button.addActionListener(action);
        return button;
    }

    private static Color textColorForBackground(Color backgroundColor) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(
            backgroundColor.getRed(),
            backgroundColor.getGreen(),
            backgroundColor.getRed(),
            hsb);
        if (hsb[2] < .6) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    static KeyableButton SwitchTacticsButton(Tactics tactic, ActionListener action) {
        var button = BasicButton(Global.capitalizeFirstLetter(tactic.name()), action);

        button.button.setBackground(tactic.getColor());
        button.button.setMinimumSize(new Dimension(0, 20));
        button.button.setForeground(textColorForBackground(tactic.getColor()));
        button.setBorder(new LineBorder(tactic.getColor(), 3));
        return button;
    }

    protected void setText(String s) {
        button.setText(s);
    }

    public JButton getButton() {
        return button;
    }
}
