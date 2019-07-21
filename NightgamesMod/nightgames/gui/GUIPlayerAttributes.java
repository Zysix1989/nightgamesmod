package nightgames.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import nightgames.characters.Attribute;
import nightgames.characters.Player;
import nightgames.global.Global;

class GUIPlayerAttributes {

    private Player player;
    private JPanel targetPanel;
    private JPanel panel;
    private JToggleButton button;

    GUIPlayerAttributes(Player player, JPanel targetPanel, GUI refreshTarget) {
        this.player = player;
        this.targetPanel = targetPanel;

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, 1));

        button = new JToggleButton("Status");
        button.addActionListener(arg0 -> {
            if (button.isSelected()) {
                targetPanel.add(panel, BorderLayout.EAST);
            } else {
                targetPanel.remove(panel);
            }
            refreshTarget.refresh();
            targetPanel.validate();
        });
    }

    JToggleButton getButton() {
        return button;
    }

    void displayStatus() {
        // resolution resolver

        Integer height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
        Integer width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);
        Integer fontsize = 5;

        panel.removeAll();
        panel.repaint();
        panel.setPreferredSize(new Dimension(width/8, targetPanel.getHeight()));


        if (width < 720) {
            panel.setMaximumSize(new Dimension(height, width / 6));
            System.out.println("STATUS PANEL");
        }
        JPanel statsPanel = new JPanel(new GridLayout(0, 3));

        panel.add(statsPanel);
        //statsPanel.setPreferredSize(new Dimension(400, 200));
        statsPanel.setPreferredSize(new Dimension(width/8, 200));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(panel.getWidth(), 2));
        panel.add(sep);
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
//        panel.setPreferredSize(new Dimension(400, height));
//        currentStatusPanel.setMaximumSize(new Dimension(400, 2000));
//        currentStatusPanel.setPreferredSize(new Dimension(400, 2000));
        panel.setPreferredSize(new Dimension(width/8, height));
        currentStatusPanel.setMaximumSize(new Dimension(width/8, 2000));
        currentStatusPanel.setPreferredSize(new Dimension(width/8, 2000));

        currentStatusPanel.setBackground(GUIColors.bgLight);
        panel.add(currentStatusPanel);
        currentStatusPanel.add(scrollPane);
        panel.setBackground(GUIColors.bgLight);

        if (width < 720) {
            currentStatusPanel.setSize(new Dimension(height, width / 6));
            System.out.println("Oh god so tiny");
        }
        panel.revalidate();
        panel.revalidate();
        panel.repaint();
    }
}