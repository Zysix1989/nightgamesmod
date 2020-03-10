package nightgames.combat;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.*;
import nightgames.characters.body.mods.catcher.*;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.match.MatchType;
import nightgames.match.Participant;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.modifier.standard.NoRecoveryModifier;
import nightgames.nskills.tags.SkillTag;
import nightgames.pet.Pet;
import nightgames.pet.PetCharacter;
import nightgames.skills.*;
import nightgames.stance.*;
import nightgames.status.*;
import nightgames.status.Stunned;
import nightgames.status.Compulsive.Situation;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.Addiction.Severity;
import nightgames.status.addiction.AddictionType;
import nightgames.utilities.ProseUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Combat {
    private static final int NPC_TURN_LIMIT = 75;
    private static final double NPC_DRAW_ERROR_MARGIN = .15;

    /**Combat phases.*/
    private enum CombatPhase {
        START,
        PRETURN,
        SKILL_SELECTION,
        PET_ACTIONS,
        DETERMINE_SKILL_ORDER,
        P1_ACT_FIRST,
        P2_ACT_FIRST,
        P1_ACT_SECOND,
        P2_ACT_SECOND,
        UPKEEP,
        RESULTS_SCENE,
        FINISHED_SCENE,
        ENDED,
    }

    private interface Phase {
        CombatPhase getEnum();
        boolean turn(Combat c);
        boolean next(Combat c);
    }

    public static class State implements Participant.PState {
        @Override
        public nightgames.characters.State getEnum() {
            return nightgames.characters.State.combat;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.getLocation().fight.battle();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(DefaultEncounter encounter, Participant p, Participant other) {
            throw new UnsupportedOperationException(String.format("%s is already in combat!",
                    p.getCharacter().getTrueName()));
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("%s is already in combat!",
                    p.getCharacter().getTrueName()));
        }
    }

    private static class StartPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.START;
        }

        @Override
        public boolean turn(Combat c) {
            c.phase = new PreTurnPhase();
            return false;
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class PreTurnPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.PRETURN;
        }

        @Override
        public boolean turn(Combat c) {
            c.wroteMessage = false;
            c.message = "";
            c.timer += 1;
            Character player;
            Character other;
            if (c.p1.getCharacter().human()) {
                player = c.p1.getCharacter();
                other = c.p2.getCharacter();
            } else {
                player = c.p2.getCharacter();
                other = c.p1.getCharacter();
            }
            c.message = c.describe(player, other);
            if (!c.shouldAutoresolve() && !Global.checkFlag(Flag.noimage)) {
                Global.gui()
                      .clearImage();
                if (!c.imagePath.isEmpty()) {
                    Global.gui()
                          .displayImage(c.imagePath, c.images.get(c.imagePath));
                }
            }
            c.p1.getCharacter().preturnUpkeep();
            c.p2.getCharacter().preturnUpkeep();
            c.p1act = null;
            c.p2act = null;
            if (Global.random(3) == 0 && !c.shouldAutoresolve()) {
                c.checkForCombatComment();
            }
            c.phase = new SkillSelectionPhase();
            return false;
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class SkillSelectionPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.SKILL_SELECTION;
        }

        @Override
        public boolean turn(Combat c) {
            if (c.p1act == null) {
                return c.p1.getCharacter().act(c);
            } else if (c.p2act == null) {
                return c.p2.getCharacter().act(c);
            } else {
                c.phase = new PetActionsPhase();
                return false;
            }
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class PetActionsPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.PET_ACTIONS;
        }

        @Override
        public boolean turn(Combat c) {
            Set<PetCharacter> alreadyBattled = new HashSet<>();
            if (c.otherCombatants.size() > 0) {
                if (!Global.checkFlag("NoPetBattles")) {
                    ArrayList<PetCharacter> pets = new ArrayList<>(c.otherCombatants);
                    for (PetCharacter pet : pets) {
                        if (!c.otherCombatants.contains(pet) || alreadyBattled.contains(pet)) { continue; }
                        for (PetCharacter otherPet : pets) {
                            if (!c.otherCombatants.contains(pet) || alreadyBattled.contains(otherPet)) { continue; }
                            if (!pet.getSelf().owner().equals(otherPet.getSelf().owner()) && Global.random(2) == 0) {
                                c.petbattle(pet.getSelf(), otherPet.getSelf());
                                alreadyBattled.add(pet);
                                alreadyBattled.add(otherPet);
                            }
                        }
                    }
                }
                List<PetCharacter> actingPets = new ArrayList<>(c.otherCombatants);
                actingPets.stream().filter(pet -> !alreadyBattled.contains(pet)).forEach(pet -> {
                    pet.act(c, c.pickTarget(pet));
                    c.write("<br/>");
                    if (pet.getSelf().owner().has(Trait.devoteeFervor) && Global.random(2) == 0) {
                        c.write(pet, Global.format("{self:SUBJECT} seems to have gained a second wind from {self:possessive} religious fervor!", pet, pet.getSelf().owner()));
                        pet.act(c, c.pickTarget(pet));
                    }
                });
                c.write("<br/>");
            }
            c.phase = new DetermineSkillOrderPhase();
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class DetermineSkillOrderPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.DETERMINE_SKILL_ORDER;
        }

        @Override
        public boolean turn(Combat c) {
            Phase result;
            if (c.p1.getCharacter().init() + c.p1act.speed() >= c.p2.getCharacter().init() + c.p2act.speed()) {
                result = new P1ActFirstPhase();
            } else {
                result = new P2ActFirstPhase();
            }
            c.phase = result;
            return false;
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class P1ActFirstPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.P1_ACT_FIRST;
        }

        @Override
        public boolean turn(Combat c) {
            if (c.doAction(c.p1.getCharacter(), c.p1act.getDefaultTarget(c), c.p1act)) {
                c.phase = new UpkeepPhase();
            } else {
                c.phase = new P2ActSecondPhase();
            }
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class P2ActFirstPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.P2_ACT_FIRST;
        }

        @Override
        public boolean turn(Combat c) {
            if (c.doAction(c.p2.getCharacter(), c.p2act.getDefaultTarget(c), c.p2act)) {
                c.phase = new UpkeepPhase();
            } else {
                c.phase = new P1ActSecondPhase();
            }
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class P1ActSecondPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.P1_ACT_SECOND;
        }

        @Override
        public boolean turn(Combat c) {
            c.doAction(c.p1.getCharacter(), c.p1act.getDefaultTarget(c), c.p1act);
            c.phase = new UpkeepPhase();
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class P2ActSecondPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.P2_ACT_SECOND;
        }

        @Override
        public boolean turn(Combat c) {
            c.doAction(c.p2.getCharacter(), c.p2act.getDefaultTarget(c), c.p2act);
            c.phase = new UpkeepPhase();
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class UpkeepPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.UPKEEP;
        }

        @Override
        public boolean turn(Combat c) {
            c.p1.getCharacter().endOfCombatRound(c, c.p2.getCharacter());
            c.p2.getCharacter().endOfCombatRound(c, c.p1.getCharacter());
            // iterate through all the pets here so we don't get concurrent modification issues
            List<PetCharacter> pets = new ArrayList<>(c.otherCombatants);
            pets.forEach(other -> {
                if (c.otherCombatants.contains(other)) {
                    other.endOfCombatRound(c, c.getOpponentCharacter(other));
                }
            });
            c.checkStamina(c.p1.getCharacter());
            c.checkStamina(c.p2.getCharacter());
            pets.forEach(other -> {
                if (c.otherCombatants.contains(other)) {
                    c.checkStamina(other);
                }
            });
            c.doStanceTick(c.p1.getCharacter());
            c.doStanceTick(c.p2.getCharacter());

            List<Character> team1 = new ArrayList<>(c.getPetsFor(c.p1.getCharacter()));
            team1.add(c.p1.getCharacter());
            List<Character> team2 = new ArrayList<>(c.getPetsFor(c.p2.getCharacter()));
            team2.add(c.p2.getCharacter());
            team1.forEach(self -> c.doAuraTick(self, team1, team2));
            team2.forEach(self -> c.doAuraTick(self, team2, team1));

            c.combatantData.values().forEach(data -> data.tick(c));

            c.getStance().decay(c);
            c.getStance().checkOngoing(c);
            c.p1.getCharacter().regen(c);
            c.p2.getCharacter().regen(c);
            c.phase = new PreTurnPhase();
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class ResultsScenePhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.RESULTS_SCENE;
        }

        @Override
        public boolean turn(Combat c) {
            if (!c.cloned) {
                if (c.p1.getCharacter().checkLoss(c) && c.p2.getCharacter().checkLoss(c)) {
                    c.draw();
                } else if (c.p1.getCharacter().checkLoss(c)) {
                    c.victory(c.p2);
                } else if (c.p2.getCharacter().checkLoss(c)) {
                    c.victory(c.p1);
                }
            }
            c.phase = new FinishedScenePhase();
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class FinishedScenePhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.FINISHED_SCENE;
        }

        @Override
        public boolean turn(Combat c) {
            c.phase = new EndedPhase();
            return c.phase.next(c);
        }

        @Override
        public boolean next(Combat c) {
            return c.next();
        }
    }

    private static class EndedPhase implements Phase {
        @Override
        public CombatPhase getEnum() {
            return CombatPhase.ENDED;
        }

        @Override
        public boolean turn(Combat c) {
            return next(c);
        }

        @Override
        public boolean next(Combat c) {
            c.end();
            return true;
        }
    }

    
    //TODO: Convert as much of this data as possible to CombatData - DSm
    private Combatant p1;
    private Combatant p2;
    public List<PetCharacter> otherCombatants;
    public Map<String, CombatantData> combatantData;
    public Optional<Combatant> winner;
    public Phase phase;
    protected Skill p1act;
    protected Skill p2act;
    public Area location;
    private String message;
    private Position stance;
    protected int timer;
    public Result state;
    private HashMap<String, String> images;
    boolean lastFailed = false;
    private CombatLog log;
    private boolean beingObserved;
    private int postCombatScenesSeen;
    private boolean wroteMessage;
    private boolean cloned;

    String imagePath = "";

    public Combat(Participant p1, Participant p2, Area loc) {
        this.p1 = new Combatant(p1);
        combatantData = new HashMap<>();
        this.p2 = new Combatant(p2);
        location = loc;
        stance = new Neutral(p1.getCharacter(), p2.getCharacter());
        message = "";
        paused = false;
        processedEnding = false;
        timer = 0;
        images = new HashMap<String, String>();
        this.p1.getParticipant().state = new Participant.CombatState();
        this.p2.getParticipant().state = new Participant.CombatState();
        postCombatScenesSeen = 0;
        otherCombatants = new ArrayList<>();
        wroteMessage = false;
        winner = Optional.empty();
        phase = new StartPhase();
        cloned = false;
        if (doExtendedLog()) {
            log = new CombatLog(this);
        }
    }

    private void applyCombatStatuses(Character self, Character other) {
        if (other.human()) {
            write(self.challenge(other));
        }
        self.getAddictions().forEach(a -> {
            if (a.isActive()) {
                Optional<Status> status = a.startCombat(this, other);
                if (status.isPresent()) {
                    self.add(this, status.get());
                }
            }
        });
        if (self.has(Trait.zealinspiring) && other.getAddiction(AddictionType.ZEAL).map(Addiction::isInWithdrawal).orElse(false)) {
            self.add(this, new DivineCharge(self, .3));
        }
        if (self.has(Trait.suave) && !other.hasDick()) {
            self.add(this, new SapphicSeduction(self));
        }

        if (self.has(Trait.footfetishist)) {
            applyFetish(self, other, FeetPart.TYPE);
        } 
        if(self.has(Trait.breastobsessed) && other.hasBreasts()) {
            applyFetish(self, other, BreastsPart.TYPE);
        }
        if(self.has(Trait.assaddict)) {
            applyFetish(self, other, AssPart.TYPE);
        }
        if(self.has(Trait.pussywhipped ) && other.hasPussy()) {
            applyFetish(self, other, PussyPart.TYPE);
        }
        if (self.has(Trait.cockcraver)&& other.hasDick()) {
            applyFetish(self, other, CockPart.TYPE);
        }
    }

    public void applyFetish(Character self, Character other, String FetishType) {
        if ( other.body.getRandom(FetishType) != null && self.body.getFetish(FetishType).isEmpty()) {
            if (self.human()) {
                var part = (GenericBodyPart) other.body.getRandom(FetishType);
                write(self,
                    "As your first battle of the night begins, you can't help but think about "
                    + other.nameOrPossessivePronoun() + " " + FetishType
                    + " and how " + ProseUtils.neuterSubjectPronoun(part.isMultipleObjects())
                    + " would feel on your skin.");
            } 
            self.add(this, new BodyFetish(self, null, FetishType, .25));
        }
    }

    public void go() {
        if (p1.getCharacter().mostlyNude() && !p2.getCharacter().mostlyNude()) {
            p1.getCharacter().emote(Emotion.nervous, 20);
        }
        if (p2.getCharacter().mostlyNude() && !p1.getCharacter().mostlyNude()) {
            p2.getCharacter().emote(Emotion.nervous, 20);
        }
        applyCombatStatuses(p1.getCharacter(), p2.getCharacter());
        applyCombatStatuses(p2.getCharacter(), p1.getCharacter());

        updateMessage();
        if (doExtendedLog()) {
            log.logHeader();
        }
        if (shouldAutoresolve()) {
            autoresolve();
        } else {
            phase.next(this);
        }
    }

    private void resumeNoClearFlag() {
        paused = false;
        while(!paused && !turn()) {}
        if (phase.getEnum() != CombatPhase.ENDED) {
            updateAndClearMessage();
        }
    }

    public void resume() {
        wroteMessage = false;
        resumeNoClearFlag();
    }

    public CombatantData getCombatantData(Character character) {
        if (!combatantData.containsKey(character.getTrueName())) {
            combatantData.put(character.getTrueName(), new CombatantData());
        }
        return combatantData.get(character.getTrueName());
    }

    private boolean checkBottleCollection(Character victor, Character loser, String modType) {
        return victor.has(Item.EmptyBottle, 1)
            && loser.body.getRandomPussy().moddedPartCountsAs(modType);
    }

    private void draw() {
        state = eval();
        p1.getCharacter().evalChallenges(this, null);
        p2.getCharacter().evalChallenges(this, null);
        if (p1.getCharacter().has(Trait.slime)) {
            p1.getCharacter().purge(this);
        }
        if (p2.getCharacter().has(Trait.slime)) {
            p2.getCharacter().purge(this);
        }
        Character first = p1.getCharacter();
        Character second = p2.getCharacter();

        first.gainXP(first.getVictoryXP(second));
        first.orgasm();
        first.undress(this);
        first.gainTrophy(this, second);
        p1.getParticipant().invalidateAttacker(p2.getParticipant());
        first.gainAttraction(second, 4);

        second.gainXP(second.getVictoryXP(first));
        second.orgasm();
        second.undress(this);
        second.gainTrophy(this, first);
        p2.getParticipant().invalidateAttacker(p1.getParticipant());
        second.gainAttraction(first, 4);

        if (p1.getCharacter().human()) {
            p2.getCharacter().sendDrawMessage(this, state);
        } else if (p2.getCharacter().human()) {
            p1.getCharacter().sendDrawMessage(this, state);
        }
        winner = Optional.of(new Combatant(new Participant(Global.noneCharacter())));
    }

    private void victory(Combatant won) {
        state = eval();
        p1.getCharacter().evalChallenges(this, won.getCharacter());
        p2.getCharacter().evalChallenges(this, won.getCharacter());
        var winner = won.getCharacter();
        var loser = getOpponentCharacter(won.getCharacter());

        if (won.getCharacter().has(Trait.slime)) {
            won.getCharacter().purge(this);
        }
        winner.gainXP(winner.getDefeatXP(loser));
        if (!winner.human() || !Global.getMatch().getCondition().name().equals(NoRecoveryModifier.NAME)) {
            winner.orgasm();
        }
        winner.dress(this);
        winner.gainAttraction(loser, 1);
        won.getParticipant().incrementScore(
                won.getParticipant().pointsForVictory(getOpponent(won.getCharacter()).getParticipant()),
                "for a win");

        loser.gainXP(loser.getVictoryXP(winner));
        loser.orgasm();
        loser.undress(this);
        getOpponent(winner).getParticipant().invalidateAttacker(won.getParticipant());
        loser.gainAttraction(winner, 2);

        if (won.getCharacter().human()) {
            loser.sendDefeatMessage(this, state);
        } else if (loser.human()) {
            won.getCharacter().sendVictoryMessage(this, state);
        }
        Character victor = won.getCharacter();

        //Collect Bottle-able substances.
        this.doBottleCollection(victor, loser);

        //If they lost, Do a willpower gain.
        if (loser.human() && loser.getWillpower().max() < loser.getMaxWillpowerPossible()) {
            write("<br/>Ashamed at your loss, you resolve to win next time.");
            write("<br/><b>Gained 1 Willpower</b>.");
            loser.getWillpower().gain(1);
        }
        victor.getWillpower().renew();
        loser.getWillpower().renew();

        if (Global.getMatch().getType() == MatchType.FTC && loser.has(Item.Flag)) {
            write(victor, Global.format(
                            "<br/><b>{self:SUBJECT-ACTION:take|takes} the " + "Flag from {other:subject}!</b>", victor,
                    loser));
            loser.remove(Item.Flag);
            victor.gain(Item.Flag);
        }
        this.winner = Optional.of(won);
    }

    private boolean checkLosses() {
        if (cloned) {
            return false;
        }
        if (p1.getCharacter().checkLoss(this) && p2.getCharacter().checkLoss(this)) {
            return true;
        }
        if (p1.getCharacter().checkLoss(this)) {
            winner = Optional.of(p2);
            return true;
        }
        if (p2.getCharacter().checkLoss(this)) {
            winner = Optional.of(p1);
            return true;
        }
        return false;
    }

    private void checkForCombatComment() {
        Character other;
        if (p1.getCharacter().human()) {
            other = p2.getCharacter();
        } else if (p2.getCharacter().human()) {
            other = p1.getCharacter();
        } else {
            other = (NPC) (Global.random(2) == 0 ? p1.getCharacter() : p2.getCharacter());
        }
        if (other instanceof NPC) {
            NPC commenter = (NPC) other;
            Optional<String> comment = commenter.getComment(this);
            if (comment.isPresent()) {
                write(commenter, "<i>\"" + Global.format(comment.get(), commenter, Global.getPlayer()) + "\"</i>");
            }
        }
    }

    private void doAuraTick(Character character, List<Character> allies, List<Character> opponents) {
        if (character.has(Trait.overwhelmingPresence)) {
            write(character, Global.format("{self:NAME-POSSESSIVE} overwhelming presence mentally exhausts {self:possessive} opponents.", character, character));
            opponents.forEach(opponent -> opponent.weaken(this, opponent.getStamina().max() / 10));
        }
        String beguilingbreastCompletedFlag = Trait.beguilingbreasts.name() + "Completed";
        //Fix for Beguiling Breasts being seen when it shouldn't.
        if (character.has(Trait.beguilingbreasts)
            && !getCombatantData(character).getBooleanFlag(beguilingbreastCompletedFlag)
            && character.outfit.slotOpen(ClothingSlot.top)
            && getStance().facing(character, getOpponentCharacter(character))
            && !getOpponentCharacter(character).is(Stsflag.blinded)) {
            Character mainOpponent = getOpponentCharacter(character);
            write(character, Global.format("The instant {self:subject-action:lay|lays} {self:possessive} eyes on {other:name-possessive} bare breasts, {self:possessive} consciousness flies out of {self:possessive} mind. " +
                            (character.canAct() ? "{other:SUBJECT-ACTION:giggle|giggles} a bit and {other:action:cup} {other:possessive} {other:body-part:breasts}"
                                                + "  and {other:action:give} them a little squeeze to which {self:subject} can only moan." : ""), 
                            mainOpponent, character));
            opponents.forEach(opponent -> opponent.add(this, new Trance(opponent, 50)));
            getCombatantData(character).setBooleanFlag(beguilingbreastCompletedFlag, true);
        }

        if (character.has(Trait.footfetishist)) {
            fetishDisadvantageAura(character, allies, opponents, FeetPart.TYPE, ClothingSlot.feet);
        }
        if (character.has(Trait.breastobsessed)) {
            fetishDisadvantageAura(character, allies, opponents, BreastsPart.TYPE, ClothingSlot.top);
        }
        if(character.has(Trait.assaddict)) {
            fetishDisadvantageAura(character, allies, opponents, AssPart.TYPE, ClothingSlot.bottom);
        }
        if(character.has(Trait.pussywhipped ) )  {
            fetishDisadvantageAura(character, allies, opponents, PussyPart.TYPE, ClothingSlot.bottom);
        }
        if(character.has(Trait.cockcraver)) {
            fetishDisadvantageAura(character, allies, opponents, CockPart.TYPE, ClothingSlot.bottom);
        }
        
        opponents.forEach(opponent -> checkIndividualAuraEffects(character, opponent));
    }
    
    
    private void fetishDisadvantageAura(Character character, List<Character> allies, List<Character> opponents, String fetishType, ClothingSlot clothingType) {
       
        float ifPartNotNull = 0;
       
        
        if(fetishType == BreastsPart.TYPE && opponents.get(0).hasBreasts()){
            ifPartNotNull = 1;
        } else if(fetishType == PussyPart.TYPE && opponents.get(0).hasPussy()){
            ifPartNotNull = 1;
        } else if(fetishType == CockPart.TYPE && opponents.get(0).hasDick()){
            ifPartNotNull = 1;
        } else if(fetishType == AssPart.TYPE ){
            ifPartNotNull = 1;
        } else if(fetishType == FeetPart.TYPE){
            ifPartNotNull = 1;
        } else{
            ifPartNotNull = 0;
        }      
        
        if(ifPartNotNull == 1)
        {
            Optional<Character> otherWithAura = opponents.stream().filter(other -> other.body.getRandom(fetishType) != null).findFirst();
            Clothing clothes = otherWithAura.get().getOutfit().getTopOfSlot(clothingType);
            boolean seeFetish = clothes == null || clothes.getLayer() <= 1 || otherWithAura.get().getOutfit().getExposure() >= .5;
            String partDescrip;
            
        if(fetishType == BreastsPart.TYPE){
             partDescrip = otherWithAura.get().body.getRandomBreasts().describe(otherWithAura.get()) ;
         } else if(fetishType == AssPart.TYPE){
             partDescrip = otherWithAura.get().body.getRandomAss().describe(otherWithAura.get()) ;
         } else if(fetishType == PussyPart.TYPE){
             partDescrip = otherWithAura.get().body.getRandomPussy().describe(otherWithAura.get()) ;
         } else if(fetishType == CockPart.TYPE){
             partDescrip = otherWithAura.get().body.getRandomCock().describe(otherWithAura.get()) ;
         } else{
             partDescrip = fetishType;
         }
        
            if ( otherWithAura.isPresent() && seeFetish && Global.random(5) == 0) {
                if (character.human()) {
                    write(character, "You can't help thinking about " + otherWithAura.get().nameOrPossessivePronoun() + " " + partDescrip + ".");
                }
                character.add(this, new BodyFetish(character, null, fetishType, .05));
            }
        }
        
       
    
    }
    
    private void checkIndividualAuraEffects(Character self, Character other) {
        if (self.has(Trait.magicEyeEnthrall) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && !other.is(Stsflag.blinded) && Global.random(20) == 0) {
            write(self,
                            Global.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and captures both {self:name-possessive} gaze and consciousness.",
                                            other, self));
            other.add(this, new Enthralled(other, self, 2));
        }
        if (self.has(Trait.magicEyeTrance) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && !other.is(Stsflag.blinded) && Global.random(10) == 0) {
            write(self,
                            Global.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and send {self:subject} straight into a trance.",
                                            other, self));
            other.add(this, new Trance(other));
        }

        if (self.has(Trait.magicEyeFrenzy) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && !other.is(Stsflag.blinded) && Global.random(10) == 0) {
            write(self,
                            Global.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and send {self:subject} into a frenzy.",
                                            other, self));
            other.add(this, new Frenzied(other, 3));
        }

        if (self.has(Trait.magicEyeArousal) && other.getArousal().percent() >= 50 && getStance().facing(other, self)
                        && !other.is(Stsflag.blinded) && Global.random(5) == 0) {
            write(self,
                            Global.format("<br/>{other:NAME-POSSESSIVE} eyes start glowing and {self:subject-action:feel|feels} a strong pleasure wherever {other:possessive} gaze lands. {self:SUBJECT-ACTION:are|is} literally being raped by {other:name-possessive} eyes!",
                                            other, self));
            other.temptNoSkillNoSource(this, self, self.get(Attribute.Seduction) / 2);
        }

        if (getStance().facing(self, other) && other.breastsAvailable() && !self.has(Trait.temptingtits) 
                        && other.has(Trait.temptingtits) && !other.is(Stsflag.blinded)) {
            write(self, Global.format("{self:SUBJECT-ACTION:can't avert|can't avert} {self:possessive} eyes from {other:name-possessive} perfectly shaped tits sitting in front of {self:possessive} eyes.",
                                            self, other));
            self.temptNoSkill(this, other, other.body.getRandomBreasts(), 10 + Math.max(0, other.get(Attribute.Seduction) / 3 - 7));
        } else if (getOpponentCharacter(self).has(Trait.temptingtits) && getStance().behind(other)) {
            write(self, Global.format("{self:SUBJECT-ACTION:feel|feels} a heat in {self:possessive} groin as {other:name-possessive} enticing tits press against {self:possessive} back.",
                            self, other));
            double selfTopExposure = self.outfit.getExposure(ClothingSlot.top);
            double otherTopExposure = other.outfit.getExposure(ClothingSlot.top);
            double temptDamage = 20 + Math.max(0, other.get(Attribute.Seduction) / 2 - 12);
            temptDamage = temptDamage * Math.min(1, selfTopExposure + .5) * Math.min(1, otherTopExposure + .5);
            self.temptNoSkill(this, other, other.body.getRandomBreasts(), (int) temptDamage);
        }

        if (self.has(Trait.enchantingVoice)) {
            int voiceCount = getCombatantData(self).getIntegerFlag("enchantingvoice-count");
            if (voiceCount >= 1) {
                if (!self.human()) {
                    write(self,
                                    Global.format("{other:SUBJECT} winks at you and verbalizes a few choice words that pass straight through your mental barriers.",
                                                    other, self));
                } else {
                    write(self,
                                    Global.format("Sensing a moment of distraction, you use the power in your voice to force {self:subject} to your will.",
                                                    other, self));
                }
                (new Command(self)).resolve(this, other);
                int cooldown = Math.max(1, 6 - (self.getLevel() - other.getLevel() / 5));
                getCombatantData(self).setIntegerFlag("enchantingvoice-count", -cooldown);
            } else {
                getCombatantData(self).setIntegerFlag("enchantingvoice-count", voiceCount + 1);
            }
        }

        self.getArmManager().ifPresent(m -> m.act(this, self, other));

        if (self.has(Trait.mindcontroller)) {
            Collection<Clothing> infra = self.outfit.getArticlesWithTrait(ClothingTrait.infrasound);
            float magnitude = infra.size() * (Addiction.LOW_INCREASE / 6);
            if (magnitude > 0) {
                other.addict(this, AddictionType.MIND_CONTROL, self, magnitude);
                if (Global.random(3) == 0) {
                    Addiction add = other.getAddiction(AddictionType.MIND_CONTROL).orElse(null);
                    Clothing source = (Clothing) infra.toArray()[0];
                    boolean knows = (add != null && add.atLeast(Severity.MED)) || other.get(Attribute.Cunning) >= 30
                                    || other.get(Attribute.Science) >= 10;
                    String msg;
                    if (other.human()) {
                        msg = "<i>You hear a soft buzzing, just at the edge of your hearing. ";
                        if (knows) {
                            msg += Global.format("Although you can't understand it, the way it draws your"
                                            + " attention to {self:name-possessive} %s must mean it's"
                                            + " influencing you somehow!", self, other, source.getName());
                        } else {
                            msg += "It's probably nothing, though.</i>";
                        }
                    } else {
                        msg = Global.format("You see that {other:subject-action:is} distracted from {self:possessive} %s", self, other, source.getName());
                    }
                    write(other, msg);
                }
            }
        }
    }

    private static final List<CombatPhase> SKIPPABLE_PHASES = 
                    Arrays.asList(
                    CombatPhase.PET_ACTIONS,
                    CombatPhase.P1_ACT_FIRST,
                    CombatPhase.P1_ACT_SECOND,
                    CombatPhase.P2_ACT_FIRST,
                    CombatPhase.P2_ACT_SECOND);

    private static final List<CombatPhase> FAST_COMBAT_SKIPPABLE_PHASES = 
                    Arrays.asList(
                    CombatPhase.PET_ACTIONS,
                    CombatPhase.P1_ACT_FIRST,
                    CombatPhase.P1_ACT_SECOND,
                    CombatPhase.P2_ACT_FIRST,
                    CombatPhase.P2_ACT_SECOND,
                    CombatPhase.UPKEEP,
                    CombatPhase.DETERMINE_SKILL_ORDER);

    private boolean turn() {
        if (p1.getCharacter().human() && p2.getCharacter() instanceof NPC) {
            Global.gui().loadPortrait((NPC) p2.getCharacter());
        } else if (p2.getCharacter().human() && p1.getCharacter() instanceof NPC) {
            Global.gui().loadPortrait((NPC) p1.getCharacter());
        }
        if (phase.getEnum() != CombatPhase.FINISHED_SCENE && phase.getEnum() != CombatPhase.RESULTS_SCENE && checkLosses()) {
            phase = new ResultsScenePhase();
            return phase.next(this);
        }
        if ((p1.getCharacter().orgasmed || p2.getCharacter().orgasmed) && phase.getEnum() != CombatPhase.RESULTS_SCENE && SKIPPABLE_PHASES.contains(phase.getEnum())) {
            phase = new UpkeepPhase();
        }
        return phase.turn(this);
    }

    private String describe(Character player, Character other) {
        if (beingObserved) {
            return "<font color='rgb(255,220,220)'>"
                            + other.describe(Global.getPlayer().get(Attribute.Perception), Global.getPlayer())
                            + "</font><br/><br/><font color='rgb(220,220,255)'>"
                            + player.describe(Global.getPlayer().get(Attribute.Perception), Global.getPlayer())
                            + "</font><br/><br/><font color='rgb(134,196,49)'><b>"
                            + Global.capitalizeFirstLetter(getStance().describe(this)) + "</b></font>";
        } else if (!player.is(Stsflag.blinded)) {
            return other.describe(player.get(Attribute.Perception), Global.getPlayer()) + "<br/><br/>"
                            + Global.capitalizeFirstLetter(getStance().describe(this)) + "<br/><br/>"
                            + player.describe(other.get(Attribute.Perception), other) + "<br/><br/>";
        } else {
            return "<b>You are blinded, and cannot see what " + other.getTrueName() + " is doing!</b><br/><br/>"
                            + Global.capitalizeFirstLetter(getStance().describe(this)) + "<br/><br/>"
                            + player.describe(other.get(Attribute.Perception), other) + "<br/><br/>";
        }
    }

    protected Result eval() {
        if (getStance().bottom.human() && getStance().inserted(getStance().top) && getStance().en == Stance.anal) {
            return Result.anal;
        } else if (getStance().inserted()) {
            return Result.intercourse;
        } else {
            return Result.normal;
        }
    }

    public static List<Skill> WORSHIP_SKILLS = Arrays.asList(new BreastWorship(null), new CockWorship(null), new FootWorship(null),
                    new PussyWorship(null), new Anilingus(null));
    public static final String TEMPT_WORSHIP_BONUS = "TEMPT_WORSHIP_BONUS";
    public boolean combatMessageChanged;
    private boolean paused;
    private boolean processedEnding;

    
    //FIXME: Worship skills may not be properly changing stance - resulting in the worshipped character orgasming and triggering orgasm effectgs as if the player was still inserted. - DSM 
    public Optional<Skill> getRandomWorshipSkill(Character self, Character other) {
        List<Skill> avail = new ArrayList<Skill>(WORSHIP_SKILLS);
        if (other.has(Trait.piety)) {
            avail.add(new ConcedePosition(self));
        }
        Collections.shuffle(avail);
        while (!avail.isEmpty()) {
            Skill skill = avail.remove(avail.size() - 1)
                               .copy(self);
            if (Skill.isUsableOn(this, skill, other)) {
                write(other, Global.format(
                                "<b>{other:NAME-POSSESSIVE} divine aura forces {self:subject} to forget what {self:pronoun} {self:action:were|was} doing and crawl to {other:direct-object} on {self:possessive} knees.</b>",
                                self, other));
                return Optional.of(skill);
            }
        }
        return Optional.ofNullable(null);
    }

    private boolean rollWorship(Character self, Character other) {
        if (!other.isPet() && (other.has(Trait.objectOfWorship) || self.is(Stsflag.lovestruck))
                        && (other.breastsAvailable() || other.crotchAvailable())) {
            double chance = Math.min(20, Math.max(5, other.get(Attribute.Divinity) + 10 - self.getLevel()));
            if (other.has(Trait.revered)) {
                chance += 10;
            }
            chance += getCombatantData(self).getDoubleFlag(TEMPT_WORSHIP_BONUS);
            if (Global.random(100) < chance) {
                getCombatantData(self).setDoubleFlag(TEMPT_WORSHIP_BONUS, 0);
                return true;
            }            
        }
        return false;
    }

    private boolean rollAssWorship(Character self, Character opponent) {
        int chance = 0;
        if (opponent.has(Trait.temptingass) && !opponent.isPet()) {
            chance += Math.max(0, Math.min(15, opponent.get(Attribute.Seduction) - self.get(Attribute.Seduction)));
            if (self.is(Stsflag.feral))
                chance += 10;
            if (self.is(Stsflag.charmed) || opponent.is(Stsflag.alluring))
                chance += 5;
            if (self.has(Trait.assmaster) || self.has(Trait.analFanatic))
                chance += 5;
            Optional<BodyFetish> fetish = self.body.getFetish(AssPart.TYPE);
            if (fetish.isPresent() && opponent.has(Trait.bewitchingbottom)) {
                chance += 20 * fetish.get().magnitude;
            }
        }
        return Global.random(100) < chance;
    }

    private Skill checkWorship(Character self, Character other, Skill def) {
        if (rollWorship(self, other)) {
            return getRandomWorshipSkill(self, other).orElse(def);
        }
        if (rollAssWorship(self, other)) {
            AssFuck fuck = new AssFuck(self);
            if (fuck.requirements(this, other) && fuck.usable(this, other) && !self.is(Stsflag.frenzied)) {
                write(other, Global.format("<b>The look of {other:name-possessive} ass,"
                                        + " so easily within {self:possessive} reach, causes"
                                        + " {self:subject} to involuntarily switch to autopilot."
                                        + " {self:SUBJECT} simply {self:action:NEED|NEEDS} that ass.</b>",
                                self, other));
                self.add(this, new Frenzied(self, 1));
                return fuck;
            }
            Anilingus anilingus = new Anilingus(self);
            if (anilingus.requirements(this, other) && anilingus.usable(this, other)) {
                write(other, Global.format("<b>The look of {other:name-possessive} ass,"
                                        + " so easily within {self:possessive} reach, causes"
                                        + " {self:subject} to involuntarily switch to autopilot."
                                        + " {self:SUBJECT} simply {self:action:NEED|NEEDS} that ass.</b>",
                                self, other));
                return anilingus;
            }
        }
        return def;
    }

    public boolean doAction(Character self, Character target, Skill action) {

        Skill skill = checkWorship(self, target, action);

        boolean results = resolveSkill(skill, target);
        this.write("<br/>");
        updateMessage();

        return results;
    }

    public void act(Character c, Skill action) {
        if (c == p1.getCharacter()) {
            p1act = action;
        }
        if (c == p2.getCharacter()) {
            p2act = action;
        }
    }

    private Character pickTarget(PetCharacter pet) {
        if (otherCombatants.size() == 1 || Global.random(2) == 0) {
            return getOpponentCharacter(pet);
        }
        Character tgt;
        do {
            tgt = Global.pickRandom(otherCombatants).get();
        } while (!petsCanFight(pet, tgt));
        return tgt;
    }

    private boolean petsCanFight(PetCharacter pet, Character target) {
        if (target == null || pet == target || pet.getSelf().owner().equals(target)) {
            return false;
        }
        if (!target.isPet()) {
            return true;
        }
        return !((PetCharacter) target).getSelf().owner().equals(pet.getSelf().owner());
    }
    
    private void doStanceTick(Character self) {
        Character other = getStance().getPartner(this, self);
        Addiction add = other.getAddiction(AddictionType.DOMINANCE).orElse(null);       //FIXME: Causes trigger even though addiction has 0 magnitude.
        if (add != null && add.atLeast(Severity.MED) && !add.wasCausedBy(self)) {
            write(self, Global.format("{self:name} does {self:possessive} best to be dominant, but with the "
                        + "way "+ add.getCause().getName() + " has been working {self:direct-object} over {self:pronoun-action:are} completely desensitized." , self, other));
            return;
        }

        if (self.has(Trait.smqueen)) {
                write(self,
                            Global.format("{self:NAME-POSSESSIVE} cold gaze in {self:possessive} dominant position"
                                            + " makes {other:direct-object} shiver.",
                                            self, other));
        } else if (getStance().time % 2 == 0 && getStance().time > 0) {
            if (other.has(Trait.indomitable)) {
                write(self, Global.format("{other:SUBJECT}, typically being the dominant one,"
                                + " {other:action:are|is} simply refusing to acknowledge {self:name-possessive}"
                                + " current dominance.", self, other));
            } else {
                write(self, Global.format("{other:NAME-POSSESSIVE} compromising position takes a toll on {other:possessive} willpower.",
                                            self, other));
            }
        }
        
        if (self.has(Trait.confidentdom) && Global.random(2) == 0) {
            Attribute attr;
            String desc;
            if (self.get(Attribute.Ki) > 0 && Global.random(2) == 0) {
                attr = Attribute.Ki;
                desc = "strengthening {self:possessive} focus on martial discipline";
            } else if (Global.random(2) == 0) {
                attr = Attribute.Power;
                desc = "further empowering {self:possessive} muscles";
            } else {
                attr = Attribute.Cunning;
                desc = "granting {self:direct-object} increased mental clarity";
            }
            write(self, Global.format("{self:SUBJECT-ACTION:feel|feels} right at home atop"
                            + " {other:name-do}, %s.", self, other, desc));
            self.add(this, new Abuff(self, attr, Global.random(3) + 1, 10));
        }

        if (self.has(Trait.unquestionable) && Global.random(4) == 0) {
            write(self, Global.format("<b><i>\"Stay still, worm!\"</i> {self:subject-action:speak|speaks}"
                            + " with such force that it casues {other:name-do} to temporarily"
                            + " cease resisting.</b>", self, other));
            other.add(this, new Flatfooted(other, 1, false));
        }

        Optional<String> compulsion = Compulsive.describe(this, self, Compulsive.Situation.STANCE_FLIP);
        if (compulsion.isPresent() && Global.random(10) < 3 && new Reversal(other).usable(this, self)) {
            self.pain(this, null, Global.random(20, 50));
            Position nw = stance.reverse(this, false);
            if (!stance.equals(nw)) {
                stance = nw;
            } else {
                stance = new Pin(other, self);
            }
            write(self, compulsion.get());
            Compulsive.doPostCompulsion(this, self, Situation.STANCE_FLIP);
        }
    }

    private boolean checkCounter(Character attacker, Character target, Skill skill) {
        return !target.has(Trait.submissive) && getStance().mobile(target)
                        && target.counterChance(this, attacker, skill) > Global.random(100);
    }

    private boolean resolveCrossCounter(Skill skill, Character target, int chance) {
        if (target.has(Trait.CrossCounter) && Global.random(100) < chance) {
            if (!target.human()) {
                write(target, Global.format("As {other:SUBJECT-ACTION:move|moves} to counter, {self:subject-action:seem|seems} to disappear from {other:possessive} line of sight. "
                                + "A split second later, {other:pronoun-action:are|is} lying on the ground with a grinning {self:name-do} standing over {other:direct-object}. "
                                + "How did {self:pronoun} do that!?", skill.user(), target));
            } else {
                write(target, Global.format("As {other:subject} moves to counter your assault, you press {other:possessive} arms down with your weight and leverage {other:possessive} "
                                + "forward motion to trip {other:direct-object}, sending the poor {other:girl} crashing onto the floor.", skill.user(), target));
            }
            skill.user().add(this, new Falling(skill.user()));
            return true;
        }
        return false;
    }

    boolean resolveSkill(Skill skill, Character target) {
        boolean orgasmed = false;
        boolean madeContact = false;
        if (Skill.isUsableOn(this, skill, target)) {
            boolean success;
            if (!target.human() || !target.is(Stsflag.blinded)) {
                write(skill.user()
                           .subjectAction("use ", "uses ") + skill.getLabel(this) + ".");
            }
            if (skill.makesContact(this) && !getStance().dom(target) && target.canAct()
                            && checkCounter(skill.user(), target, skill)) {
                write("Countered!");
                if (!resolveCrossCounter(skill, target, 25)) {
                    target.counterattack(skill.user(), skill.type(this), this);
                }
                madeContact = true;
                success = false;
            } else if (target.is(Stsflag.counter) && skill.makesContact(this)) {
                write("Countered!");
                if (!resolveCrossCounter(skill, target, 50)) {
                    CounterStatus s = (CounterStatus) target.getStatus(Stsflag.counter);
                    if (skill.user()
                             .is(Stsflag.wary)) {
                        write(target, s.getCounterSkill()
                                       .getBlockedString(this, skill.user()));
                    } else {
                        s.resolveSkill(this, skill.user());
                    }
                }
                madeContact = true;
                success = false;
            } else {
                success = Skill.resolve(skill, this, target);
                madeContact |= success && skill.makesContact(this);
            }
            if (success) {
                if (skill.getTags(this).contains(SkillTag.thrusting) && skill.user().has(Trait.Jackhammer) && Global.random(2) == 0) {
                    write(skill.user(), Global.format("{self:NAME-POSSESSIVE} hips don't stop as {self:pronoun-action:continue|continues} to fuck {other:direct-object}.", skill.user(), target));
                    Skill.resolve(new WildThrust(skill.user()), this, target);
                }
                if (skill.getTags(this).contains(SkillTag.thrusting) && skill.user().has(Trait.Piledriver) && Global.random(3) == 0) {
                    write(skill.user(), Global.format("{self:SUBJECT-ACTION:fuck|fucks} {other:name-do} <b>hard</b>, so much so that {other:pronoun-action:are|is} momentarily floored by the stimulation.", skill.user(), target));
                    target.add(this, new Stunned(target, 1, false));
                }
                if (skill.type(this) == Tactics.damage) {
                    checkAndDoPainCompulsion(skill.user());
                }
            }
            if (skill.type(this) == Tactics.damage) {
                checkAndDoPainCompulsion(skill.user());
            }
            if (madeContact) {
            	resolveContactBonuses(skill.user(), target);
            	resolveContactBonuses(target, skill.user());
            }
            checkStamina(target);
            checkStamina(skill.user());
            orgasmed = checkOrgasm(skill.user(), target, skill);
            lastFailed = false;
        } else {
            write(skill.user()
                       .possessiveAdjective() + " " + skill.getLabel(this) + " failed.");
            lastFailed = true;
        }
        return orgasmed;
    }

    private void checkAndDoPainCompulsion(Character self) {
        Optional<String> compulsion = Compulsive.describe(this, self, Situation.PUNISH_PAIN);
        if (compulsion.isPresent()) {
            self.pain(this, null, Global.random(10, 40));
            write(compulsion.get());
            Compulsive.doPostCompulsion(this, self, Situation.PUNISH_PAIN);
        }
    }
    
    private void resolveContactBonuses(Character contacted, Character contacter) {
		if (contacted.has(Trait.VolatileSubstrate) && contacted.has(Trait.slime)) {
			contacter.add(this, new Slimed(contacter, contacted, 1));
		}
	}

	private boolean checkOrgasm(Character user, Character target, Skill skill) {
        return target.orgasmed || user.orgasmed;
    }

    public void write(String text) {
        text = Global.capitalizeFirstLetter(text);
        if (text.isEmpty()) {
            return;
        }
        String added = message + "<br/>" + text;
        message = added;
        wroteMessage = true;
    }

    public void updateMessage() {
        Global.gui().refresh();
        p1.getCharacter().message(message);
        p2.getCharacter().message(message);
    }

    public void updateAndClearMessage() {
        Global.gui().clearText();
        updateMessage();
    }

    public void write(Character user, String text) {
        text = Global.capitalizeFirstLetter(text);
        message = message + Global.colorizeMessage(user, text);
        wroteMessage = true;
    }

    public void checkStamina(Character p) {
        if (p.getStamina().isAtUnfavorableExtreme() && !p.is(Stsflag.stunned)) {
            p.add(this, new Winded(p, 3));
            if (p.isPet()){
                // pets don't get stance changes
                return;
            }
            Character other;
            if (p == p1.getCharacter()) {
                other = p2.getCharacter();
            } else {
                other = p1.getCharacter();
            }
            if (!getStance().prone(p)) {
                if (!getStance().mobile(p) && getStance().dom(other)) {
                    if (p.human()) {
                        write(p, "Your legs give out, but " + other.getName() + " holds you up.");
                    } else {
                        write(p, String.format("%s slumps in %s arms, but %s %s %s to keep %s from collapsing.",
                                        p.subject(), other.nameOrPossessivePronoun(),
                                        other.pronoun(), other.action("support"), p.objectPronoun(),
                                        p.objectPronoun()));
                    }
                } else if (getStance().havingSex(this, p) && getStance().dom(p) && getStance().reversable(this)) {
                    write(getOpponentCharacter(p), Global.format("{other:SUBJECT-ACTION:take|takes} the chance to shift into a more dominant position.", p, getOpponentCharacter(p)));
                    setStance(getStance().reverse(this, false));
                } else {
                    if (stance.havingSex(this)) {
                        setStance(stance.reverse(this, true));
                    } else {
                        if (p.human()) {
                            write(p, "You don't have the strength to stay on your feet. You slump to the floor.");
                        } else {
                            write(p, p.getName() + " drops to the floor, exhausted.");
                        } 
                        setStance(new StandingOver(other, p), null, false);
                    }
                }
                p.loseWillpower(this, Math.min(p.getWillpower()
                                                .max()
                                / 8, 15), true);
            }
            if (other.has(Trait.dominatrix)) {
                if (p.hasAddiction(AddictionType.DOMINANCE)) {
                    write(other, String.format("Being dominated by %s again reinforces %s"
                                    + " submissiveness towards %s.", other.getName(), p.nameOrPossessivePronoun(),
                                    other.objectPronoun()));
                } else {
                    write(other, Global.format("There's something about the way {other:subject-action:know} just"
                                    + " how and where to hurt {self:name-do} which some part of {self:possessive}"
                                    + " psyche finds strangely appealing. {self:SUBJECT-ACTION:find} {self:reflective}"
                                    + " wanting more.", p, other));
                }
                p.addict(this, AddictionType.DOMINANCE, other, Addiction.HIGH_INCREASE);
            }
        }
    }

    private boolean next() {
        assert phase.getEnum() != CombatPhase.ENDED;
        if (shouldAutoresolve()) {
            return true;
        }
        if (!(wroteMessage || phase.getEnum() == CombatPhase.START)
                || !beingObserved
                || (Global.checkFlag(Flag.AutoNext) && FAST_COMBAT_SKIPPABLE_PHASES.contains(phase.getEnum()))) {
            return false;
        } else {
            if (!paused) {
                p1.getCharacter().nextCombat(this);
                p2.getCharacter().nextCombat(this);
                // This is a horrible hack to catch the case where the player is watching or
                // has intervened in the combat
                if (!(p1.getCharacter().human() || p2.getCharacter().human()) && beingObserved) {
                    Global.getPlayer().nextCombat(this);
                }
            }
            return true;
        }
    }

    private void autoresolve() {
        assert !p1.getCharacter().human() && !p2.getCharacter().human() && !beingObserved;
        assert timer == 0;
        while (timer < NPC_TURN_LIMIT && !winner.isPresent()) {
            turn();
        }
        if (timer < NPC_TURN_LIMIT) {
            double fitness1 = p1.getCharacter().getFitness(this);
            double fitness2 = p2.getCharacter().getFitness(this);
            double diff = Math.abs(fitness1 / fitness2 - 1.0);
            if (diff > NPC_DRAW_ERROR_MARGIN) {
                victory(fitness1 > fitness2 ? p1 : p2);
            } else {
                draw();
            }
        }
        phase = new EndedPhase();
        phase.next(this);
    }

    public void intervene(Participant intruder, Participant assist) {
        Combatant target;
        if (p1.getParticipant() == assist) {
            target = p2;
        } else {
            target = p1;
        }
        var targetCharacter = target.getCharacter();
        var assistCharacter = assist.getCharacter();
        var intruderCharacter = intruder.getCharacter();

        if (targetCharacter.resist3p(this, intruderCharacter, assistCharacter)) {
            targetCharacter.gainXP(20 + targetCharacter.lvlBonus(intruderCharacter));
            targetCharacter.orgasm();
            targetCharacter.undress(this);

            intruderCharacter.gainXP(10 + intruderCharacter.lvlBonus(targetCharacter));
            target.getParticipant().invalidateAttacker(intruder);
        } else {
            intruderCharacter.gainXP(intruderCharacter.getAssistXP(targetCharacter));
            intruderCharacter.intervene3p(this, targetCharacter, assistCharacter);

            targetCharacter.gainXP(targetCharacter.getDefeatXP(assistCharacter));
            targetCharacter.orgasm();
            targetCharacter.undress(this);
            target.getParticipant().invalidateAttacker(assist);
            target.getParticipant().invalidateAttacker(intruder);
            targetCharacter.gainAttraction(assistCharacter, 1);

            assistCharacter.gainAttraction(intruderCharacter, 1);
            assistCharacter.gainXP(assistCharacter.getVictoryXP(targetCharacter));
            assistCharacter.dress(this);
            assistCharacter.gainTrophy(this, targetCharacter);
            assistCharacter.victory3p(this, targetCharacter, intruderCharacter);
            assistCharacter.gainAttraction(targetCharacter, 1);
            assist.incrementScore(assist.pointsForVictory(target.getParticipant()), "for an unearned win");
        }

        phase = new ResultsScenePhase();
        if (!(p1.getCharacter().human() || p2.getCharacter().human() || intruderCharacter.human())) {
            end();
        } else {
            Global.gui().watchCombat(this);
            resumeNoClearFlag();
        }
    }

    private void end() {
        p1.getParticipant().state = new Participant.ReadyState();
        p2.getParticipant().state = new Participant.ReadyState();
        if (processedEnding) {
            if (beingObserved) {
                Global.gui().endCombat();
            }
            return;
        }
        boolean hasScene = false;
        if (p1.getCharacter().human() || p2.getCharacter().human()) {
            if (postCombatScenesSeen < 3) {
                if (!p2.getCharacter().human() && p2.getCharacter() instanceof NPC) {
                    hasScene = doPostCombatScenes((NPC)p2.getCharacter());
                } else if (!p1.getCharacter().human() && p1.getCharacter() instanceof NPC) {
                    hasScene = doPostCombatScenes((NPC)p1.getCharacter());
                }
                if (hasScene) {
                    postCombatScenesSeen += 1;
                    return;
                }
            } else {
                p1.getCharacter().nextCombat(this);
                p2.getCharacter().nextCombat(this);
                // This is a horrible hack to catch the case where the player is watching or
                // has intervened in the combat
                if (!(p1.getCharacter().human() || p2.getCharacter().human()) && beingObserved) {
                    Global.getPlayer().nextCombat(this);
                }
            }
        }
        processedEnding = true;
        p1.getCharacter().endofbattle(this);
        p2.getCharacter().endofbattle(this);
        getCombatantData(p1.getCharacter()).getRemovedItems().forEach(p1.getCharacter()::gain);
        getCombatantData(p2.getCharacter()).getRemovedItems().forEach(p2.getCharacter()::gain);
        location.endEncounter();
        // it's a little ugly, but we must be mindful of lazy evaluation
        boolean ding = p1.getCharacter().levelUpIfPossible(this) && p1.getCharacter().human();
        ding = (p2.getCharacter().levelUpIfPossible(this) && p2.getCharacter().human()) || ding;
        if (doExtendedLog()) {
            log.logEnd(winner.map(Combatant::getCharacter));
        }

        if (!ding && beingObserved) {
            Global.gui().endCombat();
        }
    }

    private boolean doPostCombatScenes(NPC npc) {
        List<CombatScene> availableScenes = npc.getPostCombatScenes()
                        .stream()
                        .filter(scene -> scene.meetsRequirements(this, npc))
                        .collect(Collectors.toList());
        Optional<CombatScene> possibleScene = Global.pickRandom(availableScenes);
        if (possibleScene.isPresent()) {
            Global.gui().clearText();
            possibleScene.get().visit(this, npc);
            return true;
        } else {
            return false;
        }
    }

    public void petbattle(Pet one, Pet two) {
        int roll1 = Global.random(20) + one.power();
        int roll2 = Global.random(20) + two.power();
        if (one.hasPussy() && two.hasDick()) {
            roll1 += 3;
        } else if (one.hasDick() && two.hasPussy()) {
            roll2 += 3;
        }
        if (roll1 > roll2) {
            one.vanquish(this, two);
        } else if (roll2 > roll1) {
            two.vanquish(this, one);
        } else {
            write(one.getName() + " and " + two.getName()
                            + " engage each other for awhile, but neither can gain the upper hand.");
        }
    }

    @Override
    public Combat clone() throws CloneNotSupportedException {
        Combat c = (Combat) super.clone();
        c.p1 = p1.copy();
        c.p2 = p2.copy();
        c.p1.getCharacter().finishClone(c.p2.getCharacter());
        c.p2.getCharacter().finishClone(c.p1.getCharacter());
        c.combatantData = new HashMap<>();
        combatantData.forEach((name, data) -> c.combatantData.put(name, (CombatantData) data.clone()));
        c.stance = getStance().clone();
        c.state = state;
        if (c.getStance().top == p1.getCharacter()) {
            c.getStance().top = c.p1.getCharacter();
        }
        if (c.getStance().top == p2.getCharacter()) {
            c.getStance().top = c.p2.getCharacter();
        }
        if (c.getStance().bottom == p1.getCharacter()) {
            c.getStance().bottom = c.p1.getCharacter();
        }
        if (c.getStance().bottom == p2.getCharacter()) {
            c.getStance().bottom = c.p2.getCharacter();
        }
        c.otherCombatants = new ArrayList<>();
        for (PetCharacter pet : otherCombatants) {
            if (pet.isPetOf(p1.getCharacter())) {
                c.otherCombatants.add(pet.cloneWithOwner(c.p1.getCharacter()));
            } else if (pet.isPetOf(p2.getCharacter())) {
                c.otherCombatants.add(pet.cloneWithOwner(c.p2.getCharacter()));
            }
        }
        c.getStance().setOtherCombatants(c.otherCombatants);
        c.postCombatScenesSeen = this.postCombatScenesSeen;
        c.cloned = true;
        return c;
    }

    public Skill lastact(Character user) {
        if (user == p1.getCharacter()) {
            return p1act;
        } else if (user == p2.getCharacter()) {
            return p2act;
        } else {
            return null;
        }
    }

    public void offerImage(String path, String artist) {
        imagePath = path;
        if (!imagePath.isEmpty() && !cloned && isBeingObserved()) {
            Global.gui()
                  .displayImage(imagePath, images.get(imagePath));
        }
    }

    public Position getStance() {
        return stance;
    }

    public void checkStanceStatus(Character c, Position oldStance, Position newStance) {
        if (oldStance.sub(c) && !newStance.sub(c)) {
            if ((oldStance.prone(c) || !oldStance.mobile(c)) && !newStance.prone(c) && newStance.mobile(c)) {
                c.add(this, new Braced(c));
                c.add(this, new Wary(c, 3));
            } else if (!oldStance.mobile(c) && newStance.mobile(c)) {
                c.add(this, new Wary(c, 3));
            }
        }
    }

    public void setStance(Position newStance) {
        setStance(newStance, null, true);
    }

    private void doEndPenetration(Character self, Character partner) {
        List<BodyPart> parts1 = stance.getPartsFor(this, self, partner);
        List<BodyPart> parts2 = stance.getPartsFor(this, partner, self);
        parts1.forEach(part -> parts2.forEach(other -> part.onEndPenetration(this, self, partner, other)));
        parts2.forEach(part -> parts1.forEach(other -> part.onEndPenetration(this, partner, self, other)));
    }

    private void doStartPenetration(Position stance, Character self, Character partner) {
        List<BodyPart> parts1 = stance.getPartsFor(this, self, partner);
        List<BodyPart> parts2 = stance.getPartsFor(this, partner, self);
        parts1.forEach(part -> parts2.forEach(other -> part.onStartPenetration(this, self, partner, other)));
        parts2.forEach(part -> parts1.forEach(other -> part.onStartPenetration(this, partner, self, other)));
    }

    public void setStanceRaw(Position stance) {
        this.stance = stance;
    }

    public void setStance(Position newStance, Character initiator, boolean voluntary) {
        if ((newStance.top.isPet() && newStance.bottom.isPet())
                || ((newStance.top.isPet() || newStance.bottom.isPet())
                    && getStance().en != Stance.neutral
                    && !newStance.isThreesome())) {
            // Pets don't get into stances with each other, and they don't usurp stances.
            // Threesomes are exceptions to this.
            return;
        }
        if ((newStance.top != getStance().bottom && newStance.top != getStance().top)
            || (newStance.bottom != getStance().bottom && newStance.bottom != getStance().top)) {
            if (initiator != null && initiator.isPet() && newStance.top == initiator) {
                PetInitiatedThreesome threesomeSkill = new PetInitiatedThreesome(initiator);
                if (newStance.havingSex(this)) {
                    threesomeSkill.resolve(this, newStance.bottom);
                } else if (!getStance().sub(newStance.bottom)) {
                    write(initiator, Global.format("{self:SUBJECT-ACTION:take|takes} the chance to send {other:name-do} sprawling to the ground", initiator, newStance.bottom));
                    newStance.bottom.add(this, new Falling(newStance.bottom));
                }
            }
            return;
        }
        if (initiator != null) {
            Character otherCharacter = getOpponentCharacter(initiator);
            if (voluntary
                && newStance.en == Stance.neutral
                && getStance().en != Stance.kneeling
                && otherCharacter.has(Trait.genuflection)
                && rollWorship(initiator, otherCharacter)) {
                write(initiator, Global.format("While trying to get back up, {self:name-possessive} eyes accidentally met {other:name-possessive} gaze. "
                                + "Like a deer in headlights, {self:possessive} body involuntarily stops moving and kneels down before {other:direct-object}.", initiator, otherCharacter));
                newStance = new Kneeling(otherCharacter, initiator);
            }
        }
        checkStanceStatus(p1.getCharacter(), stance, newStance);
        checkStanceStatus(p2.getCharacter(), stance, newStance);

        if (stance.inserted() && !newStance.inserted()) {
            doEndPenetration(p1.getCharacter(), p2.getCharacter());
            Character threePCharacter = stance.domSexCharacter(this);
            if (threePCharacter != p1.getCharacter() && threePCharacter != p2.getCharacter()) {
                doEndPenetration(p1.getCharacter(), threePCharacter);
                doEndPenetration(p2.getCharacter(), threePCharacter);
                getCombatantData(threePCharacter).setIntegerFlag("ChoseToFuck", 0);
            }
            getCombatantData(p1.getCharacter()).setIntegerFlag("ChoseToFuck", 0);
            getCombatantData(p2.getCharacter()).setIntegerFlag("ChoseToFuck", 0);
        } else if (!stance.inserted() && newStance.inserted() && (newStance.penetrated(this, p1.getCharacter()) || newStance.penetrated(this, p2.getCharacter())) ) {
            doStartPenetration(newStance, p1.getCharacter(), p2.getCharacter());
        } else if (!stance.havingSex(this) && newStance.havingSex(this)) {
            Character threePCharacter = stance.domSexCharacter(this);
            if (threePCharacter != p1.getCharacter() && threePCharacter != p2.getCharacter()) {
                doStartPenetration(newStance, p1.getCharacter(), threePCharacter);
                doStartPenetration(newStance, p2.getCharacter(), threePCharacter);
            }

            if (voluntary) {
                if (initiator != null) {
                    getCombatantData(initiator).setIntegerFlag("ChoseToFuck", 1);
                    getCombatantData(getOpponentCharacter(initiator)).setIntegerFlag("ChoseToFuck", -1);
                }
            }
            checkBreeder(p1.getCharacter(), voluntary);
            checkBreeder(p2.getCharacter(), voluntary);
        }

        if (stance != newStance && initiator != null && initiator.has(Trait.Catwalk)) {
            write(initiator, Global.format("The way {self:subject-action:move|moves} exudes such feline grace that it demands {other:name-possessive} attention.",
                            initiator, getOpponentCharacter(initiator)));
            initiator.add(this, new Alluring(initiator, 1));
        }
        stance = newStance;
        offerImage(stance.image(), "");
    }

    /**Checks if the opponent has breeder - currently presumes Kat is the only character with it and outputs text. 
     * 
     * FIXME: this is currently hardcoded and needs to be moved elsewhere. The text and activation for this trait needs to be sent into the traint itself.*/
    private void checkBreeder(Character checked, boolean voluntary) {
        Character opp = getStance().getPartner(this, checked);
        if (checked.checkAddiction(AddictionType.BREEDER, opp) && getStance().inserted(checked)) {
            if (voluntary) {
                write(checked, "As you enter Kat, instinct immediately kicks in. It just"
                                + " feels so right, like this is what you're supposed"
                                + " to be doing all the time.");
                checked.addict(this, AddictionType.BREEDER, opp, Addiction.MED_INCREASE);
            } else {
                write(checked, "Something shifts inside of you as Kat fills herself with"
                                + " you. A haze descends over your mind, clouding all but a desire"
                                + " to fuck her as hard as you can.");
                checked.addict(this, AddictionType.BREEDER, opp, Addiction.LOW_INCREASE);
            }
        }
    }

    public Combatant getOpponent(Character self) {
        if (self.equals(p1.getCharacter()) || self.isPetOf(p1.getCharacter())) {
            return p2;
        }
        if (self.equals(p2.getCharacter()) || self.isPetOf(p2.getCharacter())) {
            return p1;
        }
        throw new RuntimeException(String.format("no opponent found for %s", self));
    }

    public Character getOpponentCharacter(Character self) {
        return getOpponent(self).getCharacter();
    }

    public void writeSystemMessage(String battleString, boolean basic) {
        if (Global.checkFlag(Flag.systemMessages) || basic 
                        && Global.checkFlag(Flag.basicSystemMessages)) {
            write(battleString);
        }
    }

    public void writeSystemMessage(Character character, String string) {
        if (Global.checkFlag(Flag.systemMessages)) {
            write(character, string);
        }
    }

    public int getTimer() {
        return timer;
    }

    private boolean doExtendedLog() {
        return (p1.getCharacter().human() || p2.getCharacter().human()) && Global.checkFlag(Flag.extendedLogs);
    }

    public boolean isBeingObserved() {
        return beingObserved;
    }

    public void setBeingObserved(boolean beingObserved) {
        this.beingObserved = beingObserved;
    }
    
    public boolean shouldPrintReceive(Character ch, Combat c) {
        return beingObserved || (c.p1.getCharacter().human() || c.p2.getCharacter().human());
    }
    
    public boolean shouldAutoresolve() {
        return !(p1.getCharacter().human() || p2.getCharacter().human()) && !beingObserved;
    }

    public String bothDirectObject(Character target) {
        return target.human() ? "you" : "them";
    }
    
    public String bothPossessive(Character target) {
        return target.human() ? "your" :  "their";
    }
    
    public String bothSubject(Character target) {
        return target.human() ? "you" : "they";
    }

    public List<PetCharacter> getPetsFor(Character target) {
        return otherCombatants.stream().filter(c -> c.isPetOf(target)).collect(Collectors.toList());
    }

    public void removePet(PetCharacter self) {
        if (self.has(Trait.resurrection) && !getCombatantData(self).getBooleanFlag("resurrected")) {
            write(self, "Just as " + self.subject() + " was about to disappear, a dazzling light covers " 
            + self.possessiveAdjective() + " body. When the light fades, " + self.pronoun() + " looks completely refreshed!");
            getCombatantData(self).setBooleanFlag("resurrected", true);
            self.getStamina().renew();
            self.getArousal().renew();
            self.getMojo().renew();
            self.getWillpower().renew();
            return;
        }
        getCombatantData(self).setBooleanFlag("resurrected", false);
        otherCombatants.remove(self);
    }

    public void addPet(Character master, PetCharacter self) {
        if (self == null) {
            System.err.println("Something fucked up happened");
            Thread.dumpStack();
            return;
        }
        if (otherCombatants.contains(self)) {
            write(String.format("<b>ERROR: Tried to add %s as a pet for %s,"
                            + " but there is already a %s who is a pet for %s."
                            + " Please report this as a bug. The extra pet will not"
                            + " be added, and you can probably continue playing without"
                            + " problems.</b>", self.getTrueName(), master.getTrueName(),
                            self.getTrueName(), self.getSelf().owner().getTrueName()));
            Thread.dumpStack();
            return;
        }
        if (master.has(Trait.leadership)) {
            int levelups = Math.max(5, master.getLevel() / 4);
            self.getSelf().setPower(self.getSelf().getPower() + levelups);
            for (int i = 0; i < levelups; i++) {
                self.ding(this);
            }
        }
        if (master.has(Trait.tactician)) {
            self.getSelf().setAc(self.getSelf().getAc() + 3);
            self.getArousal().setMax((int) ((float) self.getArousal().max() * 1.5f));
            self.getStamina().setMax((int) ((float) self.getStamina().max() * 1.5f));
        }
        self.getStamina().renew();
        self.getArousal().renew();
        writeSystemMessage(self, Global.format("{self:SUBJECT-ACTION:have|has} summoned {other:name-do} (Level %s)",
                                        master, self, self.getLevel()));
        otherCombatants.add(self);
        this.write(self, self.challenge(getOpponentCharacter(self)));
    }

    public List<PetCharacter> getOtherCombatants() {
        return otherCombatants;
    }

    public boolean isEnded() {
        return phase.getEnum() == CombatPhase.FINISHED_SCENE || phase.getEnum() == CombatPhase.ENDED;
    }

    public void pause() {
        this.paused = true;
    }


    /**Collects any substances gained in this victory into an empty bottle.
     * 
     * TODO: Mark this for combat rebuild - this goes to one of the final phases on combat end. 
     * */
    public void doBottleCollection(Character victor, Character loser){
        
        if (loser.hasDick() && victor.has(Trait.succubus)) {
            victor.gain(Item.semen, 3);
            if (loser.human()) {
                write(victor, "<br/><b>As she leaves, you see all your scattered semen ooze out and gather into a orb in "
                                + victor.nameOrPossessivePronoun() + " hands. "
                                + "She casually drops your seed in some empty vials that appeared out of nowhere</b>");
            } else if (victor.human()) {
                write(victor, "<br/><b>" + loser.nameOrPossessivePronoun()
                                + " scattered semen lazily oozes into a few magically conjured flasks. "
                                + "To speed up the process, you milk " + loser.possessiveAdjective()
                                + " out of the last drops " + loser.subject()
                                + " had to offer. Yum, you just got some leftovers.</b>");
            }
        } else if (loser.hasDick() && (victor.human() || victor.has(Trait.madscientist)) && victor.has(Item.EmptyBottle, 1)) {
            write(victor, Global.format("<br/><b>{self:SUBJECT-ACTION:manage|manages} to collect some of {other:name-possessive} scattered semen in an empty bottle</b>", victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.semen, 1);
        }
         
        if (checkBottleCollection(victor, loser, DivineMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:name-possessive} divine pussy juices in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.HolyWater, 1);
        }
        if (checkBottleCollection(victor, loser, DemonicMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:name-possessive} demonic pussy juices in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.ExtremeAphrodisiac, 1);
        }
        if (checkBottleCollection(victor, loser, PlantMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} nectar in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.nectar, 3);
        }
        if (checkBottleCollection(victor, loser, CyberneticMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} artificial lubricant in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.LubricatingOils, 1);
        }
        if (checkBottleCollection(victor, loser, ArcaneMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of the floating mana wisps ejected from {other:possessive} orgasm in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.RawAether, 1);
        }
        if (checkBottleCollection(victor, loser, FeralMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} musky juices in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.FeralMusk, 1);
        }
        if (checkBottleCollection(victor, loser, GooeyMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} goo in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.BioGel, 1);
        }
        if (checkBottleCollection(victor, loser, FieryMod.TYPE)) {
            write(victor, Global.format(
                            "<br/><b>{other:SUBJECT-ACTION:shoot|shoots} {self:name-do} a dirty look as {self:subject-action:move|moves} to collect some of {other:possessive} excitement in an empty bottle</b>",
                            victor, loser));
            victor.consume(Item.EmptyBottle, 1, false);
            victor.gain(Item.MoltenDrippings, 1);
        }
    }

    public Character getP1Character() {
        return p1.getCharacter();
    }

    public Character getP2Character() {
        return p2.getCharacter();
    }
    
}
