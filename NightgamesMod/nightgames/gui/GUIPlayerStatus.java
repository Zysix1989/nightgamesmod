package nightgames.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import nightgames.characters.Player;

class GUIPlayerStatus {
    private ArrayList<GUIMeter> meters = new ArrayList<GUIMeter>();
    private JPanel panel;

    private static GUIMeter newStaminaMeter(Player player) {
        return new GUIMeter(
            "Stamina",
            player.getStamina(),
            new Color(164, 8, 2),
            "Stamina represents your endurance and ability to keep fighting. If it drops to zero, you'll be temporarily stunned."
        );
    }

    private static GUIMeter newArousalMeter(Player player) {
        return new GUIMeter(
            "Arousal",
            player.getArousal(),
            new Color(254, 1, 107),
            "Arousal is raised when your opponent pleasures or seduces you. If it hits your max, you'll orgasm and lose the fight."
        );
    }

    private static GUIMeter newMojoMeter(Player player) {
        return new GUIMeter(
            "Mojo",
            player.getMojo(),
            new Color(51, 153, 255),
            "Mojo is the abstract representation of your momentum and style. It increases with normal techniques and is used to power special moves"
        );
    }

    private static GUIMeter newWillpowerMeter(Player player) {
        return new GUIMeter(
            "Willpower",
            player.getWillpower(),
            new Color(68, 170, 85),
            "Willpower is a representation of your will to fight. When this reaches 0, you lose."
        );
    }

    GUIPlayerStatus(Player player) {
        GUIMeter stamina = newStaminaMeter(player);
        GUIMeter arousal = newArousalMeter(player);
        GUIMeter mojo = newMojoMeter(player);
        GUIMeter willpower = newWillpowerMeter(player);

        meters.add(stamina);
        meters.add(arousal);
        meters.add(mojo);
        meters.add(willpower);

        panel = new JPanel();
        panel.setBackground(GUIColors.bgDark);
        panel.setLayout(new GridLayout(0, meters.size(), 0, 0));

        panel.add(stamina.getPanel());
        panel.add(arousal.getPanel());
        panel.add(mojo.getPanel());
        panel.add(willpower.getPanel());
    }

    JPanel getPanel() {
        return panel;
    }

    void refresh() {
        for (GUIMeter meter : meters) {
            meter.refresh();
        }
    }
}