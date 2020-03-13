package nightgames.characters;

import nightgames.areas.Area;
import nightgames.characters.body.*;
import nightgames.characters.body.BreastsPart.Size;
import nightgames.characters.body.mods.ExternalTentaclesMod;
import nightgames.characters.body.mods.GooeySkinMod;
import nightgames.characters.body.mods.catcher.GooeyMod;
import nightgames.characters.body.mods.pitcher.SlimyCockMod;
import nightgames.characters.corestats.ArousalStat;
import nightgames.characters.corestats.StaminaStat;
import nightgames.characters.corestats.WillpowerStat;
import nightgames.combat.Combat;
import nightgames.combat.CombatSceneChoice;
import nightgames.daytime.*;
import nightgames.global.Global;
import nightgames.global.Scene;
import nightgames.grammar.Person;
import nightgames.grammar.SingularSecondPerson;
import nightgames.gui.GUI;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.Clothing;
import nightgames.match.*;
import nightgames.match.ftc.FTCMatch;
import nightgames.skills.*;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Behind;
import nightgames.stance.Neutral;
import nightgames.stance.Position;
import nightgames.start.PlayerConfiguration;
import nightgames.status.PlayerSlimeDummy;
import nightgames.status.Status;
import nightgames.status.addiction.Addiction;
import nightgames.trap.Trap;

import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.Collectors;

public class Player extends Character {

    public GUI gui;
    int traitPoints;
    private int levelsToGain;
    private boolean skippedFeat;

    public Player(String name, GUI gui) {
        this(name, gui, CharacterSex.male, Optional.empty(), new ArrayList<>(), new HashMap<>());
    }

    // TODO(Ryplinn): This initialization pattern is very close to that of BasePersonality. I think it makes sense to make NPC the primary parent of characters instead of BasePersonality.
    public Player(String name, GUI gui, CharacterSex sex, Optional<PlayerConfiguration> config,
        List<Trait> pickedTraits,
                    Map<Attribute, Integer> selectedAttributes) {
        super(name, 1);
        this.gui = gui;
        initialGender = sex;
        levelsToGain = 0;
        applyBasicStats(this);
        setGrowth();

        body.makeGenitalOrgans(initialGender);

        config.ifPresent(this::applyConfigStats);
        finishCharacter(pickedTraits, selectedAttributes);
    }

    @Override
    public void finishClone(Character other) {
        super.finishClone(other);
    }

    public void applyBasicStats(Character self) {
        self.getStamina().setMax(80);
        self.getArousal().setMax(80);
        self.getWillpower().setMax(self.willpower.max());
        self.availableAttributePoints = 0;
        self.setTrophy(Item.PlayerTrophy);
    }

    private void applyConfigStats(PlayerConfiguration config) {
        config.apply(this);
    }

    private void finishCharacter(List<Trait> pickedTraits, Map<Attribute, Integer> selectedAttributes) {
        pickedTraits.forEach(this::addTraitDontSaveData);
        att.putAll(selectedAttributes);
        change();
        body.finishBody(initialGender);
    }

    private static Growth newGrowth() {
        var stamina = new CoreStatGrowth<StaminaStat>(20, 0);
        var arousal = new CoreStatGrowth<ArousalStat>(40, 0);
        var willpower = new CoreStatGrowth<WillpowerStat>(0, 0);
        return new Growth(new CoreStatsGrowth(stamina, arousal, willpower));
    }

    public void setGrowth() {
        setGrowth(newGrowth());
    }

    public String describeStatus() {
        StringBuilder b = new StringBuilder();
        if (gui.combat != null && (gui.combat.getP1Character().human() || gui.combat.getP2Character().human())) {
            body.describeBodyText(b, gui.combat.getOpponentCharacter(this), false);
        } else {
            body.describeBodyText(b, Global.getCharacterByType("Angel"), false);
        }
        if (getTraits().size() > 0) {
            b.append("<br/>Traits:<br/>");
            List<Trait> traits = new ArrayList<>(getTraits());
            traits.removeIf(t -> t.isOverridden(this));
            traits.sort(Comparator.comparing(Trait::toString));
            b.append(traits.stream()
                           .map(Object::toString)
                           .collect(Collectors.joining(", ")));
        }
        if (status.size() > 0) {
            b.append("<br/><br/>Statuses:<br/>");
            List<Status> statuses = new ArrayList<>(status);
            statuses.sort(Comparator.comparing(status -> status.name));
            b.append(statuses.stream()
                             .map(s -> s.name)
                             .collect(Collectors.joining(", ")));
        }
        return b.toString();
    }

    @Override
    public String describe(int per, Character observer) {
        String description = "<i>";
        for (Status s : status) {
            description = description + s.describe(observer) + "<br/>";
        }
        description = description + "</i>";
        description = description + outfit.describe(this);
        if (per >= 5 && status.size() > 0) {
            description += "<br/>List of statuses:<br/><i>";
            description += status.stream().map(Status::toString).collect(Collectors.joining(", "));
            description += "</i><br/>";
        }
        description += Stage.describe(this);
        
        return description;
    }

    @Override
    public boolean act(Combat c, Character target) {
        pickSkills(c, target);
        return true;
    }

    /**Adds skills to the GUI*/
    private void pickSkills(Combat c, Character target) {
        HashSet<Skill> available = new HashSet<>();
        HashSet<Skill> cds = new HashSet<>();
        for (Skill a : getSkills()) {
            if (Skill.isUsable(c, a)) {
                if (cooldownAvailable(a)) {
                    available.add(a);
                } else {
                    cds.add(a);
                }
            }
        }
        HashMap<Tactics, HashSet<Skill>> skillMap = new HashMap<>();
        Skill.filterAllowedSkills(c, available, this, target);
        if (available.size() == 0) {
            available.add(new Nothing(this));
        }
        available.addAll(cds);
        available.forEach(skill -> {
            if (!skillMap.containsKey(skill.type(c))) {
                skillMap.put(skill.type(c), new HashSet<>());
            }
            skillMap.get(skill.type(c)).add(skill);
        });
        ArrayList<SkillGroup> skillGroups = new ArrayList<>();
        skillMap.forEach((tactic, skills) -> skillGroups.add(new SkillGroup(tactic, skills.stream()
                .map(skill -> skill.instantiate(c, target)).collect(Collectors.toSet()))));

        gui.chooseSkills(c, target, skillGroups);
        Global.getMatch().pause();
    }


    /**Overridden abstract method for determining if this character is human - meaning the player. 
     * TODO: Reccomend renaming to isHuman(), to make more meaningful name and easier to find.*/
    @Override
    public boolean human() {
        return true;
    }

    @Override
    public String bbLiner(Combat c, Character target) {
        return null;
    }

    @Override
    public String nakedLiner(Combat c, Character target) {
        return null;
    }

    @Override
    public String stunLiner(Combat c, Character target) {
        return null;
    }

    @Override
    public String taunt(Combat c, Character target) {
        return null;
    }

    public ActionListener encounterOption(Runnable continuation) {
        return event -> continuation.run();
    }

    public void presentFightFlightChoice(Participant opponent, ActionListener fightCallback, ActionListener flightCallback) {
        assessOpponent(opponent);
        gui.message("<br/>");
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption("Fight",
                fightCallback));
        options.add(new CommandPanelOption("Flee",
                flightCallback));
        gui.presentOptions(options);
    }

    public void assessOpponent(Participant opponent) {
        String arousal;
        String stamina;
        if (get(Attribute.Perception) >= 6) {
            gui.message("She is level " + opponent.getCharacter().getLevel());
        }
        if (get(Attribute.Perception) >= 8) {
            gui.message("Her Power is " + opponent.getCharacter().get(Attribute.Power) + ", her Cunning is "
                            + opponent.getCharacter().get(Attribute.Cunning) + ", and her Seduction is "
                            + opponent.getCharacter().get(Attribute.Seduction));
        }
        opponent.state.sendAssessmentMessage(opponent, this);
        if (get(Attribute.Perception) >= 4) {
            if (opponent.getCharacter().getArousal()
                        .percent() > 70) {
                arousal = "horny";
            } else if (opponent.getCharacter().getArousal()
                               .percent() > 30) {
                arousal = "slightly aroused";
            } else {
                arousal = "composed";
            }
            if (opponent.getCharacter().getStamina()
                        .percent() < 50) {
                stamina = "tired";
            } else {
                stamina = "eager";
            }
            gui.message("She looks " + stamina + " and " + arousal + ".");
        }
    }

    @Override
    public void displayStateMessage(Optional<Trap.Instance> knownTrap) {
        if (Global.getMatch().getType() == MatchType.FTC) {
            Character holder = ((FTCMatch) Global.getMatch()).getFlagHolder();
            if (holder != null && !holder.human()) {
                gui.message("<b>" + holder.subject() + " currently holds the Flag.</b></br>");
            }
        }
        gui.message(location.get().getDescriptions().whereAmI() + "<br/><br/>");
        knownTrap.ifPresent(trap -> gui.message("You've set a " + trap.getName() + " here."));
    }

    @Override
    public void endOfMatchRound() {
        super.endOfMatchRound();
        getAddictions().forEach(Addiction::refreshWithdrawal);
    }

    @Override
    public void ding(Combat c) {
        levelsToGain += 1;
        if (levelsToGain == 1) {
            actuallyDing();
            if (cloned == 0) {
                if (c != null) {
                    c.pause();
                }
                handleLevelUp();
            }
        }
    }

    private void handleLevelUp() {
        if (availableAttributePoints > 0) {
            gui.message(this, availableAttributePoints + " Attribute Points remain.</br>");

            List<CommandPanelOption> options = att.keySet().stream()
                .filter(a -> Attribute.isTrainable(this, a) && getPure(a) > 0)
                .map(this::optionToSelectAttribute)
                .collect(Collectors.toList());
            options.add(optionToSelectAttribute(Attribute.Willpower));
            gui.presentOptions(options);

            if (Global.getMatch() != null) {
                Global.getMatch().pause();
            }
        } else if (traitPoints > 0 && !skippedFeat) {
            gui.message(this, "You've earned a new perk. Select one below.</br>");

            List<CommandPanelOption> options = Global.getFeats(this).stream()
                .filter(t -> !has(t))
                .map(this::optionToSelectTrait)
                .collect(Collectors.toList());
            options.add(optionToSelectTrait(null));
            gui.presentOptions(options);
        } else {
            skippedFeat = false;
            Global.gainSkills(this);
            levelsToGain -= 1;
            if (getLevelsToGain() > 0) {
                actuallyDing();
                handleLevelUp();
            } else {
                if (gui.combat != null) {
                    gui.combat.resume();
                } else if (Global.getMatch() != null) {
                    Global.getMatch().resume();
                } else if (Global.day != null) {
                    Global.getDay().plan();
                } else {
                    MatchType.NORMAL.runPrematch();
                }
            }
        }
    }

    private CommandPanelOption optionToSelectAttribute(Attribute a) {
        return new CommandPanelOption(a.name(), event -> increaseAttribute(a));
    }

    private CommandPanelOption optionToSelectTrait(Trait t) {
        CommandPanelOption o = new CommandPanelOption("Skip",
            "Save the trait point for later.",
            event -> {
                skipFeat();
                handleLevelUp();
            });
        if (t != null) {
            o = new CommandPanelOption(t.toString(), t.getDesc(), event -> grantTrait(t));
        }
        return o;
    }

    private void skipFeat() {
        skippedFeat = true;
    }

    private void increaseAttribute(Attribute attribute) {
        if (availableAttributePoints < 1) {
            throw new RuntimeException("attempted to increase attributes with no points remaining");
        }
        mod(attribute, 1);
        availableAttributePoints -= 1;
        handleLevelUp();
    }

    @Override
    public void matchPrep(Match m) {
        super.matchPrep(m);
        gui.startMatch();
    }

    private void grantTrait(Trait trait) {
        if (traitPoints < 1) {
            throw new RuntimeException("attempted to grant trait without trait points");
        }
        gui.message(this, "Gained feat: " + trait.toString());
        add(trait);
        Global.gainSkills(this);
        traitPoints -= 1;
        handleLevelUp();
    }

    private void actuallyDing() {
        level += 1;
        getGrowth().levelUpCoreStatsOnly(this);
        availableAttributePoints += getGrowth().attributePointsForRank(rank);
        gui.message(this, "You've gained a Level!<br/>Select which attributes to increase.");
        if (getLevel() % 3 == 0 && level < 10 || (getLevel() + 1) % 2 == 0 && level > 10) {
            traitPoints += 1;
        }
    }

    @Override
    public int getMaxWillpowerPossible() {
        return 50 + getLevel() * 5 - get(Attribute.Submissive) * 2;
    }

    @Override
    public void notifyTravel(Area dest, String message) {
        super.notifyTravel(dest, message);
        gui.message(message);
    }

    @Override
    public void intervene3p(Combat c, Character target, Character assist) {
        c.write("You take your time, approaching " + target.getName() + " and " + assist.getName() + " stealthily. "
                        + assist.getName() + " notices you first and before her reaction "
                        + "gives you away, you quickly lunge and grab " + target.getName()
                        + " from behind. She freezes in surprise for just a second, but that's all you need to "
                        + "restrain her arms and leave her completely helpless. Both your hands are occupied holding her, so you focus on kissing and licking the "
                        + "sensitive nape of her neck.<br/><br/>");
    }

    @Override
    public void victory3p(Combat c, Character target, Character assist) {
        if (target.hasDick()) {
            c.write(String.format(
                            "You position yourself between %s's legs, gently "
                                            + "forcing them open with your knees. %s dick stands erect, fully "
                                            + "exposed and ready for attention. You grip the needy member and "
                                            + "start jerking it with a practiced hand. %s moans softly, but seems"
                                            + " to be able to handle this level of stimulation. You need to turn "
                                            + "up the heat some more. Well, if you weren't prepared to suck a cock"
                                            + " or two, you may have joined the wrong competition. You take just "
                                            + "the glans into your mouth, attacking the most senstitive area with "
                                            + "your tongue. %s lets out a gasp and shudders. That's a more promising "
                                            + "reaction.<br/><br/>You continue your oral assault until you hear a breathy "
                                            + "moan, <i>\"I'm gonna cum!\"</i> You hastily remove %s dick out of "
                                            + "your mouth and pump it rapidly. %s shoots %s load into the air, barely "
                                            + "missing you.", target.getName(),
                            Global.capitalizeFirstLetter(target.possessiveAdjective()), target.getName(),
                            Global.capitalizeFirstLetter(target.pronoun()), target.possessiveAdjective(), target.getName(),
                            target.possessiveAdjective()));
        } else {
            c.write(target.nameOrPossessivePronoun()
                            + " arms are firmly pinned, so she tries to kick you ineffectually. You catch her ankles and slowly begin kissing and licking your way "
                            + "up her legs while gently, but firmly, forcing them apart. By the time you reach her inner thighs, she's given up trying to resist. Since you no "
                            + "longer need to hold her legs, you can focus on her flooded pussy. You pump two fingers in and out of her while licking and sucking her clit. In no "
                            + "time at all, she's trembling and moaning in orgasm.");
        }
    }

    @Override
    public void gain(Item item) {
        gui.message("<b>You've gained " + item.pre() + item.getName() + ".</b>");
        super.gain(item);
    }

    @Override
    public String challenge(Character other) {
        return null;
    }



    @Override
    public void afterParty() {
    }

    @Override
    public void counterattack(Character target, Tactics type, Combat c) {
        switch (type) {
            case damage:
                c.write(this, "You dodge " + target.getName()
                                + "'s slow attack and hit her sensitive tit to stagger her.");
                target.pain(c, target, 4 + Math.min(Global.random(get(Attribute.Power)), 20));
                break;
            case pleasure:
                if (!target.crotchAvailable() || !target.hasPussy()) {
                    c.write(this, "You pull " + target.getName()
                                    + " off balance and lick her sensitive ear. She trembles as you nibble on her earlobe.");
                    target.body.pleasure(this, body.getRandom("tongue"),
                        target.body.getRandomEars(),
                                    4 + Math.min(Global.random(get(Attribute.Seduction)), 20), c);
                } else {
                    c.write(this, "You pull " + target.getName() + " to you and rub your thigh against her girl parts.");
                    target.body.pleasure(this, body.getRandomFeet(), target.body.getRandomPussy(),
                                    4 + Math.min(Global.random(get(Attribute.Seduction)), 20), c);
                }
                break;
            case fucking:
                if (c.getStance()
                     .sub(this)) {
                    Position reverse = c.getStance()
                                        .reverse(c, true);
                    if (reverse != c.getStance() && !BodyPart.hasOnlyType(reverse.bottomParts(), StraponPart.TYPE)) {
                        c.setStance(reverse, this, false);
                    } else {
                        c.write(this, Global.format(
                                        "{self:NAME-POSSESSIVE} quick wits find a gap in {other:name-possessive} hold and {self:action:slip|slips} away.",
                                        this, target));
                        c.setStance(new Neutral(this, c.getOpponentCharacter(this)), this, true);
                    }
                } else {
                    target.body.pleasure(this, body.getRandomHands(), target.body.getRandomBreasts(),
                                    4 + Math.min(Global.random(get(Attribute.Seduction)), 20), c);
                    c.write(this, Global.format(
                                    "{self:SUBJECT-ACTION:pinch|pinches} {other:possessive} nipples with {self:possessive} hands as {other:subject-action:try|tries} to fuck {self:direct-object}. "
                                                    + "While {other:subject-action:yelp|yelps} with surprise, {self:subject-action:take|takes} the chance to pleasure {other:possessive} body.",
                                    this, target));
                }
                break;
            case stripping:
                Clothing clothes = target.stripRandom(c);
                if (clothes != null) {
                    c.write(this, "You manage to catch " + target.possessiveAdjective()
                                    + " hands groping your clothing, and in a swift motion you strip off her "
                                    + clothes.getName() + " instead.");
                } else {
                    c.write(this, "You manage to dodge " + target.possessiveAdjective()
                                    + " groping hands and give a retaliating slap in return.");
                    target.pain(c, target, 4 + Math.min(Global.random(get(Attribute.Power)), 20));
                }
                break;
            case positioning:
                if (c.getStance().dom(this)) {
                    c.write(this, "You outmanuever " + target.getName() + " and you exhausted her from the struggle.");
                    target.weaken(c, (int) this.modifyDamage(DamageType.stance, target, 15));
                } else {
                    c.write(this, target.getName()
                                    + " loses her balance while grappling with you. Before she can fall to the floor, you catch her from behind and hold her up.");
                    c.setStance(new Behind(this, target));
                }
                break;
            default:
                c.write(this, "You manage to dodge " + target.possessiveAdjective()
                                + " attack and give a retaliating slap in return.");
                target.pain(c, target, 4 + Math.min(Global.random(get(Attribute.Power)), 20));
        }
    }

    @Override
    public void endOfCombatRound(Combat c, Character opponent) {
        super.endOfCombatRound(c, opponent);
        if (has(Trait.RawSexuality)) {
            c.write(this, Global.format("{self:NAME-POSSESSIVE} raw sexuality turns both of you on.", this, opponent));
            temptNoSkillNoSource(c, opponent, arousal.max() / 25);
            opponent.temptNoSkillNoSource(c, this, opponent.arousal.max() / 25);
        }
        if (has(Trait.slime)) {
            if (hasPussy() && !body.getRandomPussy().moddedPartCountsAs(GooeyMod.TYPE)) {
                body.getRandomPussy().addTemporaryMod(new GooeyMod(), 999);
                c.write(this, 
                                Global.format("{self:NAME-POSSESSIVE} %s turned back into a gooey pussy.",
                                                this, opponent, body.getRandomPussy()));
            }
            if (hasDick() && !body.getRandomCock().moddedPartCountsAs(SlimyCockMod.TYPE)) {
                body.getRandomCock().addTemporaryMod(new SlimyCockMod(), 999);
                c.write(this, 
                                Global.format("{self:NAME-POSSESSIVE} %s turned back into a gooey pussy.",
                                                this, opponent, body.getRandomPussy()));
            }
        }
    }

    @Override
    public String nameDirectObject() {
        return "you";
    }
    
    @Override
    public boolean add(Trait t) {
        if (t==Trait.nymphomania) {mod(Attribute.Nymphomania, 1);}
        return super.add(t);
    }

    @Override
    public String subjectAction(String verb, String pluralverb) {
        return subject() + " " + verb;
    }

    @Override
    public String nameOrPossessivePronoun() {
        return "your";
    }

    @Override
    public String subject() {
        return "you";
    }

    @Override
    protected String subjectWas() {
        return subject() + " were";
    }

    @Override
    public void emote(Emotion emo, int amt) {

    }

    @Override
    public String getPortrait() {
        return null;
    }

    @Override
    public String action(String firstPerson, String thirdPerson) {
        return firstPerson;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean resist3p(Combat c, Character target, Character assist) {
        return has(Trait.cursed);
    }

    @Override
    protected void resolveOrgasm(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart, int times,
                    int totalTimes) {
        super.resolveOrgasm(c, opponent, selfPart, opponentPart, times, totalTimes);
        if (has(Trait.slimification) && times == totalTimes && getWillpower().percent() < 60
            && !has(Trait.slime)) {
            c.write(this, Global.format(
                "A powerful shiver runs through your entire body. Oh boy, you know where this"
                    + " is headed... Sure enough, you look down to see your skin seemingly <i>melt</i>,"
                    + " turning a translucent blue. You legs fuse together and collapse into a puddle."
                    + " It only takes a few seconds for you to regain some semblance of control over"
                    + " your amorphous body, but you're not going to switch back to your human"
                    + " form before this fight is over...", this, opponent));
            nudify();
            purge(c);
            addTemporaryTrait(Trait.slime, 999);
            add(c, new PlayerSlimeDummy(this));
            if (hasPussy() && !body.getRandomPussy().moddedPartCountsAs(GooeyMod.TYPE)) {
                body.getRandomPussy().addTemporaryMod(new GooeyMod(), 999);
                body.getRandomPussy().addTemporaryMod(new ExternalTentaclesMod(), 999);
            }
            if (hasDick() && !body.getRandomCock().moddedPartCountsAs(SlimyCockMod.TYPE)) {
                body.getRandomCock().addTemporaryMod(new SlimyCockMod(), 999);
            }
            BreastsPart part = body.getRandomBreasts();
            if (part != null
                && body.getRandomBreasts().getSize() != Size.min()) {
                part.temporarilyChangeSize(1, 999);
            }
            ((GenericBodyPart) body.getSkin()).addTemporaryMod(new GooeySkinMod(), 999);
            body.temporaryAddPart(
                new TentaclePart("slime pseudopod", "back", "slime", 0.0, 1.0, 1.0), 999);
            addTemporaryTrait(Trait.Sneaky, 999);
            addTemporaryTrait(Trait.shameless, 999);
            addTemporaryTrait(Trait.lactating, 999);
            addTemporaryTrait(Trait.addictivefluids, 999);
            addTemporaryTrait(Trait.autonomousPussy, 999);
            addTemporaryTrait(Trait.enthrallingjuices, 999);
            addTemporaryTrait(Trait.energydrain, 999);
            addTemporaryTrait(Trait.desensitized, 999);
            addTemporaryTrait(Trait.steady, 999);
            addTemporaryTrait(Trait.strongwilled, 999);
        }
    }

    private int getLevelsToGain() {
        return levelsToGain;
    }

    @Override
    public int exercise(Exercise source) {
        int gain = super.exercise(source);
        gui.clearText();
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        CommandPanelOption o = new CommandPanelOption("Next",
            event -> {
                source.done(true);
                gui.clearText();
            });
        options.add(o);
        gui.presentOptions(options);
        source.showScene(source.pickScene(gain));
        if (gain > 0) {
            Global.gui().message("<b>Your maximum stamina has increased by " + gain + ".</b>");
        }
        return gain;
    }

    @Override
    public int porn(Porn source) {
        int gain = super.porn(source);
        gui.clearText();
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        CommandPanelOption o = new CommandPanelOption("Next",
            event -> {
                source.done(true);
                gui.clearText();
            });
        options.add(o);
        gui.presentOptions(options);
        source.showScene(source.pickScene(gain));
        if (gain > 0) {
            Global.gui().message("<b>Your maximum arousal has increased by " + gain + ".</b>");
        }
        return gain;
    }

    @Override
    public void chooseLocateTarget(Map<Character, Runnable> potentialTargets, Runnable noneOption, String msg) {
        gui.clearText();
        gui.validate();
        gui.message(msg);
        List<CommandPanelOption> options = potentialTargets.entrySet().stream()
            .map(entry -> new CommandPanelOption(entry.getKey().getTrueName(),
                event -> entry.getValue().run()))
            .collect(Collectors.toList());
        options.add(new CommandPanelOption("Leave", event -> noneOption.run()));
        gui.presentOptions(options);
    }

    @Override
    public void leaveAction(Runnable callback) {
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption("Leave", event -> callback.run()));
        gui.presentOptions(options);
    }

    @Override
    public void chooseShopOption(Store shop, Collection<Loot> items,
        List<String> additionalChoices) {
        List<CommandPanelOption> options = items.stream()
            .map(item -> new CommandPanelOption(
                Global.capitalizeFirstLetter(item.getName()),
                item.getDesc(),
                event -> shop.visit(item.getName())))
            .collect(Collectors.toList());
        options.addAll(additionalChoices.stream()
            .map(choice -> newActivitySubchoice(shop, choice))
            .collect(Collectors.toList()));
        gui.presentOptions(options);
    }

    @Override
    public void sceneNext(Scene source) {
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption(
            "Next",
            event -> source.respond("Next")
        ));
        gui.presentOptions(options);
    }

    // displayTexts and prices are expected to be 1:1
    @Override
    public void chooseBodyShopOption(BodyShop shop, List<String> displayTexts,
        List<Integer> prices, List<String> additionalChoices) {
        assert displayTexts.size() == prices.size();
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        for (int i = 0; i < displayTexts.size(); i++) {
            final String displayText = displayTexts.get(i);
            options.add(new CommandPanelOption(
                displayText,
                "Price: $" + prices.get(i),
                event -> shop.visit(displayText)));
        }
        options.addAll(additionalChoices.stream()
            .map(choice -> newActivitySubchoice(shop, choice))
            .collect(Collectors.toList()));
        gui.presentOptions(options);
    }

    @Override
    public void nextCombat(Combat c) {
        Global.getMatch().pause();
        ArrayList<CommandPanelOption> options = new ArrayList<>();
        options.add(new CommandPanelOption("Next", event -> {
            c.resume();
        }));
        gui.presentOptions(options);
    }

    public void chooseActivity(List<Activity> activities) {
        gui.presentOptions(activities.stream()
            .map(activity -> new CommandPanelOption(activity.toString(),
                event -> activity.visit("Start")))
            .collect(Collectors.toList()));
    }

    public void chooseCombatScene(Combat c, Character npc, List<CombatSceneChoice> choices) {
        gui.presentOptions(choices.stream()
            .map(choice -> new CommandPanelOption(choice.getChoice(), event -> {
                c.write("<br/>");
                choice.choose(c, npc);
                c.updateMessage();
                nextCombat(c);
            }))
            .collect(Collectors.toList()));
    }

    @Override
    public void chooseActivitySubchoices(Activity activity, List<String> choices) {
        gui.presentOptions(choices.stream().map(
            choice -> newActivitySubchoice(activity, choice))
            .collect(Collectors.toList()));
    }

    private static CommandPanelOption newActivitySubchoice(Activity activity, String choice) {
        return new CommandPanelOption(choice, event -> activity.visit(choice));
    }

    @Override
    public Person getGrammar() {
        return new SingularSecondPerson(this);
    }

    @Override
    public void notifyCombatStart(Combat c, Character opponent) {
        super.notifyCombatStart(c, opponent);
        assert opponent instanceof NPC: opponent.toString();
        c.setBeingObserved(true);
        gui.beginCombat(c, (NPC) opponent);
    }

    @Override
    public void message(String message) {
        gui.message(message);
    }

    @Override
    public Intelligence makeIntelligence() {
        return new HumanIntelligence(this);
    }
}
