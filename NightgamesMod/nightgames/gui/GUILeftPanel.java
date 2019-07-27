package nightgames.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.global.DebugFlags;
import nightgames.global.Global;

class GUILeftPanel {

    private JPanel leftPanel;
    private GUIPortraitPanel portraitPanel;
    private JComponent map;

    private final static String USE_PORTRAIT = "PORTRAIT";
    private final static String USE_MAP = "MAP";
    private final static String USE_NONE = "NONE";


    GUILeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setLayout(new ShrinkingCardLayout());
        leftPanel.setBackground(GUIColors.bgDark);

        portraitPanel = new GUIPortraitPanel();
        leftPanel.add(portraitPanel, USE_PORTRAIT);

        map = new MapComponent();
        leftPanel.add(map, USE_MAP);
        leftPanel.add(Box.createGlue(), USE_NONE);
    }

    void clearPortrait() {
        portraitPanel.clearPortrait();
    }

    void loadPortrait(Combat c, NPC enemy) {
        portraitPanel.loadPortrait(c, enemy);
    }

    void showPortrait() {
        System.out.println("Show portrait");
        CardLayout portraitLayout = (CardLayout) (leftPanel.getLayout());
        portraitLayout.show(leftPanel, USE_PORTRAIT);
    }

    void showMap() {
        if (Global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show map");
        }
        map.setPreferredSize(new Dimension(300, 385));
        CardLayout portraitLayout = (CardLayout) (leftPanel.getLayout());
        portraitLayout.show(leftPanel, USE_MAP);
    }

    void showNone() {
        if (Global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show none");
        }
        CardLayout portraitLayout = (CardLayout) (leftPanel.getLayout());
        portraitLayout.show(leftPanel, USE_NONE);
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