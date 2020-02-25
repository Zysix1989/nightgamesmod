package nightgames.gui.commandpanel;

import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.gui.GUIColors;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

class CommandPanelButton extends ToggleButton {

    private CommandPanelButton(CommandPanelData data, Color backgroundColor) {
        setUserData(data);
        setText(data.label);
        setWrapText(true);
        setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        setTextFill(textColorForBackground(backgroundColor));
        setFocusTraversable(false);
        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                var newBGColor = backgroundColor.getBrightness() > .5 ? backgroundColor.darker() : backgroundColor.brighter();
                setBackground(new Background(new BackgroundFill(newBGColor, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() > 1) {
                data.action.actionPerformed(null);
            }
        });
    }

    static CommandPanelButton BasicButton(String text, ActionListener action) {
        return BasicButton(text, action, GUIColors.PAINT_BG_GREY);
    }

    private static CommandPanelButton BasicButton(String text, ActionListener action, Color backgroundColor) {
        var data = new CommandPanelData();
        data.label = text;
        data.action = action;
        return BasicButton(data, backgroundColor);
    }

    private static CommandPanelButton BasicButton(CommandPanelData data, Color backgroundColor) {
        var button = new CommandPanelButton(data, backgroundColor);
        var fontSize = 18;
        if (data.label.contains("<br/>")) {
            fontSize = 14;
        }
        button.setFont(new Font("Baskerville Old Face", fontSize));
        return button;
    }

    private static Color textColorForBackground(Color backgroundColor) {
        if (backgroundColor.getBrightness() < .6) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    static CommandPanelButton SwitchTacticsButton(Tactics tactic, ActionListener action) {
        var button = BasicButton(Global.capitalizeFirstLetter(tactic.name()), action, tactic.getColor());

        button.setMinSize(0, 20);
        button.setTextFill(textColorForBackground(tactic.getColor()));
        button.setBorder(new Border(new BorderStroke(
                tactic.getColor(),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.DEFAULT)));
        return button;
    }

    static CommandPanelButton SkillButton (
        Combat combat,
        Skill action,
        Character target,
        CommandPanel commandPanel) {

        BorderStroke border;
        int actualAccuracy = target.getChanceToHit(action.getSelf(), combat, action.accuracy(combat, target));
        int clampedAccuracy = Math.min(100, Math.max(0, actualAccuracy));

        var model = JtwigModel.newModel()
                .with("label", action.getLabel(combat))
                .with("description", action.describe(combat))
                .with("accuracy", (actualAccuracy >=150 ? "---" : clampedAccuracy + "%"));

        border = new BorderStroke(action.type(combat).getColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
        if (action.getMojoBuilt(combat) > 0) {
            border = new BorderStroke(new Color(.2, .66, 1, 1),
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
            model.with("mojoBuild", action.getMojoBuilt(combat));
        } else {
            model.with("mojoBuild", 0);
        }
        if (action.getMojoCost(combat) > 0) {
            border = new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
            model.with("mojoCost", action.getMojoCost(combat));
        } else {
            model.with("mojoCost", 0);
        }
        boolean onCoolDown = false;
        if (!action.user().cooldownAvailable(action)) {
            onCoolDown = true;
            model.with("coolDownTurns", action.user().getCooldown(action));
        } else {
            model.with("coolDownTurns", 0);
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

        Color bgColor = action.type(combat).getColor();
        if (onCoolDown) {
            bgColor = action.type(combat).getColor().darker();
        }
        var data = new CommandPanelData();
        data.label = action.getLabel(combat);
        data.detail = SKILL_DETAIL_TEMPLATE.render(model);
        data.action = actionListener;
        var button = BasicButton(data, bgColor);
        if (onCoolDown) {
            button.setDisabled(true);
        }
        button.setBorder(new Border(border));
        return button;
    }

    private static final JtwigTemplate SKILL_DETAIL_TEMPLATE = JtwigTemplate.inlineTemplate(
            "<p><b>{{ label }}</b><br/>" +
                    "{{ description }}<br/>" +
                    "Accuracy: {{ accuracy }}<br/>" +
                    "{% if (mojoCost > 0) %}Mojo cost: {{ mojoCost }}<br/>{% endif %}" +
                    "{% if (mojoBuild > 0) %}Mojo generated: {{ mojoBuild }}%<br/>{% endif %}" +
                    "{% if (coolDownTurns > 0) %}Remaining cooldown: {{ coolDownTurns }} turns<br/>{% endif %}");
}
