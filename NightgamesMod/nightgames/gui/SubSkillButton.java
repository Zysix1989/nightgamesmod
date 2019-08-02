package nightgames.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import nightgames.skills.Skill;

public class SubSkillButton extends KeyableButton {
    private static final long serialVersionUID = -3177604366435328960L;
    protected Skill action;
    private String choice;

    public SubSkillButton(final String choice, ActionListener listener) {
        super(choice);
        this.choice = choice;        
        getButton().setOpaque(true);
        getButton().setBorderPainted(false);
        getButton().setFont(new Font("Baskerville Old Face", Font.PLAIN, 18));
        getButton().setBackground(new Color(200, 200, 200));
        getButton().addActionListener(listener);
    }

    @Override
    public String getText() {
        return choice;
    }
}
