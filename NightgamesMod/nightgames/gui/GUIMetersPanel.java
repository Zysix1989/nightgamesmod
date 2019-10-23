package nightgames.gui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import nightgames.characters.NPC;

class GUIMetersPanel extends JPanel {
    private GUIMeterPanel stamina;
    private GUIMeterPanel arousal;
    private GUIMeterPanel mojo;
    private GUIMeterPanel willpower;

    GUIMetersPanel() {
        stamina = new GUIMeterPanel("stamina.png", GUIColors.staminaColor);
        arousal = new GUIMeterPanel("arousal.png", GUIColors.arousalColor);
        mojo = new GUIMeterPanel("mojo.png", GUIColors.mojoColor);
        willpower = new GUIMeterPanel("willpower.png", GUIColors.willpowerColor);

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

    void setTarget(NPC target) {
        stamina.setTargetMeter(target.getStamina());
        arousal.setTargetMeter(target.getArousal());
        mojo.setTargetMeter(target.getMojo());
        willpower.setTargetMeter(target.getWillpower());
    }

    void refresh() {
        stamina.refresh();
        arousal.refresh();
        mojo.refresh();
        willpower.refresh();
    }

    void clear() {
        stamina.clear();
        arousal.clear();
        mojo.clear();
        willpower.clear();
    }
}
