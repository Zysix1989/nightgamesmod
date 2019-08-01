package nightgames.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Skill;
import nightgames.skills.TacticGroup;

public class CommandPanel {
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
    private Map<java.lang.Character, KeyableButton> hotkeyMapping;
    private List<KeyableButton> buttons;
    private JPanel[] rows;

    private Map<TacticGroup, ArrayList<Skill>> skills;
    private Character target;
    private Combat combat;

    CommandPanel(int width) {
        commandPanel = new JPanel();
        commandPanel.setBackground(GUIColors.bgDark);
        commandPanel.setPreferredSize(new Dimension(width, 160));
        commandPanel.setMinimumSize(new Dimension(width, 160));
        commandPanel.setBorder(new CompoundBorder());
        hotkeyMapping = new HashMap<>();
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
        Arrays.stream(TacticGroup.values()).forEach(tactic -> skills.put(tactic, new ArrayList<>()));
        groupBox.removeAll();
        buttons.clear();
        hotkeyMapping.clear();
        clear();
        refresh();
    }

    private void clear() {
        for (JPanel row : rows) {
            row.removeAll();
        }
        POSSIBLE_HOTKEYS.forEach(hotkeyMapping::remove);
        index = 0;  
    }

    public void refresh() {
        commandPanel.repaint();
        commandPanel.revalidate();
    }

    public void add(KeyableButton button) {
        page = 0;
        buttons.add(button);
        use(button);
    }

    private void use(KeyableButton button) {
        int effectiveIndex = index - page * POSSIBLE_HOTKEYS.size();
        int currentPage = page;
        if (effectiveIndex >= 0 && effectiveIndex < POSSIBLE_HOTKEYS.size()) {
            int rowIndex = Math.min(rows.length - 1, effectiveIndex / ROW_LIMIT);
            JPanel row = rows[rowIndex];
            row.add(button);
            java.lang.Character hotkey = POSSIBLE_HOTKEYS.get(effectiveIndex);
            register(hotkey, button);
        } else if (effectiveIndex == -1) {
            KeyableButton leftPage = new RunnableButton("<<<", () -> setPage(currentPage - 1));
            rows[0].add(leftPage, 0);
            register('~', leftPage);
        } else if (effectiveIndex == POSSIBLE_HOTKEYS.size()){
            KeyableButton rightPage = new RunnableButton(">>>", () -> setPage(currentPage + 1));
            rows[0].add(rightPage);
            register('`', rightPage);
        }
        index += 1;
    }

    private void setPage(int page) {
        this.page = page;
        clear();
        buttons.forEach(this::use);
        refresh();
    }

    Optional<KeyableButton> getButtonForHotkey(char keyChar) {
        return Optional.ofNullable(hotkeyMapping.get(keyChar));
    }

    private void register(java.lang.Character hotkey, KeyableButton button) {
        button.setHotkeyTextTo(hotkey.toString().toUpperCase());
        hotkeyMapping.put(hotkey, button);
    }

    private void addSkillToGroup(TacticGroup group, Skill skill) {
        if (!this.skills.containsKey(group)) {
            this.skills.put(group, new ArrayList<>());
        }
        this.skills.get(group).add(skill);
    }

    void chooseSkills(Combat com, nightgames.characters.Character target, List<Skill> skills) {
        reset();
        if (skills.isEmpty()) {
            throw new IllegalArgumentException("skills cannot be empty");
        }
        combat = com;
        this.target = target;
        skills.forEach(skill -> addSkillToGroup(skill.type(com).getGroup(), skill));

        // Order matters for TacticGroup.all
        for (TacticGroup group : TacticGroup.values()) {
            skills.stream().filter(skill -> skill.type(com).getGroup() == group)
                .forEach(skill -> addSkillToGroup(TacticGroup.all, skill));
        }

        int i = 1;
        for (TacticGroup group : TacticGroup.values()) {
            SwitchTacticsButton tacticsButton = new SwitchTacticsButton(group,
                event -> switchTactics(group),
                this.skills.get(group).isEmpty());
            register(java.lang.Character.forDigit(i % 10, 10), tacticsButton);
            groupBox.add(tacticsButton);
            groupBox.add(Box.createHorizontalStrut(4));
            i += 1;
        }

        switchTactics(TacticGroup.all);
        Global.getMatch().pause();
        refresh();
    }

    private void switchTactics(TacticGroup group) {
        clear();
        this.skills.get(group).forEach(skill -> {
            SkillButton button = new SkillButton(combat, skill, target);
            add(button);
        });
        refresh();
    }
}
