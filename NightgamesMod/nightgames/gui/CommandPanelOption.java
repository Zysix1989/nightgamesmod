package nightgames.gui;

import java.awt.event.ActionListener;

public class CommandPanelOption {

    final String displayText;
    final String toolTipText;
    final ActionListener action;

    public CommandPanelOption(String displayText, ActionListener action) {
        this.displayText = displayText;
        this.toolTipText = null;
        this.action = action;
    }

    public CommandPanelOption(String displayText, String toolTipText, ActionListener action) {
        this.displayText = displayText;
        this.toolTipText = toolTipText;
        this.action = action;
    }
}
