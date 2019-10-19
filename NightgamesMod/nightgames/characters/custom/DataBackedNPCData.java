package nightgames.characters.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import nightgames.characters.Character;
import nightgames.characters.CharacterSex;
import nightgames.characters.Emotion;
import nightgames.characters.Growth;
import nightgames.characters.Plan;
import nightgames.characters.PreferredAttribute;
import nightgames.characters.body.Body;
import nightgames.combat.Combat;
import nightgames.items.Item;
import nightgames.items.ItemAmount;
import nightgames.items.clothing.Clothing;
import org.jtwig.JtwigTemplate;

public class DataBackedNPCData {
    List<PreferredAttribute> preferredAttributes;
    List<ItemAmount> purchasedItems;
    List<ItemAmount> startingItems;
    List<CustomStringEntry> portraits;
    Map<Emotion, Integer> moodThresholds;
    Map<String, List<CustomStringEntry>> characterLines;
    Stack<Clothing> top;
    Stack<Clothing> bottom;
    Stats stats;
    Growth growth;
    Item trophy;
    String name;
    CharacterSex sex;
    String defaultPortraitName;
    Plan plan;
    String type;
    RecruitmentData recruitment;
    Body body;
    AiModifiers aiModifiers;
    Map<CommentSituation, String> comments;
    Map<String, JtwigTemplate> templates;
    boolean isStartCharacter;

    public DataBackedNPCData() {
        preferredAttributes = new ArrayList<>();
        purchasedItems = new ArrayList<>();
        startingItems = new ArrayList<>();
        portraits = new ArrayList<>();
        body = new Body();
        moodThresholds = new HashMap<>();
        characterLines = new HashMap<>();
        top = new Stack<>();
        bottom = new Stack<>();
        stats = new Stats();
        growth = new Growth();
        trophy = Item.MiscTrophy;
        name = "Anonymous";
        sex = CharacterSex.female;
        defaultPortraitName = "";
        recruitment = new RecruitmentData();
        aiModifiers = new AiModifiers();
        comments = new HashMap<>();
        templates = new HashMap<>();
        isStartCharacter = false;
    }

    public String getName() {
        return name;
    }

    public Stats getStats() {
        return stats;
    }

    public Stack<Clothing> getTopOutfit() {
        return top;
    }

    public Stack<Clothing> getBottomOutfit() {
        return bottom;
    }

    public Growth getGrowth() {
        return growth;
    }

    public List<PreferredAttribute> getPreferredAttributes() {
        return preferredAttributes;
    }

    public List<ItemAmount> getStartingItems() {
        return startingItems;
    }

    public List<ItemAmount> getPurchasedItems() {
        return purchasedItems;
    }

    public JtwigTemplate describe() {
        return templates.get("describe");
    }

    public String getLine(String type, Combat c, Character self, Character other) {
        if (!characterLines.containsKey(type)) {
            return "";
        }
        List<CustomStringEntry> lines = characterLines.get(type);
        for (CustomStringEntry line : lines) {
            if (line.meetsRequirements(c, self, other)) {
                return line.getLine(self, other);
            }
        }
        return "";
    }

    public Item getTrophy() {
        return trophy;
    }

    public boolean checkMood(Character self, Emotion mood, int value) {
        if (moodThresholds.containsKey(mood)) {
            return value >= moodThresholds.get(mood);
        }
        return value >= 100;
    }

    public Body getBody() {
        return body;
    }

    public CharacterSex getSex() {
        return sex;
    }

    public String getPortraitName(Combat c, Character self, Character other) {
        for (CustomStringEntry line : portraits) {
            if (line.meetsRequirements(c, self, other)) {
                return line.getLine(self, other);
            }
        }
        return "";
    }

    public String getDefaultPortraitName() {
        return defaultPortraitName;
    }

    public Plan getPlan() {
        return plan;
    }

    public String getType() {
        return type;
    }

    public RecruitmentData getRecruitment() {
        return recruitment;
    }

    public AiModifiers getAiModifiers() {
        return aiModifiers;
    }

    public Map<CommentSituation, String> getComments() {
        return comments;
    }

    public boolean isStartCharacter() {
        return isStartCharacter;
    }

    public Map<Emotion, Integer> getMoodThresholds() {
        return moodThresholds;
    }

    public void setMoodThresholds(Map<Emotion, Integer> moodThresholds) {
        this.moodThresholds = moodThresholds;
    }

    public Map<String, List<CustomStringEntry>> getCharacterLines() {
        return characterLines;
    }

    public void setCharacterLines(Map<String, List<CustomStringEntry>> characterLines) {
        this.characterLines = characterLines;
    }

    public Stack<Clothing> getTop() {
        return top;
    }

    public void setTop(Stack<Clothing> top) {
        this.top = top;
    }

    public Stack<Clothing> getBottom() {
        return bottom;
    }

    public void setBottom(Stack<Clothing> bottom) {
        this.bottom = bottom;
    }

    public void setPreferredAttributes(List<PreferredAttribute> preferredAttributes) {
        this.preferredAttributes = preferredAttributes;
    }

    public void setPurchasedItems(List<ItemAmount> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }

    public void setStartingItems(List<ItemAmount> startingItems) {
        this.startingItems = startingItems;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public void setGrowth(Growth growth) {
        this.growth = growth;
    }

    public void setTrophy(Item trophy) {
        this.trophy = trophy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(CharacterSex sex) {
        this.sex = sex;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRecruitment(RecruitmentData recruitment) {
        this.recruitment = recruitment;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setAiModifiers(AiModifiers aiModifiers) {
        this.aiModifiers = aiModifiers;
    }

    public void setComments(Map<CommentSituation, String> comments) {
        this.comments = comments;
    }

    public void setIsStartCharacter(boolean isStartCharacter) {
        this.isStartCharacter = isStartCharacter;
    }
    
    public boolean getIsStartCharacter() {
        return isStartCharacter;
    }
}
