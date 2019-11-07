package nightgames.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import nightgames.characters.NPC;

class GUICharacterPanel extends JPanel {

    private GUIAppearancePanel appearancePanel;
    private GUICoreStatsBar metersPanel;

    GUICharacterPanel() {
        appearancePanel = new GUIAppearancePanel();
        metersPanel = new GUICoreStatsBar();

        setOpaque(false);

        var layout = new GroupLayout(this);
        layout.setAutoCreateGaps(false);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.CENTER)
            .addComponent(appearancePanel)
            .addComponent(metersPanel)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(appearancePanel)
            .addComponent(metersPanel)
        );
        setLayout(layout);
    }

    void clearPortrait() {
        appearancePanel.clearPortrait();
        metersPanel.clear();
    }

    void loadPortrait(NPC enemy) {
        appearancePanel.loadPortrait(enemy);
        metersPanel.setTarget(enemy);
    }

    void refresh() {
        metersPanel.refresh();
    }
}
