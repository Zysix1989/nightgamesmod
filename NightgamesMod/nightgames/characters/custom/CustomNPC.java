package nightgames.characters.custom;

import nightgames.characters.BasePersonality;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.items.ItemAmount;
import nightgames.start.NpcConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomNPC extends BasePersonality {
    private final DataBackedNPCData data;
    private static final long serialVersionUID = -8169646189131720872L;

    public static final String TYPE_PREFIX = "CUSTOM_";

    public CustomNPC(DataBackedNPCData data){
        this(data, Optional.empty(), Optional.empty());
    }

    public CustomNPC(DataBackedNPCData data, Optional<NpcConfiguration> charConfig, Optional<NpcConfiguration> commonConfig) {
        super(data.getName(), data.isStartCharacter());
        this.data = data;
        character.isStartCharacter = this.data.isStartCharacter();
        character.plan = this.data.getPlan();
        character.mood = Emotion.confident;
        setupCharacter(this,charConfig, commonConfig);
        for (String lineType : CharacterLine.ALL_LINES) {
            if (lineType.equals(CharacterLine.DESCRIBE_LINER)) {
                this.description = data.describe();
                continue;
            }
            addLine(lineType, (c, self, other) -> data.getLine(lineType, c, self, other));
        }
        for (int i = 1; i < data.getStats().level; i++) {
            character.ding(null);
        }
    }

    @Override
    public void applyBasicStats(Character self) {
        preferredAttributes = new ArrayList<>(data.getPreferredAttributes());

        self.outfitPlan.addAll(data.getTopOutfit());
        self.outfitPlan.addAll(data.getBottomOutfit());
        self.closet.addAll(self.outfitPlan);
        self.change();
        self.att = new HashMap<>(data.getStats().attributes);
        self.clearTraits();
        data.getStats().traits.forEach(self::addTraitDontSaveData);
        self.getArousal().setMax(data.getStats().arousal);
        self.getStamina().setMax(data.getStats().stamina);
        self.getMojo().setMax(data.getStats().mojo);
        self.getWillpower().setMax(data.getStats().willpower);
        self.setTrophy(data.getTrophy());
        self.custom = true;

        try {
            self.body = data.getBody().clone(self);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        self.initialGender = data.getSex();

        for (ItemAmount i : data.getStartingItems()) {
            self.gain(i.item, i.amount);
        }

        Global.gainSkills(self);
    }

    public void setGrowth() {
        character.setGrowth(data.getGrowth());
    }

    @Override
    public void rest(int time) {
        for (ItemAmount i : data.getPurchasedItems()) {
            buyUpTo(i.item, i.amount);
        }
    }

    @Override
    public String victory(Combat c, Result flag) {
        character.getArousal().renew();
        return data.getLine("victory", c, character, c.getOpponentCharacter(character));
    }

    @Override
    public String defeat(Combat c, Result flag) {
        return data.getLine("defeat", c, character, c.getOpponentCharacter(character));
    }

    @Override
    public String draw(Combat c, Result flag) {
        return data.getLine("draw", c, character, c.getOpponentCharacter(character));
    }

    @Override
    public boolean fightFlight(Character opponent) {
        return !character.mostlyNude() || opponent.mostlyNude();
    }

    @Override
    public boolean attack(Character opponent) {
        return true;
    }

    @Override
    public String victory3p(Combat c, Character target, Character assist) {
        if (target.human()) {
            return data.getLine("victory3p", c, character, assist);
        } else {
            return data.getLine("victory3pAssist", c, character, target);
        }
    }

    @Override
    public String intervene3p(Combat c, Character target, Character assist) {
        if (target.human()) {
            return data.getLine("intervene3p", c, character, assist);
        } else {
            return data.getLine("intervene3pAssist", c, character, target);
        }
    }

    @Override
    public boolean fit() {
        return !character.mostlyNude() && character.getStamina().percent() >= 50;
    }

    @Override
    public boolean checkMood(Combat c, Emotion mood, int value) {
        return data.checkMood(character, mood, value);
    }

    @Override
    public String getType() {
        return TYPE_PREFIX + data.getType();
    }

    @Override
    public String image() {
        return data.getPortraitName(character);
    }

    public String defaultImage() {
        return data.getDefaultPortraitName();
    }

    @Override
    public RecruitmentData getRecruitmentData() {
        return data.getRecruitment();
    }

    @Override
    public AiModifiers getAiModifiers() {
        return data.getAiModifiers();
    }

    @Override
    public Map<CommentSituation, String> getComments(Combat c) {
        Map<CommentSituation, String> all = data.getComments();
        Map<CommentSituation, String> applicable = new HashMap<>();
        all.entrySet().stream().filter(e -> e.getKey().isApplicable(c, character, c.getOpponentCharacter(character)))
                        .forEach(e -> applicable.put(e.getKey(), e.getValue()));
        return applicable;
    }
    
    public DataBackedNPCData getData() {
        return data;
    }
}
