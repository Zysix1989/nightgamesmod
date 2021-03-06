package nightgames.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import nightgames.characters.Character;
import nightgames.global.*;
import nightgames.utilities.DebugHelper;

class GUIStory {

    private JTextPane textPane;
    private JPanel panel;
    private Integer fontSize;

    GUIStory(JLabel imageLabel) {

        int height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
        int width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);

        JScrollPane textScroll = new JScrollPane();

        textPane = new JTextPane();
        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textPane.setForeground(GUIColors.textColorLight);
        textPane.setBackground(GUIColors.bgLight);
        textPane.setPreferredSize(new Dimension(width, 400));
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textScroll.setViewportView(textPane);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(imageLabel);
        panel.add(textScroll);
        panel.setBackground(GUIColors.bgDark);

        fontSize = 5;
    }

    JPanel getPanel() {
        return panel;
    }

    void clearText() {
        textPane.setText("");
    }

    void message(String text) {
        if (text.trim().length() == 0) {
            return;
        }
        text = Global.capitalizeFirstLetter(text);
        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(),
                "<font face='Georgia' color='white' size='" + fontSize + "'>" + text
                    + "</font><br/>",
                0, 0, null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
    }

    void message(Character character, String text) {
        if (character != null) {
            text = Global.colorizeMessage(character, text);
        }
        message(text);
    }
}



