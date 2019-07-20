package nightgames.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import nightgames.characters.Meter;

class GUIMeter {

    private String name;
    private Meter meter;
    private JLabel label;

    GUIMeter(String name, Meter meter, Color color, String toolTipText) {
        this.name = name;
        this.meter = meter;
        this.label = new JLabel(this.getLabelString());

        this.label.setFont(new Font("Sylfaen", 1, 15));
        this.label.setHorizontalAlignment(SwingConstants.CENTER);
        this.label.setForeground(color);
        this.label.setToolTipText(toolTipText);
    }

    JLabel getLabel() {
        return label;
    }

    void refresh() {
        label.setText(getLabelString());
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

