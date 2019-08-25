package nightgames.gui.commandpanel;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.gui.GUIColors;
import nightgames.skills.SkillGroup;
import nightgames.skills.Tactics;

public class CommandPanel{
    private JPanel panel;
    private Box groupBox;
    private JPanel commandPanel;

    private Map<Tactics, SkillGroup> skills;
    private Character target;
    private Combat combat;

    public CommandPanel() {
        commandPanel = new JPanel();
        commandPanel.setBackground(GUIColors.bgDark);
        commandPanel.setLayout(new GroupLayout(commandPanel));

        JScrollPane scrollPane = new JScrollPane(commandPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setLayout(ViewportLayout.SHARED_INSTANCE);
        scrollPane.getViewport().setBackground(GUIColors.bgDark);
        // commandPanel - visible, contains the player's command buttons
        groupBox = Box.createHorizontalBox();
        groupBox.setBackground(GUIColors.bgDark);
        groupBox.setBorder(new CompoundBorder());

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(groupBox, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
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
        clear();
        refresh();
    }

    private void clear() {
        commandPanel.removeAll();
        commandPanel.revalidate();
    }

    private void refresh() {
        commandPanel.repaint();
        commandPanel.revalidate();
    }

    private void add(List<KeyableButton> buttons) {
        GroupLayout layout = new GroupLayout(commandPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        SequentialGroup horizontal = layout.createSequentialGroup();
        ParallelGroup vertical = layout.createParallelGroup(Alignment.LEADING);

        buttons.forEach(button -> {
            horizontal.addComponent(button);
            vertical.addComponent(button);
        });
        layout.setHorizontalGroup(horizontal);
        layout.setVerticalGroup(vertical);
        commandPanel.setLayout(layout);
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
        groupBox.removeAll();
        present(options);
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
        add(this.skills.get(tactics).skills.stream()
            .map(skill -> new SkillButton(combat, skill, target, this))
            .collect(Collectors.toList()));
        refresh();
    }
}
