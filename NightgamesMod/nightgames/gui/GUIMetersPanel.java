package nightgames.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;

class GUIMetersPanel extends JPanel {
    private GUIMeterPanel stamina;
    private GUIMeterPanel arousal;
    private GUIMeterPanel mojo;
    private GUIMeterPanel willpower;

    GUIMetersPanel() {
        stamina = new GUIMeterPanel("stamina.png");
        arousal = new GUIMeterPanel("arousal.png");
        mojo = new GUIMeterPanel("mojo.png");
        willpower = new GUIMeterPanel("willpower.png");

        setOpaque(false);

        var layout = new GroupLayout(this);
        layout.setAutoCreateGaps(false);
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(stamina)
            .addComponent(arousal)
            .addComponent(mojo)
            .addComponent(willpower)
        );
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.CENTER)
            .addComponent(stamina)
            .addComponent(arousal)
            .addComponent(mojo)
            .addComponent(willpower)
        );
        setLayout(layout);
    }
}
