package nightgames.gui.commandpanel;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

class KeyableButton extends JPanel {
    private static final long serialVersionUID = -2379908542190189603L;
    private final JButton button;

    KeyableButton(String text) {
        this.button = new JButton(text);
        this.setLayout(new BorderLayout());
        this.add(button);
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    protected void setText(String s) {
        button.setText(s);
    }

    public JButton getButton() {
        return button;
    }
}
