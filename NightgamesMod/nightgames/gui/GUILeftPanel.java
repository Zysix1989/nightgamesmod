package nightgames.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import nightgames.Resources.ResourceLoader;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.combat.Combat;
import nightgames.global.*;

class GUILeftPanel {

    private JPanel portraitPanel;
    private JComponent map;
    private JLabel portrait;

    private final static String USE_PORTRAIT = "PORTRAIT";
    private final static String USE_MAP = "MAP";
    private final static String USE_NONE = "NONE";


    GUILeftPanel() {
        portraitPanel = new JPanel();

        portraitPanel.setLayout(new ShrinkingCardLayout());

        portraitPanel.setBackground(GUIColors.bgDark);
        portrait = new JLabel("");
        portrait.setVerticalAlignment(SwingConstants.TOP);
        portraitPanel.add(portrait, USE_PORTRAIT);

        map = new MapComponent();
        portraitPanel.add(map, USE_MAP);
        portraitPanel.add(Box.createGlue(), USE_NONE);
    }

    void clearPortrait() {
        portrait.setIcon(null);
    }

    void loadPortrait(Combat c, NPC enemy) {
        clearPortrait();

        if (Global.checkFlag(Flag.noportraits)) {
            return;
        }
        String imagePath = enemy.getPortrait(c);
        if (imagePath == null) {
            return;
        }
        File imageFile = new File("assets/" + imagePath);
        try {
            BufferedImage face = ImageIO.read(imageFile);

            int height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
            int width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);
            if (width > 720) {
                portrait.setIcon(new ImageIcon(face));
                portrait.setVerticalAlignment(SwingConstants.TOP);
            } else {
                Image scaledFace = face.getScaledInstance(width / 6,
                    height / 4,
                    Image.SCALE_SMOOTH);
                portrait.setIcon(new ImageIcon(scaledFace));
                portrait.setVerticalAlignment(SwingConstants.TOP);
                System.out.println("Portrait resizing active.");
            }
        } catch (IOException e) {
            System.out.println(String.format("Error loading %s", "assets/" + imagePath));
            e.printStackTrace();
        }
    }

    void showMap() {
        if (Global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Show map");
        }
        map.setPreferredSize(new Dimension(300, 385));
        CardLayout portraitLayout = (CardLayout) (portraitPanel.getLayout());
        portraitLayout.show(portraitPanel, USE_MAP);
    }

    JPanel getPanel() {
        return portraitPanel;
    }

    void refresh() {
        if (map != null) {
            map.repaint();
        }
    }
}