package nightgames.gui.commandpanel;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Skill;
import nightgames.skills.Stage;
import nightgames.skills.Tactics;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.stream.Collectors;

class CommandPanelButton extends JPanel {
    private final JRadioButton button;
    private final ActionListener listener;

    // Very annoying, but supplying a null icon results in the default from the L&F
    private static final class NoIcon implements Icon {
        @Override
        public void paintIcon(Component component, Graphics graphics, int i, int i1) {

        }
        @Override
        public int getIconWidth() {
            return 0;
        }

        @Override
        public int getIconHeight() {
            return 0;
        }
    }

    private CommandPanelButton(String text, ActionListener listener) {
        this.listener = listener;
        this.button = new JRadioButton(text, new NoIcon());
        this.button.setActionCommand(text);
        var self = this;
        this.button.addItemListener(itemEvent -> {
            switch (itemEvent.getStateChange()) {
                case ItemEvent.SELECTED:
                    self.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
                    break;
                case ItemEvent.DESELECTED:
                    self.setBorder(BorderFactory.createEmptyBorder());
                    break;
            }
        });
        this.button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1) {
                    self.listener.actionPerformed(null);
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
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
        var button = new CommandPanelButton(text, action);
        var fontSize = 18;
        if (text.contains("<br/>")) {
            fontSize = 14;
        }
        button.button.setFont(new Font("Baskerville Old Face", Font.PLAIN, fontSize));
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

    public JRadioButton getButton() {
        return button;
    }

    ActionListener getListener() { return listener; }
}
