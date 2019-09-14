package nightgames.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import nightgames.Resources.ResourceLoader;
import nightgames.global.*;

class GUIStoryImage {
    private JPanel panel;
    private JLabel label;

    GUIStoryImage() {
        panel = new JPanel();

        label = new JLabel();
        panel.add(label, BorderLayout.NORTH);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    JLabel getLabel() {
        return label;
    }

    JPanel getPanel() {
        return panel;
    }

    void displayImage(String path, String artist) {
        if (Global.checkFlag(Flag.noimage)) {
            return;
        }
        if (!(new File("assets/" + path).canRead())) {
            return;
        }
        BufferedImage pic = null;
        try {
            pic = ImageIO.read(ResourceLoader.getFileResourceAsStream("assets/" + path));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        clearImage();
        if (pic != null) {
            label.setIcon(new ImageIcon(pic));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setToolTipText(artist);
        }
    }

    void clearImage() {
        label.setIcon(null);
    }
}