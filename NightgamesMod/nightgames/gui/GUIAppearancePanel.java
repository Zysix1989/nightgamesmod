package nightgames.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import nightgames.characters.NPC;
import nightgames.combat.Combat;

class GUIAppearancePanel extends JPanel {

    private class AppearancePanelMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            toggle();
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }
    }

    private GUIPortraitPanel portraitPanel;
    private GUIAppearanceDescriptionPanel appearanceDescriptionPanel;

    GUIAppearancePanel() {
        portraitPanel = new GUIPortraitPanel();
        appearanceDescriptionPanel = new GUIAppearanceDescriptionPanel();
        setBackground(null);
        add(portraitPanel);
        add(appearanceDescriptionPanel);
        appearanceDescriptionPanel.setVisible(false);
        addMouseListener(new AppearancePanelMouseListener());
    }

    void clearPortrait() {
        portraitPanel.clearPortrait();
    }

    void loadPortrait(Combat c, NPC enemy) {
        portraitPanel.loadPortrait(c, enemy);
        appearanceDescriptionPanel.setCharacter(enemy);
    }

    private void toggle() {
        portraitPanel.setVisible(!portraitPanel.isVisible());
        appearanceDescriptionPanel.setVisible(!appearanceDescriptionPanel.isVisible());
    }
}
