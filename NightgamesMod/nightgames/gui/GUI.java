package nightgames.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.combat.Combat;
import nightgames.daytime.Activity;
import nightgames.global.DebugFlags;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.gui.commandpanel.CommandPanel;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.gui.commandpanel.KeyableButton;
import nightgames.skills.SkillGroup;


public class GUI extends JFrame implements Observer {
    private static final long serialVersionUID = 451431916952047183L;
    public Combat combat;
    private CommandPanel commandPanel;
    private GUIPlayerStatus playerStatus;
    private GUIPlayerBio playerBio;
    private JPanel topPanel;
    protected CreationGUI creation;
    private JPanel gamePanel;
    private JPanel mainPanel;
    private JPanel clothesPanel;
    private GUILeftPanel portraitPanel;
    private JPanel centerPanel;

    private GUIMenuBar menuBar;
    private GUIStory story;
    private GUIStoryImage storyImage;

    public int fontsize;
    private NgsChooser saveFileChooser;

    private static final String USE_MAIN_TEXT_UI = "MAIN_TEXT";
    private static final String USE_CLOSET_UI = "CLOSET";

    public GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e1) {
            System.err.println("Unable to set look-and-feel");
            e1.printStackTrace();
        }
        
        // frame title
        setTitle("NightGames Mod");
        setBackground(GUIColors.bgDark);
        // closing operation
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // resolution resolver

        int height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
        int width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);

        setPreferredSize(new Dimension(width, height));

        // center the window on the monitor

        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        int x1 = x / 2 - width / 2;
        int y1 = y / 2 - height / 2;

        this.setLocation(x1, y1);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // panel layouts

        // gamePanel - everything is contained within it

        gamePanel = new JPanel();
        getContentPane().add(gamePanel);
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

        // panel0 - invisible, only handles topPanel

        Panel panel0 = new Panel();
        gamePanel.add(panel0);
        panel0.setLayout(new BoxLayout(panel0, BoxLayout.X_AXIS));

        // topPanel - invisible, menus

        topPanel = new JPanel();
        panel0.add(topPanel);
        topPanel.setLayout(new GridLayout(0, 1, 0, 0));

        // mainPanel - body of GUI (not including the top bar and such)

        mainPanel = new JPanel();
        gamePanel.add(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 0));

        // portraitPanel - invisible, contains imgPanel, west panel

        portraitPanel = new GUILeftPanel();
        mainPanel.add(portraitPanel.getPanel(), BorderLayout.WEST);

        storyImage = new GUIStoryImage();
        story = new GUIStory(storyImage.getLabel());
        menuBar = new GUIMenuBar(storyImage.getPanel(), storyImage.getLabel(), portraitPanel.getPanel(), this);

        // centerPanel, a CardLayout that will flip between the main text and different UIs
        centerPanel = new JPanel(new ShrinkingCardLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        fontsize = 5;

        centerPanel.add(story.getPanel(), USE_MAIN_TEXT_UI);

        // clothesPanel - used for closet ui
        clothesPanel = new JPanel();
        clothesPanel.setLayout(new GridLayout(0, 1));
        clothesPanel.setBackground(new Color(25, 25, 50));
        centerPanel.add(clothesPanel, USE_CLOSET_UI);

        JButton debug = new JButton("Debug");
        debug.addActionListener(arg0 -> Global.getMatch().resume());

        commandPanel = new CommandPanel(width);
        gamePanel.add(commandPanel.getPanel());

        setVisible(true);
        pack();
        JPanel panel = (JPanel) getContentPane();
        panel.setFocusable(true);
        panel.addKeyListener(new KeyListener() {
            /**
             * Space bar will select the first option, unless they are in the default actions list.
             */
            @Override
            public void keyReleased(KeyEvent e) {
                Optional<KeyableButton> buttonOptional = commandPanel.getButtonForHotkey(e.getKeyChar());
                buttonOptional.ifPresent(KeyableButton::call);
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}
        });

        // Use this for making save dialogs
        saveFileChooser = new NgsChooser(this);
    }

    public Optional<File> askForSaveFile() {
        return saveFileChooser.askForSaveFile();
    }

    // combat GUI

    public Combat beginCombat(Player player, NPC enemy) {
        showPortrait();
        combat = new Combat(player, enemy, player.location());
        combat.addObserver(this);
        combat.setBeingObserved(true);
        loadPortrait(combat, enemy);
        showPortrait();
        return combat;
    }

    public void displayImage(String path, String artist) {
        storyImage.displayImage(path, artist);
    }

    public void clearImage() {
        storyImage.clearImage();
    }
    public void clearPortrait() {
        portraitPanel.clearPortrait();
    }

    // portrait loader
    public void loadPortrait(Combat c, NPC enemy) {
        portraitPanel.loadPortrait(c, enemy);
        portraitPanel.showPortrait();
    }

    private void showMap() {
        portraitPanel.showMap();
    }

    private void showPortrait() {
        portraitPanel.showPortrait();
    }

    void showNone() {
        portraitPanel.showNone();
    }

    // Combat spectate ???
    public void watchCombat(Combat c) {
        combat = c;
        combat.addObserver(this);
        c.setBeingObserved(true);
    }

    public void populatePlayer(Player player) {
        menuBar.setOptionsEnabled(true);
        getContentPane().remove(creation);
        getContentPane().add(gamePanel);
        getContentPane().validate();
        player.addObserver(this);

        playerStatus = new GUIPlayerStatus(player);
        topPanel.add(playerStatus.getPanel());

        try {
            // on macs, the aqua look and feel does not have colored progress bars.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        playerBio = new GUIPlayerBio(player, mainPanel, this);
        topPanel.add(playerBio.getPanel());

        UIManager.put("ToggleButton.select", new Color(75, 88, 102));

        removeClosetGUI();
        topPanel.validate();
        showNone();
    }

    public void createCharacter() {
        menuBar.setOptionsEnabled(false);
        getContentPane().remove(gamePanel);
        creation = new CreationGUI();
        getContentPane().add(creation);
        getContentPane().validate();
    }

    public void purgePlayer() {
        getContentPane().remove(gamePanel);
        clearText();
        showNone();
        clearImage();
        menuBar.setQuitMatchEnabled(false);
        combat = null;
        topPanel.removeAll();
    }

    public void clearText() {
        story.clearText();
    }

    public void message(String text) {
        story.message(text);
    }

    public void message(Character speaker, String text) {
        story.message(speaker, text);
    }


    public void chooseSkills(Combat com, Character target, List<SkillGroup> skills) {
        commandPanel.chooseSkills(com, target, skills);
    }

    private CommandPanelOption clearingOption(final CommandPanelOption option) {
        return option.wrap(
                event -> clearText(),
                event -> refresh());
    }

    // New code should use this one
    public void presentOptions(final List<CommandPanelOption> options) {
        commandPanel.present(options.stream().map(this::clearingOption).collect(Collectors.toList()));
    }

    public void prompt(String message, List<CommandPanelOption> choices) {
        clearText();
        message(message);
        presentOptions(choices);
    }

    public void promptWithSave(String message, List<CommandPanelOption> choices) {
        message(message);
        choices = choices.stream().map(this::clearingOption)
            .map(option -> option.wrap(event -> commandPanel.reset(), event -> {}))
            .collect(Collectors.toList());
        choices.add(saveOption());
        commandPanel.presentNoReset(choices);
    }

    public void endCombat() {
        if (Global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("End Combat");
        }
        combat = null;
        clearText();
        clearImage();
        showMap();
        Global.getMatch().resume();
    }

    // Night match initializer

    public void startMatch() {
        menuBar.setQuitMatchEnabled(true);
        showMap();
    }

    public void endMatch() {
        if (Global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("Match end");
        }
        combat = null;
        showNone();
        menuBar.setQuitMatchEnabled(false);
        Global.endNightForSave();
        List<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption("Go to sleep", event -> Global.startDay()));
        promptWithSave("", options);
    }

    public void refresh() {
        playerStatus.refresh();
        playerBio.refresh();
        portraitPanel.refresh();
    }

    public void displayStatus() {
        playerBio.displayStatus();
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        refresh();
        if (combat != null) {
            if (combat.combatMessageChanged) {
                message(combat.getMessage());
                combat.combatMessageChanged = false;
            }
        }
    }

    public void changeClothes(Character player, Activity event, String backOption) {
        clothesPanel.removeAll();
        clothesPanel.add(new ClothesChangeGUI(player, event, backOption));
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, USE_CLOSET_UI);
    }

    void removeClosetGUI() {
        if (Global.isDebugOn(DebugFlags.DEBUG_GUI)) {
            System.out.println("remove closet gui");
        }
        clothesPanel.removeAll();
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, USE_MAIN_TEXT_UI);
    }

    public void systemMessage(String string) {
        if (Global.checkFlag(Flag.systemMessages)) {
            message(string);
        }
    }

    private static CommandPanelOption saveOption() {
        return new CommandPanelOption(
            "Save",
            event -> Global.saveWithDialog()
        );
    }

    public static CommandPanelOption sceneOption(String displayText) {
        return new CommandPanelOption(displayText, event -> Global.current.respond(displayText));
    }
}
