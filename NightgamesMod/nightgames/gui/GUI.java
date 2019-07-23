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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import nightgames.actions.Action;
import nightgames.actions.Locate;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.combat.Combat;
import nightgames.combat.CombatSceneChoice;
import nightgames.daytime.Activity;
import nightgames.daytime.Store;
import nightgames.global.DebugFlags;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Loot;
import nightgames.skills.Skill;
import nightgames.skills.TacticGroup;

@SuppressWarnings("unused")
public class GUI extends JFrame implements Observer {
    private static final long serialVersionUID = 451431916952047183L;
    public Combat combat;
    private Map<TacticGroup, List<SkillButton>> skills;
    private TacticGroup currentTactics;
    CommandPanel commandPanel;
    private GUIPlayerStatus playerStatus;
    private GUIPlayerBio playerBio;
    private JPanel topPanel;
    private Panel panel0;
    protected CreationGUI creation;
    private JPanel gamePanel;
    private JPanel mainPanel;
    private JPanel clothesPanel;
    private JPanel optionsPanel;
    private GUILeftPanel portraitPanel;
    private JPanel centerPanel;

    private GUIMenuBar menuBar;
    private GUIStory story;
    private GUIStoryImage storyImage;

    private int width;
    private int height;
    public int fontsize;
    private boolean skippedFeat;
    public NgsChooser saveFileChooser;
    private Box groupBox;
    private JFrame inventoryFrame;

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

        height = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.85);
        width = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.85);

        setPreferredSize(new Dimension(width, height));

        // center the window on the monitor

        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        int x1 = x / 2 - width / 2;
        int y1 = y / 2 - height / 2;

        this.setLocation(x1, y1);

        getContentPane().setLayout(new BoxLayout(getContentPane(), 1));

        // panel layouts

        // gamePanel - everything is contained within it

        gamePanel = new JPanel();
        getContentPane().add(gamePanel);
        gamePanel.setLayout(new BoxLayout(gamePanel, 1));

        // panel0 - invisible, only handles topPanel

        panel0 = new Panel();
        gamePanel.add(panel0);
        panel0.setLayout(new BoxLayout(panel0, 0));

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

        // commandPanel - visible, contains the player's command buttons
        groupBox = Box.createHorizontalBox();
        groupBox.setBackground(GUIColors.bgDark);
        groupBox.setBorder(new CompoundBorder());
        JPanel groupPanel = new JPanel();
        gamePanel.add(groupPanel);

        commandPanel = new CommandPanel(width);
        groupPanel.add(groupBox);
        groupPanel.add(commandPanel.getPanel());
        gamePanel.add(groupPanel);
        groupPanel.setBackground(GUIColors.bgDark);
        groupPanel.setBorder(new CompoundBorder());

        skills = new HashMap<>();
        clearCommand();
        currentTactics = TacticGroup.all;
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
                if (buttonOptional.isPresent()) {
                    buttonOptional.get().call();
                }
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

    public Combat beginCombat(Character player, Character enemy) {
        showPortrait();
        combat = new Combat(player, enemy, player.location());
        combat.addObserver(this);
        combat.setBeingObserved(true);
        loadPortrait(combat, player, enemy);
        showPortrait();
        return combat;
    }

    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
          Object key = keys.nextElement();
          Object value = UIManager.get (key);
          if (value != null && value instanceof javax.swing.plaf.FontUIResource)
            UIManager.put (key, f);
          }
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
    public void loadPortrait(String imagepath) {
        portraitPanel.loadPortrait(imagepath);
    }

    // portrait loader
    public void loadPortrait(Combat c, Character player, Character enemy) {
        portraitPanel.loadPortrait(c, player, enemy);
    }

    private void showMap() {
        portraitPanel.showMap();
    }

    public void showPortrait() {
        menuBar.showPortrait();
    }

    private void showNone() {
        menuBar.showNone();
    }

    // Combat GUI

    public Combat beginCombat(Character player, Character enemy, int code) {
        showPortrait();
        combat = new Combat(player, enemy, player.location(), code);
        combat.addObserver(this);
        combat.setBeingObserved(true);
        message(combat.getMessage());
        loadPortrait(combat, player, enemy);
        showPortrait();
        return combat;
    }

    // Combat spectate ???
    public void watchCombat(Combat c) {
        showPortrait();
        combat = c;
        combat.addObserver(this);
        c.setBeingObserved(true);
        loadPortrait(c, c.p1, c.p2);
        showPortrait();
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
        clearCommand();
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

    public void message(Combat c, Character character, String text) {
        story.message(character, text);
    }

    public void combatMessage(String text) {
        story.message(text);
    }

    public void clearCommand() {
        skills.clear();
        Arrays.stream(TacticGroup.values()).forEach(tactic -> skills.put(tactic, new ArrayList<>()));
        groupBox.removeAll();
        commandPanel.reset();
    }

    public void addSkill(Combat com, Skill action, Character target) {
        SkillButton btn = new SkillButton(com, action, target);
        skills.get(action.type(com).getGroup()).add(btn);
    }

    public void showSkills() {
        commandPanel.reset();
        int i = 1;
        for (TacticGroup group : TacticGroup.values()) {
            SwitchTacticsButton tacticsButton = new SwitchTacticsButton(group);
            commandPanel.register(java.lang.Character.forDigit(i % 10, 10), tacticsButton);
            groupBox.add(tacticsButton);
            groupBox.add(Box.createHorizontalStrut(4));
            i += 1;
        }
        List<SkillButton> flatList = new ArrayList<>();
        for (TacticGroup group : TacticGroup.values()) {
            skills.get(group).forEach(flatList::add);
        }
        if (currentTactics == TacticGroup.all || flatList.size() <= 6 || skills.get(currentTactics).size() == 0) {
            flatList.forEach(this::addToCommandPanel);
        } else {
            for (SkillButton button : skills.get(currentTactics)) {
                addToCommandPanel(button);
            }
        }
        Global.getMatch().pause();
        commandPanel.refresh();
    }

    public void addToCommandPanel(KeyableButton button) {
        commandPanel.add(button);
        commandPanel.refresh();
    }

    // New code should use this one
    public void addToCommandPanel(final CommandPanelOption option) {
        CommandPanelOption wrappedOption = new CommandPanelOption(option.displayText,
            option.toolTipText,
            event -> {
                clearText();
                option.action.actionPerformed(event);
                refresh();
            });
        addToCommandPanel(optionButton(wrappedOption));
    }

    public void addAction(Action action, Character user) {
        commandPanel.add(new ActionButton(action, user));
        Global.getMatch().pause();
        commandPanel.refresh();
    }

    public void addActivity(Activity act) {
        CommandPanelOption o = new CommandPanelOption(act.toString(), event -> {
            act.visit("Start");
        });
        addToCommandPanel(o);
    }

    public void next(Combat combat) {
        refresh();
        clearCommand();
        Global.getMatch().pause();
        CommandPanelOption o = new CommandPanelOption("Next", event -> {
            clearCommand();
            combat.resume();
        });
        addToCommandPanel(o);
    }

    public void choose(Combat c, Character npc, String message, CombatSceneChoice choice) {
        CommandPanelOption o = new CommandPanelOption(message, event -> {
            c.write("<br/>");
            choice.choose(c, npc);
            c.updateMessage();
            Global.gui().next(c);
        });
        addToCommandPanel(o);
    }

    public void choose(String choice) {
        commandPanel.add(new SceneButton(choice));
        commandPanel.refresh();
    }

    public void choose(Activity event, String choice) {
        commandPanel.add(eventButton(event, choice, null));
        commandPanel.refresh();
    }

    public void choose(Activity event, String choice, String tooltip) {
        commandPanel.add(eventButton(event, choice, tooltip));
        commandPanel.refresh();
    }

    public void sale(Store shop, Loot i) {
        CommandPanelOption o = new CommandPanelOption(Global.capitalizeFirstLetter(i.getName()),
            i.getDesc(),
            event -> {
                shop.visit(i.getName());
            });
        addToCommandPanel(o);
    }

    public void prompt(String message, List<KeyableButton> choices) {
        clearText();
        clearCommand();
        message(message);
        for (KeyableButton button : choices) {
            commandPanel.add(button);
        }
        commandPanel.refresh();
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
        clearCommand();
        showNone();
        menuBar.setQuitMatchEnabled(false);
        Global.endNightForSave();
        CommandPanelOption o = new CommandPanelOption("Go to sleep", event -> {
            Global.startDay();
        });
        addToCommandPanel(o);
        commandPanel.add(new SaveButton());
        commandPanel.refresh();
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

    private KeyableButton optionButton(CommandPanelOption option) {
        RunnableButton button = new RunnableButton(option.displayText, option.action);
        if (option.toolTipText != null) {
            button.getButton().setToolTipText(option.toolTipText);
        }
        return button;
    }

    private KeyableButton eventButton(Activity activity, String choice, String tooltip) {
        CommandPanelOption o = new CommandPanelOption(choice,
            tooltip,
            event -> {
                activity.visit(choice);
        });
        return optionButton(o);
    }

    public void changeClothes(Character player, Activity event, String backOption) {
        clothesPanel.removeAll();
        clothesPanel.add(new ClothesChangeGUI(player, event, backOption));
        CardLayout layout = (CardLayout) centerPanel.getLayout();
        layout.show(centerPanel, USE_CLOSET_UI);
    }

    public void removeClosetGUI() {
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

    public int nSkillsForGroup(TacticGroup group) {
        return skills.get(group).size();
    }

    public void switchTactics(TacticGroup group) {
        groupBox.removeAll();
        currentTactics = group;
        Global.gui().showSkills();
    }
}
