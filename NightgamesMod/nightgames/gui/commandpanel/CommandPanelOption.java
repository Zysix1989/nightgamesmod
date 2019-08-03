package nightgames.gui.commandpanel;

import java.awt.event.ActionListener;
import nightgames.actions.Action;
import nightgames.gui.commandpanel.KeyableButton;
import nightgames.gui.commandpanel.RunnableButton;

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

    KeyableButton toButton() {
        RunnableButton button = new RunnableButton(displayText, action);
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
