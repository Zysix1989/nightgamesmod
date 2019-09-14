package nightgames.gui.commandpanel;

import java.awt.event.ActionListener;

public class CommandPanelOption {

    private final String displayText;
    private final String toolTipText;
    private final ActionListener action;

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

    CommandPanelButton toButton() {
        var button = CommandPanelButton.BasicButton(displayText, action);
        if (toolTipText != null) {
            button.getButton().setToolTipText(toolTipText);
        }
        return button;
    }

    public CommandPanelOption wrap(ActionListener before, ActionListener after) {
        return new CommandPanelOption(
            displayText,
            toolTipText,
            event -> {
                before.actionPerformed(event);
                action.actionPerformed(event);
                after.actionPerformed(event);
            });
    }
}
