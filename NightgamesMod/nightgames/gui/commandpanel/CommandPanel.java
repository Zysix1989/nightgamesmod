package nightgames.gui.commandpanel;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.gui.GUIColors;
import nightgames.skills.Skill;
import nightgames.skills.SkillGroup;
import nightgames.skills.Tactics;

public class CommandPanel extends JPanel{
    private JPanel commandPanel;

    private Map<Tactics, SkillGroup> skills;
    private Character target;
    private Combat combat;
    private Tactics selectedTactic;
    private Skill selectedSkill;
    private JButton backButton;

    public CommandPanel() {
        commandPanel = new JPanel();
        commandPanel.setOpaque(false);
        commandPanel.setLayout(new GroupLayout(commandPanel));

        JScrollPane scrollPane = new JScrollPane(commandPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setLayout(ViewportLayout.SHARED_INSTANCE);
        scrollPane.getViewport().setBackground(GUIColors.bgDark);

        backButton = new JButton();
        backButton.setText("back");
        CommandPanel self = this;
        backButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                self.upOneLevel();
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
        backButton.setVisible(false);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.PAGE_END);
        setOpaque(false);
        setBorder(new CompoundBorder());

        skills = new HashMap<>();
    }

    public void reset() {
        skills.clear();
        backButton.setVisible(false);
        clear();
        refresh();
    }

    private void clear() {
        commandPanel.removeAll();
        refresh();
    }

    private void refresh() {
        repaint();
        revalidate();
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
            .map(t -> KeyableButton.SwitchTacticsButton(t, event -> switchTactics(t)))
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
        backButton.setVisible(true);
        refresh();
    }

    private void switchTactics(Tactics tactics) {
        clear();
        if (tactics != null) {
            add(this.skills.get(tactics).skills.stream()
                .map(skill -> new SkillButton(combat, skill, target, this))
                .collect(Collectors.toList()));
            selectedTactic = tactics;
        } else {
            addTactics();
        }
        refresh();
    }
}
