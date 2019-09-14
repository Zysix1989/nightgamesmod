package nightgames.gui.commandpanel;

import java.awt.Font;
import java.awt.event.ActionListener;
import org.apache.commons.lang3.text.WordUtils;

class RunnableButton extends KeyableButton {
    private static final long serialVersionUID = 5435929681634872672L;

    RunnableButton(String text, ActionListener action) {
        super(formatHTMLMultiline(text));
        resetFontSize();

        getButton().addActionListener(action);
    }

    private void resetFontSize() {
        if (getButton().getText().contains("<br/>")) {
            getButton().setFont(new Font("Baskerville Old Face", Font.PLAIN, 14));
        } else {
            getButton().setFont(new Font("Baskerville Old Face", Font.PLAIN, 18));
        }
    }

    private static String formatHTMLMultiline(String original) {
        String out = WordUtils.wrap(original.replace("<", "&lt").replace(">", "&gt"), Math.max(30, original.length() * 2 / 3), "<br/>", false);
        // do not word wrap the hotkey extras, since it looks pretty bad.
        return String.format("<html><center>%s</center></html>", out);
    }
}