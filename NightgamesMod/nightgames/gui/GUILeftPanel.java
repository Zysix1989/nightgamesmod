package nightgames.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import nightgames.characters.NPC;
import nightgames.combat.Combat;

class GUILeftPanel {

    private JPanel leftPanel;
    private GUICharacterPanel characterPanel;
    private CardLayout layout;
    private JComponent map;

    private final static String USE_PORTRAIT = "PORTRAIT";
    private final static String USE_MAP = "MAP";
    private final static String USE_NONE = "NONE";

    GUILeftPanel() {
        leftPanel = new JPanel();
        layout = new CardLayout();
        leftPanel.setLayout(layout);
        leftPanel.setBackground(GUIColors.bgDark);

        characterPanel = new GUICharacterPanel();
        leftPanel.add(characterPanel, USE_PORTRAIT);

        map = new MapComponent();
        leftPanel.add(map, USE_MAP);
        leftPanel.add(Box.createGlue(), USE_NONE);
    }

    void clearPortrait() {
        characterPanel.clearPortrait();
    }

    void loadPortrait(Combat c, NPC enemy) {
        characterPanel.loadPortrait(c, enemy);
    }

    void showPortrait() {
        layout.show(leftPanel, USE_PORTRAIT);
    }

    void showMap() {
        map.setPreferredSize(new Dimension(300, 385));
        layout.show(leftPanel, USE_MAP);
    }

    void showNone() {
        layout.show(leftPanel, USE_NONE);
    }

    JPanel getPanel() {
        return leftPanel;
    }

    void refresh() {
        if (map != null) {
            map.repaint();
        }
    }
}