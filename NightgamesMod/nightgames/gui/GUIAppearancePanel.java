package nightgames.gui;

import javax.swing.JPanel;
import nightgames.characters.NPC;
import nightgames.combat.Combat;

class GUIAppearancePanel extends JPanel {

    private GUIPortraitPanel portraitPanel;

    GUIAppearancePanel() {
        portraitPanel = new GUIPortraitPanel();
        setBackground(null);
        add(portraitPanel);
    }

    void clearPortrait() {
        portraitPanel.clearPortrait();
    }

    void loadPortrait(Combat c, NPC enemy) {
        portraitPanel.loadPortrait(c, enemy);
    }
}
