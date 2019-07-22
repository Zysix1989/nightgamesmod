package nightgames.gui;

import java.awt.event.ActionListener;

public class CommandPanelOption {

    String displayText;
    String toolTipText;
    ActionListener action;

    public CommandPanelOption(String displayText, ActionListener action) {
        this.displayText = displayText;
        this.action = action;
    }

    public CommandPanelOption(String displayText, String toolTipText, ActionListener action) {
        this.displayText = displayText;
        this.toolTipText = toolTipText;
        this.action = action;
    }
}
