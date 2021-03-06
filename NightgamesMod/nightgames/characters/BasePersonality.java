package nightgames.characters;

import nightgames.areas.Area;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.mods.pitcher.CockMod;
import nightgames.characters.custom.AiModifiers;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.custom.CommentSituation;
import nightgames.characters.custom.RecruitmentData;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Dialog;
import nightgames.pet.arms.ArmManager;
import nightgames.skills.Skill;
import nightgames.start.NpcConfiguration;
import nightgames.status.Disguised;
import nightgames.status.Stsflag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.Addiction.Severity;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.Serializable;
import java.util.*;

public abstract class BasePersonality implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2279220186754458082L;
    private String type;
    public NPC character;
    protected List<PreferredAttribute> preferredAttributes;
    protected Optional<CockMod> preferredCockMod;
    protected AiModifiers mods;
    protected Map<String, List<CharacterLine>> lines;
    protected JtwigTemplate description;
    
    protected int dominance=0;
    protected int minDominance=0;

    protected BasePersonality(String name, boolean isStartCharacter) {
        // Make the built-in character
        type = getClass().getSimpleName();
        character = new NPC(name, 1, this);
        character.isStartCharacter = isStartCharacter;
        preferredCockMod = Optional.empty();
        preferredAttributes = new ArrayList<PreferredAttribute>();
        lines = new HashMap<>();
    }

    protected static void setupCharacter(BasePersonality p, Optional<NpcConfiguration> charConfig,
        Optional<NpcConfiguration> commonConfig) {
        p.setGrowth();
        p.applyBasicStats(p.character);

        // Apply config changes
        Optional<NpcConfiguration> mergedConfig = NpcConfiguration.mergeOptionalNpcConfigs(charConfig, commonConfig);
        mergedConfig.ifPresent(cfg -> cfg.apply(p.character));

        if (Global.checkFlag("FutaTime") && p.character.initialGender == CharacterSex.female) {
            p.character.initialGender = CharacterSex.herm;
        }
        p.character.body.makeGenitalOrgans(p.character.initialGender);
        p.character.body.finishBody(p.character.initialGender);
        for (int i = 1; i < p.character.getProgression().getLevel(); i++) {
            p.character.getGrowth().levelUp(p.character);
        }
        p.character.distributePoints(p.preferredAttributes);
        p.character.getGrowth().addOrRemoveTraits(p.character);
    }

    public void setCharacter(NPC c) {
        this.character = c;
    }

    abstract public void setGrowth();

    public void rest(int time) {
        if (preferredCockMod.isPresent() && character.getProgression().getRank() > 0) {
            Optional<BodyPart> optDick = Optional.ofNullable(character.body.getRandomCock());
            if (optDick.isPresent()) {
                CockPart part = (CockPart) optDick.get();
                part.addMod(preferredCockMod.get());
            }
        }
        for (Addiction addiction : character.getAddictions()) {
            if (addiction.atLeast(Severity.LOW)) {
                Character cause = addiction.getCause();
                int affection = character.getAffection(cause);
                int affectionDelta = affection - character.getAffection(Global.getPlayer());
                // day 10, this would be (10 + sqrt(10) * 5) * .7 = 18 affection lead to max
                // day 60, this would be (10 + sqrt(70) * 5) * .7 = 36 affection lead to max
                double chanceToDoDaytime = .25 + (addiction.getMagnitude() / 2) + Global.clamp((affectionDelta / (10 + Math.sqrt(Global.getDate()) * 5)), -.7, .7);
                if (Global.randomdouble() < chanceToDoDaytime) {
                    addiction.aggravate(null, Addiction.MED_INCREASE);
                    addiction.flagDaytime();
                    character.gainAffection(cause, 1);
                }
            }
        }
    }

    public void buyUpTo(Item item, int number) {
        while (character.money > item.getPrice() && character.count(item) < number) {
            character.money -= item.getPrice();
            character.gain(item);
        }
    }

    public String getType() {
        return type;
    }

    public Skill act(HashSet<Skill> available, Combat c) {
        HashSet<Skill> tactic;
        Skill chosen;
        ArrayList<WeightedSkill> priority = Decider.parseSkills(available, c, character);
        if (!Global.checkFlag(Flag.dumbmode)) {
            chosen = Decider.prioritizeNew(character, priority, c);
        } else {
            chosen = character.prioritize(priority);
        }
        if (chosen == null) {
            tactic = available;
            Skill[] actions = tactic.toArray(new Skill[tactic.size()]);
            return actions[Global.random(actions.length)];
        } else {
            return chosen;
        }
    }

    public Action.Instance move(Collection<Action.Instance> available, Collection<Area> radar) {
        return Decider.parseMoves(available, radar, character);
    }

    public String image() {
        return character.getTrueName().toLowerCase()
                        + "/portraits/" + character.mood.name() + ".jpg";
    }

    public void ding(Character self) {
        self.getGrowth().levelUp(self);
        onLevelUp(self);
        self.distributePoints(preferredAttributes);
    }

    public List<PreferredAttribute> getPreferredAttributes() {
        return preferredAttributes;
    }

    protected void onLevelUp(Character self) {
        // NOP
    }

    public NPC getCharacter() {
        return character;
    }

    public RecruitmentData getRecruitmentData() {
        return null;
    }

    public AiModifiers getAiModifiers() {
        if (mods == null)
            resetAiModifiers();
        return mods;
    }

    public void resetAiModifiers() {
        mods = AiModifiers.getDefaultModifiers(getType());
    }
    
    public String resist3p(Combat c, Character target, Character assist) {
        return null;
    }

    public Map<CommentSituation, String> getComments(Combat c) {
        Map<CommentSituation, String> all = CommentSituation.getDefaultComments(getType());
        Map<CommentSituation, String> applicable = new HashMap<>();
        all.entrySet()
           .stream()
           .filter(e -> e.getKey()
                         .isApplicable(c, character, c.getOpponentCharacter(character)))
           .forEach(e -> applicable.put(e.getKey(), e.getValue()));
        return applicable;
    }

    public abstract String victory(Combat c, Result flag);

    public abstract String defeat(Combat c, Result flag);

    public abstract String victory3p(Combat c, Character target, Character assist);

    public abstract String intervene3p(Combat c, Character target, Character assist);

    public abstract String draw(Combat c, Result flag);

    public abstract boolean fightFlight(Character opponent);

    public abstract boolean attack(Character opponent);

    public abstract boolean fit();

    public abstract boolean checkMood(Combat c, Emotion mood, int value);

    public void resolveOrgasm(Combat c, NPC self, Character opponent, BodyPart selfPart,
        BodyPart opponentPart, int times,
        int totalTimes) {
        // no op
    }

    public void eot(Combat c, Character opponent) {
        // noop
    }

    public abstract void applyBasicStats(Character self);

    public void addLine(String lineType, CharacterLine line) {
        if (lineType.equals(CharacterLine.DESCRIBE_LINER)) {
            throw new IllegalArgumentException();
        }
        lines.computeIfAbsent(lineType, type -> new ArrayList<>());
        lines.get(lineType).add(line);
    }

    public String getRandomLineFor(String lineType, Combat c, Character other) {
        Map<String, List<CharacterLine>> lines = this.lines;
        Disguised disguised = (Disguised) character.getStatus(Stsflag.disguised);
        if (disguised != null) {
            lines = disguised.getTarget().getLines();
        }
        if (lineType.equals(CharacterLine.DESCRIBE_LINER)) {
            throw new IllegalArgumentException();
        }
        return Global.format(Global.pickRandom(lines.get(lineType)).orElse((cb, sf, ot) -> "").getLine(c, character, other), character, other);
    }

    String describe(Character self) {
        var model = JtwigModel.newModel()
            .with("self", self);
        return description.render(model).replace(System.lineSeparator(), " ");
    }

    void initializeArms(ArmManager m) { }

    Optional<ArmManager> getArmManager() {
        return Optional.empty();
    }

    Dialog makeDialog() {
        return new Dialog() {
            @Override
            public void intrudeInCombat(Combat c, Character target, Character assist) {
                c.write(intervene3p(c, target, assist));
            }

            @Override
            public void assistedByIntruder(Combat c, Character target, Character assist) {
                c.updateAndClearMessage();
                c.write(victory3p(c, target, assist));
            }
        };
    }
}
