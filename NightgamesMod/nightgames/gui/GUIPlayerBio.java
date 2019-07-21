package nightgames.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import nightgames.characters.Attribute;
import nightgames.characters.Player;
import nightgames.global.Global;
import nightgames.global.Time;
import nightgames.items.Item;

class GUIPlayerBio {
    private static JLabel labelForString(String s) {
            JLabel l = new JLabel(s);
            l.setHorizontalAlignment(SwingConstants.LEFT);
            l.setFont(new Font("Sylfaen", 1, 15));
            l.setForeground(GUIColors.textColorLight);
            return l;
        }

    private JPanel panel;
    private Player player;
    private JLabel name;
    private JLabel level;
    private JLabel xp;
    private JFrame inventory;
    private JLabel location;
    private JLabel time;
    private JLabel cash;
    private JPanel statusPanel;
    private JPanel statusTarget;

    GUIPlayerBio(Player player, JPanel statusTarget, GUI refreshTarget) {
        this.player = player;

        this.statusTarget = statusTarget;

        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 0, 0, 0));
        panel.setBackground(GUIColors.bgDark);

        name = labelForString(player.getTrueName());
        panel.add(name);

        level = labelForString("Lvl: " + player.getLevel());
        panel.add(level);

        xp = labelForString("XP: " + player.getXP());
        panel.add(xp);

        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, 1));
        this.statusPanel = statusPanel;

        JToggleButton statusButton = new JToggleButton("Status");
        statusButton.addActionListener(arg0 -> {
            if (statusButton.isSelected()) {
                statusTarget.add(statusPanel, BorderLayout.EAST);
            } else {
                statusTarget.remove(statusPanel);
            }
            refreshTarget.refresh();
            statusTarget.validate();
        });
        panel.add(statusButton);

        inventory = new JFrame("Inventory");
        inventory.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        inventory.setVisible(false);
        inventory.setLocationByPlatform(true);
        inventory.setResizable(true);
        inventory.setMinimumSize(new Dimension(800, 100));

        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.addActionListener(arg0 -> {
            toggleInventory(refreshTarget);
        });
        location = new JLabel();
        location.setFont(new Font("Sylfaen", 1, 16));
        location.setForeground(GUIColors.textColorLight);

        panel.add(location);

        time = labelForString("");
        panel.add(time);

        cash = labelForString("");
        panel.add(cash);
        panel.add(inventoryButton);
    }

    JPanel getPanel() {
        return panel;
    }

    private void toggleInventory(GUI refreshTarget) {
        EventQueue.invokeLater(() -> {
            if (!inventory.isVisible()) {
                refreshTarget.refresh();
                inventory.setVisible(true);
            } else {
                inventory.setVisible(false);
            }
        });
    }

    void refresh() {
        level.setText("Lvl: " + player.getLevel());
        xp.setText("XP: " + player.getXP());

        location.setText(player.location().name);
        cash.setText("$" + player.money);

        refreshTime();
        displayStatus();
        refreshInventory();
    }

    private void refreshTime() {
        String timeText;
        String textColor = "rgb(0, 0, 0)";

        // We may be in between setting NIGHT and building the Match object
        if (Global.getTime() == Time.NIGHT) {
            // yup... silverbard pls :D
            if (Global.getMatch() == null) {
                timeText = "9:50 pm";
            } else if (Global.getMatch().getHour() >= 12) {
                timeText = Global.getMatch().getTime() + " am";
            } else {
                timeText = Global.getMatch().getTime() + " pm";
            }
            textColor = "rgb(51, 101, 202)";
        } else if (Global.getTime() == Time.DAY) { // not updating correctly during daytime
            if (Global.getDay() != null) {
                timeText = Global.getDay().getTime();
            } else {
                timeText = "10:00 am";
            }
            textColor = "rgb(253, 184, 19)";
        } else {
            System.err.println("Unknown time of day: " + Global.getTime());
            timeText = "";
        }
        time.setText(String.format("<html>Day %d - <font color='%s'>%s</font></html>", Global.getDate(), textColor, timeText));
    }

    public void displayStatus() {
        // resolution resolver

        Integer height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
        Integer width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);
        Integer fontsize = 5;

        statusPanel.removeAll();
        statusPanel.repaint();
        statusPanel.setPreferredSize(new Dimension(width/8, statusTarget.getHeight()));


        if (width < 720) {
            statusPanel.setMaximumSize(new Dimension(height, width / 6));
            System.out.println("STATUS PANEL");
        }
        JPanel statsPanel = new JPanel(new GridLayout(0, 3));

        Player player = Global.human;

        statusPanel.add(statsPanel);
        //statsPanel.setPreferredSize(new Dimension(400, 200));
        statsPanel.setPreferredSize(new Dimension(width/8, 200));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(statusPanel.getWidth(), 2));
        statusPanel.add(sep);
        int count = 0;
        statsPanel.setBackground(GUIColors.bgLight);
        int descFontSize = fontsize - 1;
        ArrayList<JLabel> attlbls = new ArrayList<>();
        for (Attribute a : Attribute.values()) {
            int amt = player.get(a);
            int pure = player.getPure(a);
            if (pure > 0 || amt > 0) {
                if (amt == pure) {
                    JLabel label = new JLabel(String.format("<html><font face='Georgia' size=%d>%s: %s</font></html>", descFontSize, a.name(), amt));
                    label.setForeground(GUIColors.textColorLight);
                    attlbls.add(count, label);
                    statsPanel.add(attlbls.get(count++));
                } else {
                    String attrColor;
                    String bonusColor;
                    if (amt < pure) {
                        attrColor = "255,100,100";
                        bonusColor = "255,0,0";
                    } else {
                        attrColor = "100,255,255";
                        bonusColor = "0,255,0";
                    }
                    int statBonusFontSize = descFontSize - 1;
                    String labelString = String.format("<html><font face='Georgia' size=%d>%s: <font color='rgb(%s)'>%d</font> <font size=%d color='rgb(%s)'>(%+d)</font></font></html>",
                        descFontSize, a.name(), attrColor, amt, statBonusFontSize, bonusColor, amt - pure);
                    JLabel label = new JLabel(labelString);
                    label.setForeground(GUIColors.textColorLight);
                    attlbls.add(count, label);
                    statsPanel.add(attlbls.get(count++));
                }
            }
        }

        // statusText - body, clothing and status description
        JTextPane statusText = new JTextPane();
        DefaultCaret caret = (DefaultCaret) statusText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        statusText.setBackground(GUIColors.bgLight);
        statusText.setEditable(false);
        statusText.setContentType("text/html");
        HTMLDocument doc = (HTMLDocument) statusText.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) statusText.getEditorKit();
        Global.freezeRNG();
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                "<font face='Georgia' color='white' size='" + descFontSize + "'>"

                    + player.getOutfit().describe(player) + "<br/>" + player.describeStatus()

                    + (Global.getButtslutQuest().isPresent()?("<br/>" + Global.getButtslutQuest().get().getDescriptionFor(player)):"")
                    + "</font><br/>",
                0, 0, null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
        Global.unfreezeRNG();
        JScrollPane scrollPane = new JScrollPane(statusText);
        scrollPane.setBackground(GUIColors.bgLight);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        JPanel currentStatusPanel = new JPanel(new GridLayout());
//        statusPanel.setPreferredSize(new Dimension(400, height));
//        currentStatusPanel.setMaximumSize(new Dimension(400, 2000));
//        currentStatusPanel.setPreferredSize(new Dimension(400, 2000));
        statusPanel.setPreferredSize(new Dimension(width/8, height));
        currentStatusPanel.setMaximumSize(new Dimension(width/8, 2000));
        currentStatusPanel.setPreferredSize(new Dimension(width/8, 2000));

        currentStatusPanel.setBackground(GUIColors.bgLight);
        statusPanel.add(currentStatusPanel);
        currentStatusPanel.add(scrollPane);
        statusPanel.setBackground(GUIColors.bgLight);

       if (width < 720) {
            currentStatusPanel.setSize(new Dimension(height, width / 6));
            System.out.println("Oh god so tiny");
        }
        statusPanel.revalidate();
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    private void refreshInventory() {
        List<Item> availItems = player.getInventory().entrySet().stream().filter(entry -> (entry.getValue() > 0))
            .map(Map.Entry::getKey).collect(Collectors.toList());

        JPanel inventoryPane = new JPanel();
        inventoryPane.setLayout(new GridLayout(0, 5));
        inventoryPane.setSize(new Dimension(400, 800));
        inventoryPane.setBackground(GUIColors.bgDark);

        Map<Item, Integer> items = player.getInventory();

        for (Item i : availItems) {
            JLabel label = new JLabel(i.getName() + ": " + items.get(i) + "\n");
            label.setForeground(GUIColors.textColorLight);
            label.setToolTipText(i.getDesc());
            inventoryPane.add(label);
        }
        inventory.getContentPane().removeAll();
        inventory.getContentPane().add(BorderLayout.CENTER, inventoryPane);
        inventory.pack();
    }
}