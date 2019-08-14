package nightgames.gui.commandpanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.gui.GUIColors;
import nightgames.skills.SkillGroup;
import nightgames.skills.Tactics;

public class CommandPanel{
    private static final List<java.lang.Character> POSSIBLE_HOTKEYS = Arrays.asList(
                    'q', 'w', 'e', 'r', 't', 'y',
                    'a', 's', 'd', 'f' , 'g', 'h',
                    'z', 'x', 'c', 'v', 'b', 'n'); 
    private static final int ROW_LIMIT = 6;

    private JPanel panel;
    private Box groupBox;
    private JPanel commandPanel;
    private int index;
    private int page;
    private List<KeyableButton> buttons;
    private JPanel[] rows;

    private Map<Tactics, SkillGroup> skills;
    private Character target;
    private Combat combat;

    public CommandPanel(int width) {
        commandPanel = new JPanel();
        commandPanel.setBackground(GUIColors.bgDark);
        commandPanel.setPreferredSize(new Dimension(width, 160));
        commandPanel.setMinimumSize(new Dimension(width, 160));
        commandPanel.setBorder(new CompoundBorder());
        rows = new JPanel[POSSIBLE_HOTKEYS.size() / ROW_LIMIT];
        rows[0] = new JPanel();
        rows[1] = new JPanel();
        rows[2] = new JPanel();
        for (JPanel row : rows) {
            FlowLayout layout;
            layout = new FlowLayout();
            layout.setVgap(0);
            layout.setHgap(4);
            row.setLayout(layout);
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder());
            row.setPreferredSize(new Dimension(0, 20));
            commandPanel.add(row);
        }
        BoxLayout layout = new BoxLayout(commandPanel, BoxLayout.Y_AXIS);
        commandPanel.setLayout(layout);
        commandPanel.add(Box.createVerticalGlue());
        commandPanel.add(Box.createVerticalStrut(2));
        buttons = new ArrayList<>();
        index = 0;

        // commandPanel - visible, contains the player's command buttons
        groupBox = Box.createHorizontalBox();
        groupBox.setBackground(GUIColors.bgDark);
        groupBox.setBorder(new CompoundBorder());
        panel = new JPanel();
        panel.add(groupBox);
        panel.add(commandPanel);
        panel.setBackground(GUIColors.bgDark);
        panel.setBorder(new CompoundBorder());

        skills = new HashMap<>();
    }

    public JPanel getPanel() {
        return panel;
    }

    public void reset() {
        skills.clear();
        groupBox.removeAll();
        buttons.clear();
        clear();
        refresh();
    }

    private void clear() {
        for (JPanel row : rows) {
            row.removeAll();
        }
        index = 0;  
    }

    private void refresh() {
        commandPanel.repaint();
        commandPanel.revalidate();
    }

    private void add(KeyableButton button) {
        page = 0;
        buttons.add(button);
        use(button);
    }

    public void present(List<CommandPanelOption> options) {
        clear();
        options.forEach(option -> add(option.wrap(event -> reset(), event -> {}).toButton()));
        refresh();
    }

    public void presentNoReset(List<CommandPanelOption> options) {
        clear();
        options.forEach(option -> add(option.toButton()));
        refresh();
    }

    void addNoReset(List<CommandPanelOption> options) {
        groupBox.removeAll();
        buttons.clear();
        present(options);
    }

    private void use(KeyableButton button) {
        int effectiveIndex = index - page * POSSIBLE_HOTKEYS.size();
        if (effectiveIndex >= 0 && effectiveIndex < POSSIBLE_HOTKEYS.size()) {
            int rowIndex = Math.min(rows.length - 1, effectiveIndex / ROW_LIMIT);
            JPanel row = rows[rowIndex];
            row.add(button);
        } else if (effectiveIndex == -1) {
            KeyableButton leftPage = new RunnableButton("<<<", () -> setPage(page - 1));
            rows[0].add(leftPage, 0);
        } else if (effectiveIndex == POSSIBLE_HOTKEYS.size()){
            KeyableButton rightPage = new RunnableButton(">>>", () -> setPage(page + 1));
            rows[0].add(rightPage);
        }
        index += 1;
    }

    private void setPage(int page) {
        this.page = page;
        clear();
        buttons.forEach(this::use);
        refresh();
    }

    public void chooseSkills(Combat com, nightgames.characters.Character target, List<SkillGroup> skills) {
        reset();
        if (skills.isEmpty()) {
            throw new IllegalArgumentException("skills cannot be empty");
        }
        combat = com;
        this.target = target;
        skills.forEach(group -> this.skills.put(group.tactics, group));

        for (Tactics tactic : Tactics.values()) {
            if (!this.skills.containsKey(tactic)) continue;
            SwitchTacticsButton tacticsButton = new SwitchTacticsButton(tactic,
                event -> switchTactics(tactic));
            groupBox.add(tacticsButton);
            groupBox.add(Box.createHorizontalStrut(4));
        }
        Global.getMatch().pause();
        refresh();
    }

    private void switchTactics(Tactics tactics) {
        clear();
        this.skills.get(tactics).skills.forEach(skill -> {
            SkillButton button = new SkillButton(combat, skill, target, this);
            add(button);
        });
        refresh();
    }
}
