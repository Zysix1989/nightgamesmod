package nightgames.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import nightgames.characters.Player;
import nightgames.items.Item;

class GUIPlayerInventory {

    private Player player;
    private JButton button;
    private JFrame frame;
    private JPanel panel;

    GUIPlayerInventory(Player player) {
        this.player = player;

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 5));
        panel.setSize(new Dimension(400, 800));
        panel.setBackground(GUIColors.bgDark);

        frame = new JFrame("Inventory");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(false);
        frame.setLocationByPlatform(true);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(800, 100));
        frame.getContentPane().add(BorderLayout.CENTER, panel);


        button = new JButton("Inventory");
        button.addActionListener(arg0 -> {
            toggleVisibility();
        });
    }

    private void toggleVisibility() {
        EventQueue.invokeLater(() -> {
            frame.setVisible(!frame.isVisible());
        });
    }

    void refresh() {
        List<Item> availItems = player.getInventory().entrySet().stream().filter(entry -> (entry.getValue() > 0))
            .map(Map.Entry::getKey).collect(Collectors.toList());

        Map<Item, Integer> items = player.getInventory();

        panel.removeAll();

        for (Item i : availItems) {
            JLabel label = new JLabel(i.getName() + ": " + items.get(i) + "\n");
            label.setForeground(GUIColors.textColorLight);
            label.setToolTipText(i.getDesc());
            panel.add(label);
        }
        frame.pack();
    }

    JButton getButton() {
        return button;
    }
}