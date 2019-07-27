package nightgames.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import nightgames.characters.NPC;
import nightgames.combat.Combat;
import nightgames.global.Flag;
import nightgames.global.Global;

class GUIPortraitPanel extends JPanel {

    private JLabel image;

    GUIPortraitPanel() {
        setBackground(GUIColors.bgDark);

        image = new JLabel();
        image.setVerticalAlignment(SwingConstants.TOP);

        add(image);
    }

    void loadPortrait(Combat c, NPC source) {
        System.out.println("loading portrait");
        clearPortrait();

        if (Global.checkFlag(Flag.noportraits)) {
            return;
        }
        String imagePath = source.getPortrait(c);
        if (imagePath == null) {
            return;
        }
        File imageFile = new File("assets/" + imagePath);
        try {
            Image face = ImageIO.read(imageFile);
            image.setIcon(new ImageIcon(face));
            image.setVerticalAlignment(SwingConstants.TOP);
        } catch (IOException e) {
            System.out.println(String.format("Error loading %s", "assets/" + imagePath));
            e.printStackTrace();
        }
    }

    void clearPortrait() {
        image.setIcon(null);
    }
}
