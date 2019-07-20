package nightgames.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.SoftBevelBorder;

import nightgames.characters.Meter;

class GUIMeter {

    private String name;
    private Meter meter;
    private JLabel label;
    private JProgressBar progressBar;

    GUIMeter(String name, Meter meter, Color color, String toolTipText) {
        this.name = name;
        this.meter = meter;
        this.label = new JLabel(this.getLabelString());

        this.label.setFont(new Font("Sylfaen", 1, 15));
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        this.label.setForeground(color);
        this.label.setToolTipText(toolTipText);

        this.progressBar = new JProgressBar();
        this.progressBar.setBorder(new SoftBevelBorder(1, null, null, null, null));
        this.progressBar.setForeground(color);
        this.progressBar.setBackground(new Color(50, 50, 50));

        this.progressBar.setMaximum(meter.max());
        this.progressBar.setValue(meter.get());
    }

    JLabel getLabel() {
        return label;
    }

    JProgressBar getProgressBar() {
        return progressBar;
    }

    void refresh() {
        label.setText(getLabelString());
        progressBar.setMaximum(meter.max());
        progressBar.setValue(meter.get());
    }

    private String getLabelString() {
        String text = name + ": ";
        boolean overflow = meter.getOverflow() > 0;
        text += overflow ? "(" : "";
        text += Integer.toString(meter.get() + meter.getOverflow());
        text += overflow ? ")" : "";
        text += "/" + Integer.toString(meter.max());
        return text;
    }
}

