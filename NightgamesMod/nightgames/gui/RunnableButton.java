package nightgames.gui;

import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.lang3.text.WordUtils;

class RunnableButton extends KeyableButton {
    private static final long serialVersionUID = 5435929681634872672L;
    private String text;
    public RunnableButton(String text, Runnable runnable) {
        super(formatHTMLMultiline(text, ""));
        this.text = text;
        resetFontSize();

        getButton().addActionListener((evt) -> runnable.run());
    }

    public RunnableButton(String text, ActionListener action) {
        super(formatHTMLMultiline(text, ""));
        this.text = text;
        resetFontSize();

        getButton().addActionListener(action);
    }

    private void resetFontSize() {
        if (getButton().getText().contains("<br/>")) {
            getButton().setFont(new Font("Baskerville Old Face", 0, 14));
        } else {
            getButton().setFont(new Font("Baskerville Old Face", 0, 18));            
        }
    }

    private static String formatHTMLMultiline(String original, String hotkeyExtra) {
        String out = WordUtils.wrap(original.replace("<", "&lt").replace(">", "&gt"), Math.max(30, original.length() * 2 / 3), "<br/>", false);
        // do not word wrap the hotkey extras, since it looks pretty bad.
        return String.format("<html><center>%s%s</center></html>", out, hotkeyExtra);
    }

    @Override
    public String getText() {
        return text;
    }

    public void setHotkeyTextTo(String string) {
        getButton().setText(formatHTMLMultiline(text, String.format(" [%s]", string)));
        resetFontSize();
    }
}