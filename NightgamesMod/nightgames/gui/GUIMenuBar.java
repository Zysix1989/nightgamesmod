package nightgames.gui;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.Hashtable;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import nightgames.debug.DebugGUIPanel;
import nightgames.global.*;

class GUIMenuBar {

    private JPanel optionsPanel;
    private JRadioButton rdnormal;
    private JRadioButton rddumb;
    private JRadioButton rdeasy;
    private JRadioButton rdhard;
    private JRadioButton rdMsgFull;
    private JRadioButton rdMsgOff;
    private JRadioButton rdMsgBasic;
    private JRadioButton rdAutoNextOn;
    private JRadioButton rdAutoNextOff;
    private JRadioButton rdautosaveon;
    private JRadioButton rdautosaveoff;
    private JRadioButton rdporon;
    private JRadioButton rdporoff;
    private JRadioButton rdimgon;
    private JRadioButton rdimgoff;
    private JButton rdfntsmall;
    private JButton rdfntnorm;

    private JButton rdnfntlrg;
    private JButton rdnfntsmall;

    public int fontsize;

    private JSlider malePrefSlider;
    private JMenuItem mntmQuitMatch;
    private JMenuItem mntmOptions;

    private final static String USE_PORTRAIT = "PORTRAIT";
    private final static String USE_MAP = "MAP";
    private final static String USE_NONE = "NONE";
    private static final String USE_MAIN_TEXT_UI = "MAIN_TEXT";
    private static final String USE_CLOSET_UI = "CLOSET";


    private JPanel portraitPanel;

    GUIMenuBar(JPanel imgPanel, JLabel imgLabel, JPanel portraitPanel, GUI gui) {
        this.portraitPanel = portraitPanel;
        JMenuBar menuBar = new JMenuBar();
        gui.setJMenuBar(menuBar);

        // menu bar - new game

        JMenuItem mntmNewgame = new JMenuItem("New Game");

        //mntmNewgame.setForeground(Color.WHITE);
        //mntmNewgame.setBackground(GUIColors.bgGrey);
        mntmNewgame.setHorizontalAlignment(SwingConstants.CENTER);

        mntmNewgame.addActionListener(arg0 -> {
            if (Global.inGame()) {
                int result = JOptionPane.showConfirmDialog(gui,
                    "Do you want to restart the game? You'll lose any unsaved progress.", "Start new game?",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    Global.reset();
                }
            }
        });

        menuBar.add(mntmNewgame);

        // menu bar - load game - can't change because can't figure out where
        // the frame is with swing

        JMenuItem mntmLoad = new JMenuItem("Load"); // Initializer

        //mntmLoad.setForeground(Color.WHITE); // Formatting
        //mntmLoad.setBackground(GUIColors.bgGrey);
        mntmLoad.setHorizontalAlignment(SwingConstants.CENTER);

        mntmLoad.addActionListener(arg0 -> Global.loadWithDialog());

        menuBar.add(mntmLoad);

        // menu bar - options

        mntmOptions = new JMenuItem("Options");
        //mntmOptions.setForeground(Color.WHITE);
        //mntmOptions.setBackground(GUIColors.bgGrey);

        menuBar.add(mntmOptions);

        // options submenu creator

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(0, 3, 0, 0));

        // AILabel - options submenu - visible

        JLabel AILabel = new JLabel("AI Mode");
        ButtonGroup ai = new ButtonGroup();
        rdnormal = new JRadioButton("Normal");
        rddumb = new JRadioButton("Easier");
        ai.add(rdnormal);
        ai.add(rddumb);
        optionsPanel.add(AILabel);
        optionsPanel.add(rdnormal);
        optionsPanel.add(rddumb);

        // difficultyLabel - options submenu - visible

        JLabel difficultyLabel = new JLabel("NPC Bonuses (Mainly XP)");
        ButtonGroup diff = new ButtonGroup();
        rdeasy = new JRadioButton("Off");
        rdhard = new JRadioButton("On");
        diff.add(rdeasy);
        diff.add(rdhard);
        optionsPanel.add(difficultyLabel);
        optionsPanel.add(rdeasy);
        optionsPanel.add(rdhard);

        // systemMessageLabel - options submenu - visible

        JLabel systemMessageLabel = new JLabel("System Messages");
        ButtonGroup sysMsgG = new ButtonGroup();
        JPanel hackPanel = new JPanel();
        hackPanel.setLayout(new GridLayout(1, 2, 0, 0));
        rdMsgFull = new JRadioButton("Full");
        rdMsgBasic = new JRadioButton("Basic");
        rdMsgOff = new JRadioButton("Off");
        sysMsgG.add(rdMsgFull);
        sysMsgG.add(rdMsgBasic);
        sysMsgG.add(rdMsgOff);
        optionsPanel.add(systemMessageLabel);
        hackPanel.add(rdMsgFull);
        hackPanel.add(rdMsgBasic);
        optionsPanel.add(hackPanel);
        optionsPanel.add(rdMsgOff);

        JLabel autoNextLabel = new JLabel("Fast Combat Display");
        ButtonGroup autoNextG = new ButtonGroup();
        rdAutoNextOn = new JRadioButton("On");
        rdAutoNextOff = new JRadioButton("Off");
        autoNextG.add(rdAutoNextOn);
        autoNextG.add(rdAutoNextOff);
        optionsPanel.add(autoNextLabel);
        optionsPanel.add(rdAutoNextOn);
        optionsPanel.add(rdAutoNextOff);

        // autosave - options submenu - visible -(not currently working?)

        JLabel lblauto = new JLabel("Autosave (saves to auto.ngs)");
        ButtonGroup auto = new ButtonGroup();
        rdautosaveon = new JRadioButton("on");
        rdautosaveoff = new JRadioButton("off");
        auto.add(rdautosaveon);
        auto.add(rdautosaveoff);
        optionsPanel.add(lblauto);
        optionsPanel.add(rdautosaveon);
        optionsPanel.add(rdautosaveoff);

        // portraitsLabel - options submenu - visible

        JLabel portraitsLabel = new JLabel("Portraits");

        // portraits - options submenu - visible

        ButtonGroup portraitsButton = new ButtonGroup();

        // rdpron / rdporoff - options submenu - visible

        rdporon = new JRadioButton("on");
        rdporoff = new JRadioButton("off");
        portraitsButton.add(rdporon);
        portraitsButton.add(rdporoff);
        optionsPanel.add(portraitsLabel);
        optionsPanel.add(rdporon);
        optionsPanel.add(rdporoff);

        // imageLabel - options submenu - visible
        JLabel imageLabel = new JLabel("Images");
        ButtonGroup image = new ButtonGroup();
        rdimgon = new JRadioButton("on");
        rdimgoff = new JRadioButton("off");
        image.add(rdimgon);
        image.add(rdimgoff);
        optionsPanel.add(imageLabel);
        optionsPanel.add(rdimgon);
        optionsPanel.add(rdimgoff);

        // fontSizeLabel - options submenu - visible
        JLabel fontSizeLabel = new JLabel("Font Size");
        ButtonGroup size = new ButtonGroup();
        rdfntnorm = new JButton("Smaller");
        rdfntnorm.addActionListener(a -> {
            fontsize = Global.clamp(fontsize - 1, 1, 7);
            Global.gui().message("Text Size changed to " + fontsize);
        });

        size.add(rdfntnorm);

        optionsPanel.add(fontSizeLabel);
        optionsPanel.add(rdfntnorm);

        JLabel pronounLabel = new JLabel("Human Pronoun Usage");
        ButtonGroup pronoun = new ButtonGroup();
        JRadioButton rdPronounBody = new JRadioButton("Based on Anatomy");
        JRadioButton rdPronounFemale = new JRadioButton("Always Female");
        pronoun.add(rdPronounBody);
        pronoun.add(rdPronounFemale);
        optionsPanel.add(pronounLabel);
        optionsPanel.add(rdPronounBody);
        optionsPanel.add(rdPronounFemale);

        JLabel npcPronounLabel = new JLabel("NPC Pronoun Usage");
        ButtonGroup npcPronoun = new ButtonGroup();
        JRadioButton rdNPCPronounBody = new JRadioButton("Based on Anatomy");
        JRadioButton rdNPCPronounFemale = new JRadioButton("Always Female");
        npcPronoun.add(rdNPCPronounBody);
        npcPronoun.add(rdNPCPronounFemale);
        optionsPanel.add(npcPronounLabel);
        optionsPanel.add(rdNPCPronounBody);
        optionsPanel.add(rdNPCPronounFemale);

        // m/f preference (no (other) males in the games yet... good for
        // modders?)

        // malePrefLabel - options submenu - visible
        JLabel malePrefLabel = new JLabel("Female vs. Male Preference");
        optionsPanel.add(malePrefLabel);
        malePrefSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 10, 1);
        malePrefSlider.setMajorTickSpacing(5);
        malePrefSlider.setMinorTickSpacing(1);
        malePrefSlider.setPaintTicks(true);
        malePrefSlider.setPaintLabels(true);
        malePrefSlider.setLabelTable(new Hashtable<Integer, JLabel>() {
            /**
             *
             */
            private static final long serialVersionUID = -4212836698571224221L;

            {
                put(0, new JLabel("Female"));
                put(5, new JLabel("Mixed"));
                put(10, new JLabel("Male"));
            }
        });
        malePrefSlider.setValue(Math.round(Global.getValue(Flag.malePref)));
        malePrefSlider.setToolTipText(
            "This setting affects the gender your opponents will gravitate towards once that"
                + " option becomes available.");
        malePrefSlider
            .addChangeListener(e -> Global.setCounter(Flag.malePref, malePrefSlider.getValue()));

        // malePrefPanel - options submenu - visible
        optionsPanel.add(malePrefSlider);
        mntmOptions.addActionListener(arg0 -> {
            if (Global.checkFlag(Flag.systemMessages)) {
                rdMsgFull.setSelected(true);
            } else if (Global.checkFlag(Flag.basicSystemMessages)) {
                rdMsgBasic.setSelected(true);
            } else {
                rdMsgOff.setSelected(true);
            }

            if (Global.checkFlag(Flag.AutoNext)) {
                rdAutoNextOn.setSelected(true);
            } else {
                rdAutoNextOff.setSelected(true);
            }

            if (Global.checkFlag(Flag.hardmode)) {
                rdhard.setSelected(true);
            } else {
                rdeasy.setSelected(true);
            }

            if (Global.checkFlag(Flag.dumbmode)) {
                rddumb.setSelected(true);
            } else {
                rdnormal.setSelected(true);
            }
            if (Global.checkFlag(Flag.autosave)) {
                rdautosaveon.setSelected(true);
            } else {
                rdautosaveoff.setSelected(true);
            }
            if (Global.checkFlag(Flag.noportraits)) {
                rdporoff.setSelected(true);
            } else {
                rdporon.setSelected(true);
            }
            if (Global.checkFlag(Flag.noimage)) {
                rdimgoff.setSelected(true);
            } else {
                rdimgon.setSelected(true);
            }
            if (Global.checkFlag(Flag.largefonts)) {

                rdnfntlrg.setSelected(true);
            } else if (Global.checkFlag(Flag.smallfonts)) {
                rdnfntsmall.setSelected(true);

            } else {
                rdfntnorm.setSelected(true);
            }
            if (Global.checkFlag(Flag.NPCFemalePronounsOnly)) {
                rdNPCPronounFemale.setSelected(true);
            } else {
                rdNPCPronounBody.setSelected(true);
            }
            if (Global.checkFlag(Flag.PCFemalePronounsOnly)) {
                rdPronounFemale.setSelected(true);
            } else {
                rdPronounBody.setSelected(true);
            }
            malePrefSlider.setValue(Math.round(Global.getValue(Flag.malePref)));
            int result = JOptionPane
                .showConfirmDialog(gui, optionsPanel, "Options", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Global.setFlag(Flag.systemMessages, rdMsgFull.isSelected());
                Global.setFlag(Flag.basicSystemMessages, rdMsgBasic.isSelected());
                Global.setFlag(Flag.AutoNext, rdAutoNextOn.isSelected());
                Global.setFlag(Flag.dumbmode, !rdnormal.isSelected());
                Global.setFlag(Flag.hardmode, rdhard.isSelected());
                Global.setFlag(Flag.autosave, rdautosaveon.isSelected());
                Global.setFlag(Flag.noportraits, rdporoff.isSelected());
                Global.setFlag(Flag.NPCFemalePronounsOnly, rdNPCPronounFemale.isSelected());
                Global.setFlag(Flag.PCFemalePronounsOnly, rdPronounFemale.isSelected());
                if (!rdporon.isSelected()) {
                    Global.gui().showNone();
                }
                if (rdimgon.isSelected()) {
                    Global.unflag(Flag.noimage);
                } else {
                    Global.flag(Flag.noimage);
                    if (imgLabel != null) {
                        imgPanel.remove(imgLabel);
                    }
                    imgPanel.repaint();
                }/*
                if (rdfntlrg.isSelected()) {
                    Global.unflag(Flag.smallfonts);
                    Global.flag(Flag.largefonts);
                    fontsize = 6;
                } else if (rdfntsmall.isSelected()) {
                    Global.flag(Flag.smallfonts);
                    Global.unflag(Flag.largefonts);
                    fontsize = 4;
                } else {
                    Global.unflag(Flag.smallfonts);
                    Global.unflag(Flag.largefonts);
                    fontsize = 5;
                }*/
            }
        });

        // menu bar - credits

        JMenuItem mntmCredits = new JMenuItem("Credits");
        //mntmCredits.setForeground(Color.WHITE);
        //mntmCredits.setBackground(GUIColors.bgGrey);
        menuBar.add(mntmCredits);

        // menu bar - quit match

        mntmQuitMatch = new JMenuItem("Quit Match");
        mntmQuitMatch.setEnabled(false);
        //mntmQuitMatch.setForeground(Color.WHITE);
        //mntmQuitMatch.setBackground(GUIColors.bgGrey);
        mntmQuitMatch.addActionListener(arg0 -> {
            int result = JOptionPane.showConfirmDialog(gui,
                "Do you want to quit for the night? Your opponents will continue to fight and gain exp.",
                "Retire early?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Global.getMatch().quit();
            }
        });
        menuBar.add(mntmQuitMatch);

        mntmCredits.addActionListener(arg0 -> {
            JPanel panel = new JPanel();
            panel.add(new JLabel("<html>Night Games created by The Silver Bard<br/>"
                + "Reyka and Samantha and a whole lot of stuff created by DNDW<br/>" + "Upgraded Strapon created by MotoKuchoma<br/>"
                + "Strapon victory scenes created by Legion<br/>" + "Advanced AI by Jos<br/>"
                + "Magic Training scenes by Legion<br/>" + "Jewel 2nd Victory scene by Legion<br/>"
                + "Video Games scenes 1-9 by Onyxdime<br/>"
                + "Kat Penetration Victory and Defeat scenes by Onyxdime<br/>"
                + "Kat Non-Penetration Draw scene by Onyxdime<br/>"
                + "Mara/Angel threesome scene by Onyxdime<br/>"
                + "Footfetish expansion scenes by Sakruff<br/>"
                + "Mod by Nergantre<br/>"
                + "A ton of testing by Bronzechair"
                + "Bugfixes, edits, and some Maya text by DarkSinfulMage</html>"));
            Object[] options = {"OK", "DEBUG"};
            Object[] okOnly = {"OK"};
            int results = JOptionPane.showOptionDialog(gui, panel, "Credits", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (results == 1 && Global.inGame()) {
                JPanel debugPanel = new DebugGUIPanel();
                JOptionPane.showOptionDialog(gui, debugPanel, "Debug", JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.INFORMATION_MESSAGE, null, okOnly, okOnly[0]);
            } else if (results == 1) {
                JOptionPane.showOptionDialog(gui, "Not in game", "Debug", JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.INFORMATION_MESSAGE, null, okOnly, okOnly[0]);
            }
        });

    }

    void setOptionsEnabled(boolean b) {
        mntmOptions.setEnabled(b);
    }

    void setQuitMatchEnabled(boolean b) {
        mntmQuitMatch.setEnabled(b);
    }
}