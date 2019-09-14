package nightgames.gui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Skill;
import nightgames.skills.Stage;
import nightgames.skills.Tactics;

class CommandPanelButton extends JPanel {
    private static final long serialVersionUID = -2379908542190189603L;
    private final JButton button;

    private CommandPanelButton(String text) {
        this.button = new JButton(text);
        this.setLayout(new BorderLayout());
        this.add(button);
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    private static String formatHTMLMultiline(String original) {
        return String.format("<html><center>%s</center></html>", original);
    }

    static CommandPanelButton BasicButton(String text, ActionListener action) {
        text = formatHTMLMultiline(text);
        var button = new CommandPanelButton(text);
        var fontSize = 18;
        if (text.contains("<br/>")) {
            fontSize = 14;
        }
        button.button.setFont(new Font("Baskerville Old Face", Font.PLAIN, fontSize));
        button.button.addActionListener(action);
        return button;
    }

    private static Color textColorForBackground(Color backgroundColor) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(
            backgroundColor.getRed(),
            backgroundColor.getGreen(),
            backgroundColor.getRed(),
            hsb);
        if (hsb[2] < .6) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    static CommandPanelButton SwitchTacticsButton(Tactics tactic, ActionListener action) {
        var button = BasicButton(Global.capitalizeFirstLetter(tactic.name()), action);

        button.button.setBackground(tactic.getColor());
        button.button.setMinimumSize(new Dimension(0, 20));
        button.button.setForeground(textColorForBackground(tactic.getColor()));
        button.setBorder(new LineBorder(tactic.getColor(), 3));
        return button;
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

    static CommandPanelButton SkillButton (
        Combat combat,
        Skill action,
        Character target,
        CommandPanel commandPanel) {

        LineBorder border;
        int actualAccuracy = target.getChanceToHit(action.getSelf(), combat, action.accuracy(combat, target));
        int clampedAccuracy = Math.min(100, Math.max(0, actualAccuracy));
        String text = "<p><b>" + action.getLabel(combat) + "</b><br/>" +
            action.describe(combat) +
            "<br/><br/>Accuracy: " +
            (actualAccuracy >=150 ? "---" : clampedAccuracy + "%") + "</p>";
        if (action.getMojoCost(combat) > 0) {
            border = new LineBorder(Color.RED, 3);
            text += "<br/>Mojo cost: " + action.getMojoCost(combat);
        } else if (action.getMojoBuilt(combat) > 0) {
            border = new LineBorder(new Color(53, 201, 255), 3);
            text += "<br/>Mojo generated: " + action.getMojoBuilt(combat) + "%";
        } else {
            border = new LineBorder(action.type(combat).getColor(), 3);
        }
        boolean onCoolDown = false;
        if (!action.user().cooldownAvailable(action)) {
            onCoolDown = true;
            text += String.format("<br/>Remaining Cooldown: %d turns",
                action.user().getCooldown(action));
        }

        ActionListener actionListener = arg0 -> {
            if (action.subChoices(combat).size() == 0) {
                commandPanel.reset();
                combat.act(action.user(), action);
                combat.resume();
            } else {
                List<CommandPanelOption> options = action.subChoices(combat).stream()
                    .map(choice -> new CommandPanelOption(
                        choice,
                        event -> {
                            commandPanel.reset();
                            action.setChoice(choice);
                            combat.act(action.user(), action);
                            combat.resume();
                        }
                    )).collect(Collectors.toList());
                commandPanel.addNoReset(options);
                commandPanel.setSelectedSkill(action);
            }
        };

        var button = BasicButton(text, actionListener);
        button.button.setFont(fontForStage(action.getStage()));
        button.button.setBorder(border);
        Color bgColor = action.type(combat).getColor();
        if (onCoolDown) {
            bgColor = action.type(combat).getColor().darker();
            button.button.setEnabled(false);
        }
        button.button.setBackground(bgColor);
        button.button.setForeground(textColorForBackground(bgColor));
        return button;
    }

    public JButton getButton() {
        return button;
    }
}
