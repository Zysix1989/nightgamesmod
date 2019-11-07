package nightgames.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.SoftBevelBorder;
import nightgames.characters.CoreStat;

class GUICoreStat {

    private String name;
    private CoreStat coreStat;
    private JLabel label;
    private JProgressBar progressBar;
    private JPanel panel;

    GUICoreStat(String name, CoreStat coreStat, Color color, String toolTipText) {
        this.name = name;
        this.coreStat = coreStat;

        label = new JLabel("");
        label.setFont(new Font("Sylfaen", 1, 15));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(color);
        label.setToolTipText(toolTipText);

        progressBar = new JProgressBar();
        progressBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(50, 50, 50));

        panel = new JPanel(new GridLayout(2, 1, 0, 0));
        panel.add(label);
        panel.add(progressBar);
        panel.setBackground(new Color(50, 50, 50));
        refresh();
    }

    JPanel getPanel() {
        return panel;
    }

    final void refresh() {
        label.setText(getLabelString());
        progressBar.setMaximum(coreStat.max());
        progressBar.setValue(coreStat.get());
    }

    private String getLabelString() {
        String text = name + ": ";
        boolean overflow = coreStat.getOverflow() > 0;
        text += overflow ? "(" : "";
        text += Integer.toString(coreStat.get() + coreStat.getOverflow());
        text += overflow ? ")" : "";
        text += "/" + coreStat.max();
        return text;
    }
}

