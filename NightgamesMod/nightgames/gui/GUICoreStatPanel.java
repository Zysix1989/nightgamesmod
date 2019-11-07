package nightgames.gui;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.MissingResourceException;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.SoftBevelBorder;
import nightgames.characters.corestats.CoreStat;

class GUICoreStatPanel extends JPanel {
    private CoreStat target = null;
    private JLabel thumbnail;
    private GUICoreStatBar bar;

    GUICoreStatPanel(String imagePath, Color dominantColor) {
        thumbnail = new JLabel();
        bar = new GUICoreStatBar();
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

        bar.setStringPainted(true);
        bar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        bar.setForeground(dominantColor);
        bar.setBackground(new Color(50, 50, 50));
        bar.setIndeterminate(true);

        setOpaque(false);

        var layout = new GroupLayout(this);
        layout.setVerticalGroup(
            layout.createParallelGroup()
            .addComponent(thumbnail)
            .addComponent(bar)
        );
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
            .addComponent(thumbnail)
            .addComponent(bar)
        );
        setLayout(layout);
    }

    void setTargetMeter(CoreStat target) {
        this.target = target;
        refresh();
        bar.setVisible(true);
    }

    void refresh() {
        if (target != null) {
            bar.setString(Integer.toString(target.percent()));
            var range = target.observe(7);
            bar.setValue(range.getMinimum());
            bar.setExtent(range.getMaximum()-range.getMinimum());
        }
    }

    void clear() {
        bar.setVisible(false);
    }
}
