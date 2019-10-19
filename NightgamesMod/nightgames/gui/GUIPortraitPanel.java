package nightgames.gui;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import nightgames.characters.NPC;
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

    void loadPortrait(NPC source) {
        clearPortrait();

        if (Global.checkFlag(Flag.noportraits)) {
            return;
        }
        String imagePath = source.getPortrait();
        if (imagePath == null) {
            return;
        }
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream(imagePath);
        if (imageStream == null) {
            System.out.println(String.format("Missing image: %s", imagePath));
            return;
        }
        try {
            Image face = ImageIO.read(imageStream);
            image.setIcon(new ImageIcon(face));
            image.setVerticalAlignment(SwingConstants.TOP);
        } catch (IOException e) {
            System.out.println(String.format("Error loading %s", imagePath));
            e.printStackTrace();
        }
    }

    void clearPortrait() {
        image.setIcon(null);
    }
}
