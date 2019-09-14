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

        setText(getText());

        getButton().setBorderPainted(false);
        getButton().setOpaque(true);
        getButton().setFont(fontForStage(action.getStage()));
        Color bgColor = action.type(c).getColor();
        getButton().setBackground(bgColor);
        getButton().setForeground(foregroundColor(bgColor));

        getButton().setToolTipText(getText());
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

    private String getText() {
        int actualAccuracy = target.getChanceToHit(action.getSelf(), combat, action.accuracy(combat, target));
        int clampedAccuracy = Math.min(100, Math.max(0, actualAccuracy));
        String text = "<html>" + "<p><b>" + action.getLabel(combat) + "</b><br/>" +
            action.describe(combat) + "<br/><br/>Accuracy: " +
            (actualAccuracy >=150 ? "---" : clampedAccuracy + "%") + "</p>";
        if (action.getMojoCost(combat) > 0) {
            setBorder(new LineBorder(Color.RED, 3));
            text += "<br/>Mojo cost: " + action.getMojoCost(combat);
        } else if (action.getMojoBuilt(combat) > 0) {
            setBorder(new LineBorder(new Color(53, 201, 255), 3));
            text += "<br/>Mojo generated: " + action.getMojoBuilt(combat) + "%";
        } else {
            setBorder(new LineBorder(getButton().getBackground(), 3));
        }
        if (!action.user().cooldownAvailable(action)) {
            getButton().setEnabled(false);
            text += String.format("<br/>Remaining Cooldown: %d turns", action.user()
                .getCooldown(action));
            getButton().setForeground(Color.WHITE);
            getButton().setBackground(getBackground().darker());
        }
        text += "</html>";
        return text;
    }
}
