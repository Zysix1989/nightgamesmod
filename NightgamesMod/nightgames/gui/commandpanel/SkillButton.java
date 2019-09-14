package nightgames.gui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.border.LineBorder;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Skill;
import nightgames.skills.Stage;

class SkillButton extends KeyableButton {
    private static final long serialVersionUID = -1253735466299929203L;
    protected Skill action;
    protected Combat combat;
    private Character target;
    private CommandPanel commandPanel;

    SkillButton(Combat c, final Skill action, Character target, CommandPanel panel) {
        super(action.getLabel(c));
        this.commandPanel = panel;
        this.target = target;
        combat = c;
        this.action = action;

        int actualAccuracy = this.target.getChanceToHit(
            this.action.getSelf(), combat, this.action.accuracy(combat, this.target));
        int clampedAccuracy = Math.min(100, Math.max(0, actualAccuracy));
        String text = "<html>" + "<p><b>" + this.action.getLabel(combat) + "</b><br/>" +
            this.action.describe(combat) + "<br/><br/>Accuracy: " +
            (actualAccuracy >=150 ? "---" : clampedAccuracy + "%") + "</p>";
        if (this.action.getMojoCost(combat) > 0) {
            setBorder(new LineBorder(Color.RED, 3));
            text += "<br/>Mojo cost: " + this.action.getMojoCost(combat);
        } else if (this.action.getMojoBuilt(combat) > 0) {
            setBorder(new LineBorder(new Color(53, 201, 255), 3));
            text += "<br/>Mojo generated: " + this.action.getMojoBuilt(combat) + "%";
        } else {
            setBorder(new LineBorder(getButton().getBackground(), 3));
        }
        if (!this.action.user().cooldownAvailable(this.action)) {
            getButton().setEnabled(false);
            text += String.format("<br/>Remaining Cooldown: %d turns", this.action.user()
                .getCooldown(this.action));
            getButton().setForeground(Color.WHITE);
            getButton().setBackground(getBackground().darker());
        }
        text += "</html>";
        setText(text);

        getButton().setBorderPainted(false);
        getButton().setOpaque(true);
        getButton().setFont(fontForStage(action.getStage()));
        Color bgColor = action.type(c).getColor();
        getButton().setBackground(bgColor);
        getButton().setForeground(foregroundColor(bgColor));

        getButton().addActionListener(arg0 -> {
            if (action.subChoices(c).size() == 0) {
                commandPanel.reset();
                combat.act(SkillButton.this.action.user(), SkillButton.this.action);
                combat.resume();
            } else {
                List<CommandPanelOption> options = action.subChoices(c).stream()
                    .map(choice -> new CommandPanelOption(
                        choice,
                        event -> {
                            commandPanel.reset();
                            action.setChoice(choice);
                            c.act(action.user(), action);
                            c.resume();
                        }
                    )).collect(Collectors.toList());
                commandPanel.addNoReset(options);
                commandPanel.setSelectedSkill(action);
            }
        });
        setLayout(new BorderLayout());
        add(getButton());
    }

    private static Color foregroundColor(Color bgColor) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getRed(), hsb);
        if (hsb[2] < .6) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    private static Font fontForStage(Stage stage) {
        switch (stage) {
            case FINISHER:
                return new Font("Baskerville Old Face", Font.BOLD, 3 * Global.gui().fontsize);
            case FOREPLAY:
                return new Font("Baskerville Old Face", Font.ITALIC, 3 * Global.gui().fontsize);
            default:
                return new Font("Baskerville Old Face", Font.PLAIN, 3 * Global.gui().fontsize);
            
        }
    }

}
