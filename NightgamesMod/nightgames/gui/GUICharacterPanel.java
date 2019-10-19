package nightgames.gui;

import javax.swing.JPanel;
import nightgames.characters.NPC;

class GUICharacterPanel extends JPanel {

    private GUIAppearancePanel appearancePanel;

    GUICharacterPanel() {
        appearancePanel = new GUIAppearancePanel();

        setOpaque(false);
        add(appearancePanel);
    }

    void clearPortrait() {
        appearancePanel.clearPortrait();
    }

    void loadPortrait(NPC enemy) {
        appearancePanel.loadPortrait(enemy);
    }
}
