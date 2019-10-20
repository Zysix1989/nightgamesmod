package nightgames.gui;

import java.awt.Image;
import java.io.IOException;
import java.util.MissingResourceException;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import nightgames.characters.Meter;

class GUIMeterPanel extends JPanel {
    private Meter target = null;
    private JLabel thumbnail;
    private JLabel description;

    GUIMeterPanel(String imagePath) {
        thumbnail = new JLabel();
        description = new JLabel();;
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

        var layout = new GroupLayout(this);
        layout.setVerticalGroup(
            layout.createParallelGroup()
            .addComponent(thumbnail)
            .addComponent(description)
        );
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addComponent(thumbnail)
            .addComponent(description)
        );
        setLayout(layout);
    }

    void setTargetMeter(Meter target) {
        this.target = target;
    }

    void refresh() {
        if (target != null) {
            description.setText(Integer.toString(target.percent()));
        }
    }

    void clear() {
        description.setText("");
    }
}
