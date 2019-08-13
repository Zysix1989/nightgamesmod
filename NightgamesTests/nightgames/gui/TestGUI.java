package nightgames.gui;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.combat.Combat;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.skills.SkillGroup;

public class TestGUI extends GUI {
    /**
     * 
     */
    private static final long serialVersionUID = 1739250786661411957L;

    public TestGUI() {
        
    }

    @Override public void setVisible(boolean visible) {
        // pass
    }

    // Don't use save dialog in tests
    @Override public Optional<File> askForSaveFile() {
        return Optional.empty();
    }

    @Override
    public Combat beginCombat(Player p1, NPC p2) {
        combat = new Combat(p1, p2, p1.location());
        combat.addObserver(this);
        combat.setBeingObserved(true);
        return combat;
    }

    @Override
    public void clearText() {}

    @Override
    public void message(String text) {}

    @Override
    public void chooseSkills(Combat com, Character target, List<SkillGroup> skills) {
    }

    @Override
    public void presentOptions(final List<CommandPanelOption> options) {
    }

    @Override
    public void update(Observable arg0, Object arg1) {}

    @Override
    public void endCombat() {
        combat = null;
    }
}
