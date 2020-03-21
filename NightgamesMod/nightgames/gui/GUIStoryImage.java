package nightgames.gui;

import nightgames.Resources.ResourceLoader;
import nightgames.global.Flag;
import nightgames.global.Global;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    void displayImage(String path) {
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
        }
    }

    void clearImage() {
        label.setIcon(null);
    }
}