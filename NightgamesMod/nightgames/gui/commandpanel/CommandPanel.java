package nightgames.gui.commandpanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.gui.GUIColors;
import nightgames.skills.Skill;
import nightgames.skills.SkillGroup;
import nightgames.skills.Tactics;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandPanel extends JFXPanel {
    private HBox commandPanel;

    private Map<Tactics, SkillGroup> skills;
    private Character target;
    private Combat combat;
    private Tactics selectedTactic;
    private Skill selectedSkill;
    private Button submitButton;
    private ToggleGroup buttonGroup;
    private HashMap<String, ActionListener> eventMap = new HashMap<>();
    private Button backButton;
    private Node focusTarget;

    public CommandPanel() {
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            commandPanel = new HBox();
            commandPanel.setAlignment(Pos.CENTER);
            commandPanel.setBackground(new Background(new BackgroundFill(GUIColors.PAINT_BG_DARK, CornerRadii.EMPTY, Insets.EMPTY)));
            commandPanel.setBorder(new Border(new BorderStroke(
                    new Color(0, 0, 0, 0),
                    BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY,
                    BorderStroke.DEFAULT_WIDTHS)));

            ScrollPane scrollPane = new ScrollPane(commandPanel);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setFitToWidth(true);
            scrollPane.setBackground(new Background(new BackgroundFill(GUIColors.PAINT_BG_DARK, CornerRadii.EMPTY, Insets.EMPTY)));

            buttonGroup = new ToggleGroup();
            buttonGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    submitButton.setVisible(true);
                } else {
                    submitButton.setVisible(false);
                }
            });

            submitButton = new Button();
            submitButton.setText("Do it!");
            submitButton.setTextFill(Color.WHITE);
            submitButton.setBackground(new Background(new BackgroundFill(GUIColors.PAINT_BG_GREY, CornerRadii.EMPTY, Insets.EMPTY)));
            submitButton.setFont(new Font("Baskerville Old Face", 18));
            submitButton.setOnAction(event -> SwingUtilities.invokeLater(() -> eventMap.get((String) buttonGroup.getSelectedToggle().getUserData()).actionPerformed(null)));
            submitButton.setVisible(false);
            submitButton.setAlignment(Pos.CENTER);

            var submitButtonPane = new StackPane(submitButton);

            var self = this;
            backButton = new Button();
            backButton.setText("Something else...");
            backButton.setTextFill(Color.WHITE);
            backButton.setBackground(new Background(new BackgroundFill(GUIColors.PAINT_BG_GREY, CornerRadii.EMPTY, Insets.EMPTY)));
            backButton.setFont(new Font("Baskerville Old Face", 18));
            backButton.setOnAction(event -> {
                self.upOneLevel();
                backButton.setVisible(false);
            });
            backButton.setVisible(false);
            backButton.setAlignment(Pos.CENTER);

            var backButtonPane = new StackPane(backButton);

            var pane = new BorderPane();
            pane.setTop(submitButtonPane);
            pane.setCenter(scrollPane);
            pane.setBottom(backButtonPane);
            pane.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case UP: {
                        if (submitButton.isVisible()) {
                            submitButton.fire();
                        }
                    }
                    break;
                    case RIGHT: {
                        int selectedIndex;
                        if (buttonGroup.getSelectedToggle() == null) {
                            selectedIndex = buttonGroup.getToggles().size() / 2;
                        } else {
                            selectedIndex = buttonGroup.getToggles().indexOf(buttonGroup.getSelectedToggle());
                        }
                        var nextSelectedToggle = buttonGroup.getToggles().get((selectedIndex + 1) % buttonGroup.getToggles().size());
                        buttonGroup.selectToggle(nextSelectedToggle);
                    }
                    break;
                    case LEFT: {
                        int selectedIndex;
                        if (buttonGroup.getSelectedToggle() == null) {
                            selectedIndex = buttonGroup.getToggles().size() / 2;
                        } else {
                            selectedIndex = buttonGroup.getToggles().indexOf(buttonGroup.getSelectedToggle());
                        }
                        var nextSelectedToggle = buttonGroup.getToggles().get((selectedIndex - 1 + buttonGroup.getToggles().size()) % buttonGroup.getToggles().size());
                        buttonGroup.selectToggle(nextSelectedToggle);
                    }
                    break;
                    case DOWN: {
                        if (backButton.isVisible()) {
                            backButton.fire();
                        }
                    }
                }
            });
            pane.setFocusTraversable(false);
            pane.setBackground(new Background(new BackgroundFill(new Color(0, 0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setBorder(new Border(new BorderStroke(
                    new Color(0, 0, 0, 0),
                    BorderStrokeStyle.NONE,
                    CornerRadii.EMPTY,
                    BorderStroke.DEFAULT_WIDTHS)));
            focusTarget = pane;

            var scene = new Scene(pane);
            scene.setFill(GUIColors.PAINT_BG_DARK);
            setScene(scene);
        });

        setOpaque(false);
        setBorder(new CompoundBorder());

        skills = new HashMap<>();
    }

    public void reset() {
        skills.clear();
        Platform.runLater(() -> {
            submitButton.setVisible(false);
            backButton.setVisible(false);
        });
        clear();
        refresh();
    }

    private void clear() {
        Platform.runLater(() -> {
            submitButton.setVisible(false);
            commandPanel.getChildren().clear();
            eventMap.clear();
            buttonGroup.getToggles().clear();
            refresh();
        });
    }

    private void refresh() {
            SwingUtilities.invokeLater(() -> {
                repaint();
                revalidate();
            });
    }

    private void add(List<CommandPanelButton> buttons) {
        Platform.runLater(() -> {
            commandPanel.getChildren().addAll(buttons);
            buttons.forEach(b -> {
                b.setToggleGroup(buttonGroup);
                eventMap.put((String) b.getUserData(), b.getListener());
            });
            buttonGroup.selectToggle(buttons.get(buttons.size() / 2));
            focusTarget.requestFocus();
        });
    }

    public void present(List<CommandPanelOption> options) {
        presentNoReset(options.stream()
            .map(option -> option.wrap(event -> reset(), event -> {}))
            .collect(Collectors.toList()));
    }

    public void presentNoReset(List<CommandPanelOption> options) {
        clear();
        add(options.stream().map(CommandPanelOption::toButton).collect(Collectors.toList()));
        refresh();
    }

    void addNoReset(List<CommandPanelOption> options) {
        present(options);
    }

    private void upOneLevel() {
        if (selectedSkill != null) {
            selectedSkill = null;
            switchTactics(selectedTactic);
            return;
        }
        if (selectedTactic != null) {
            switchTactics(null);
        }
        selectedTactic = null;
    }

    void setSelectedSkill(Skill s) {
        selectedSkill = s;
    }

    private void addTactics() {
        add(Arrays.stream(Tactics.values())
            .filter(t -> this.skills.containsKey(t))
            .map(t -> CommandPanelButton.SwitchTacticsButton(t, event -> switchTactics(t)))
            .collect(Collectors.toList()));
    }

    public void chooseSkills(Combat com, nightgames.characters.Character target, List<SkillGroup> skills) {
        reset();
        if (skills.isEmpty()) {
            throw new IllegalArgumentException("skills cannot be empty");
        }
        combat = com;
        this.target = target;
        skills.forEach(group -> this.skills.put(group.tactics, group));
        addTactics();
        Global.getMatch().pause();
        refresh();
    }

    private void switchTactics(Tactics tactics) {
        clear();
        if (tactics != null) {
            add(this.skills.get(tactics).skills.stream()
                .map(skill -> CommandPanelButton.SkillButton(combat, skill, target, this))
                .collect(Collectors.toList()));
            selectedTactic = tactics;
            backButton.setVisible(true);
        } else {
            addTactics();
        }
        refresh();
    }
}
