package nightgames.gui;

import java.awt.Image;
import java.io.IOException;
import java.util.MissingResourceException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class GUIMeterPanel extends JPanel {
    private JLabel thumbnail;

    GUIMeterPanel(String imagePath) {
        thumbnail = new JLabel();
        var imageStream = this.getClass().getClassLoader().getResourceAsStream(imagePath);
        if (imageStream == null) {
            throw new MissingResourceException("", this.getClass().getName(), imagePath);
        }
        try {
            Image thumbnailImage = ImageIO.read(imageStream);
            thumbnail.setVerticalAlignment(SwingConstants.TOP);
            thumbnail.setOpaque(false);
            thumbnail.setIcon(new ImageIcon(thumbnailImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        setOpaque(false);
        add(thumbnail);
    }
}
