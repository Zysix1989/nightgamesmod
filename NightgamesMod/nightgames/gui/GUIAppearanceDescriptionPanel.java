package nightgames.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import nightgames.characters.Attribute;
import nightgames.characters.NPC;
import nightgames.global.Global;

class GUIAppearanceDescriptionPanel extends JPanel {

    private HTMLDocument document;
    private JTextPane text;

    // It's frustrating to have to write this boilerplate, but otherwise this component will steal
    // MouseEvents and it's not supposed to be selectable text.
    private class AppearanceDescriptionMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            for (MouseListener m : getParent().getMouseListeners()) {
                m.mouseClicked(mouseEvent);
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            for (MouseListener m : getParent().getMouseListeners()) {
                m.mousePressed(mouseEvent);
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            for (MouseListener m : getParent().getMouseListeners()) {
                m.mouseReleased(mouseEvent);
            }
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            for (MouseListener m : getParent().getMouseListeners()) {
                m.mouseEntered(mouseEvent);
            }
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            for (MouseListener m : getParent().getMouseListeners()) {
                m.mouseExited(mouseEvent);
            }
        }
    }

    GUIAppearanceDescriptionPanel() {
        setBackground(null);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        document = new HTMLDocument();
        text = new JTextPane(document);
        text.setContentType("text/html");

        ((DefaultCaret) text.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        text.setForeground(GUIColors.textColorLight);
        text.setEditable(false);
        text.setBackground(null);

        // Pass MouseEvents to the parent
        text.setHighlighter(null);
        text.addMouseListener(new AppearanceDescriptionMouseListener());

        add(text);
    }

    void setCharacter(NPC character) {
        text.setText("<font face='Georgia' color='white' size='4'>" +
            character.describe(Global.getPlayer().get(Attribute.Perception), Global.getPlayer())
            + "</font>");
        System.out.println(document.getLength());
    }

    @Override
    public Dimension getPreferredSize() {
        var d = super.getPreferredSize();
        d.width = getParent().getWidth();
        return d;
    }
}
