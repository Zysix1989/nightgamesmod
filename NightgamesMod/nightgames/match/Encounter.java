package nightgames.match;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Enthralled;
import nightgames.status.Flatfooted;
import nightgames.status.Hypersensitive;
import nightgames.status.Stsflag;
import nightgames.trap.Spiderweb;
import nightgames.trap.Trap;

import java.util.Optional;
import java.util.Set;

public class Encounter {
    protected Participant p1;
    protected Participant p2;
    
    private boolean p1ff;
    private boolean p2ff;
    
    private transient Optional<String> p1Guaranteed;
    private transient Optional<String> p2Guaranteed;
    protected Area location;
    protected transient Combat fight;
    private int checkin;
    protected int fightTime;

    public Encounter(Participant first, Participant second, Area location) {
        this.location = location;
        p1 = first;
        p2 = second;
        checkin = 0;
        fight = null;
        p1Guaranteed = Optional.empty();
        p2Guaranteed = Optional.empty();
        checkEnthrall(p1, p2);
        checkEnthrall(p2, p1);
    }

    private static void checkEnthrall(Participant slave, Participant master) {
        nightgames.status.Status enthrall = slave.getCharacter().getStatus(Stsflag.enthralled);
        if (enthrall != null) {
            if (((Enthralled) enthrall).master != master.getCharacter()) {
                slave.getCharacter().removelist.add(enthrall);
                slave.getCharacter().addNonCombat(new nightgames.match.Status(new Flatfooted(slave.getCharacter(), 2)));
                slave.getCharacter().addNonCombat(new nightgames.match.Status(new Hypersensitive(slave.getCharacter())));
                slave.getCharacter().message("At " + master.getCharacter().getName() + "'s interruption, you break free from the"
                        + " succubus' hold on your mind. However, the shock all but"
                        + " short-circuits your brain; you "
                        + " collapse to the floor, feeling helpless and"
                        + " strangely oversensitive");
                master.getCharacter().message(String.format(
                        "%s doesn't appear to notice you at first, but when you wave your hand close to %s face %s "
                                + "eyes open wide and %s immediately drops to the floor. Although the display leaves you "
                                + "somewhat worried about %s health, %s is still in a very vulnerable position and you never "
                                + "were one to let an opportunity pass you by.",
                        slave.getCharacter().getName(), slave.getCharacter().possessiveAdjective(),
                        slave.getCharacter().possessiveAdjective(), slave.getCharacter().pronoun(),
                        slave.getCharacter().possessiveAdjective(), slave.getCharacter().pronoun()));
            }
        }
    }

    /**
     * Checks for and runs any scenarios that arise from two Characters encountering each other. 
     * Returns true if something has come up that prevents them from being presented with the usual
     * campus Actions.
     */
    public final boolean spotCheck() {
        if (p1.canStartCombat(p2) && p2.canStartCombat(p1)) {
            eligibleSpotCheck();
            return true;
        } else {
            ineligibleSpotCheck();
            return false;
        }
    }

    public static boolean spotCheck(Participant spotter, Participant hidden) {
        if (spotter.getCharacter().bound()) {
            return false;
        }
        int dc = hidden.getCharacter().get(Attribute.Cunning) / 3;
        dc += hidden.state.spotCheckDifficultyModifier(hidden);
        if (hidden.getCharacter().has(Trait.Sneaky)) {
            dc += 20;
        }
        dc -= dc * 5 / Math.max(1, spotter.getCharacter().get(Attribute.Perception));
        return spotter.getCharacter().check(Attribute.Cunning, dc);
    }

    public void spy(Participant attacker, Participant victim) {
        attacker.getCharacter().spy(victim, () -> ambush(attacker, victim), () -> location.endEncounter());
    }

    protected void eligibleSpotCheck() {
        var p1Replacement = p1.state.eligibleCombatReplacement(this, p2, p1);
        var p2Replacement = p2.state.eligibleCombatReplacement(this, p1, p2);
        assert p1Replacement.isEmpty() || p2Replacement.isEmpty();
        if (p1Replacement.isPresent()) {
            p1Replacement.get().run();
            return;
        }
        if (p2Replacement.isPresent()) {
            p2Replacement.get().run();
            return;
        }
        
        // We need to run both vision checks no matter what, and they have no
        // side effects besides.
        boolean p2_sees_p1 = spotCheck(p2, p1);
        boolean p1_sees_p2 = spotCheck(p1, p2);

        if (p2_sees_p1 && p1_sees_p2) {
            p1.getCharacter().faceOff(p2,
                    () -> fightOrFlight(p1, true, Optional.empty()),
                    () -> fightOrFlight(p1, false, Optional.empty()),
                    () -> fightOrFlight(p1, false, Optional.of(smokeMessage(p1.getCharacter()))));
            p2.getCharacter().faceOff(p1,
                    () -> fightOrFlight(p2, true, Optional.empty()),
                    () -> fightOrFlight(p2, false, Optional.empty()),
                    () -> fightOrFlight(p2, false, Optional.of(smokeMessage(p2.getCharacter()))));
        } else if (p2_sees_p1) {
            p2.getCharacter().spy(p1, () -> ambush(p2, p1), () -> location.endEncounter());
        } else if (p1_sees_p2) {
            p1.getCharacter().spy(p2, () -> ambush(p1, p2), () -> location.endEncounter());
        } else {
            // Ships passing in the night :(
            location.endEncounter();
        }
    }

    private static void ineligibleMessages(Participant pastWinner, Participant pastLoser) {
        pastLoser.getCharacter().message("You encounter " + pastWinner.getCharacter().getName() + ", but you still haven't recovered from your last fight.");
        pastWinner.getCharacter().message(String.format(
                "You find %s still naked from your last encounter, but %s's not fair game again until %s replaces %s clothes.",
                pastLoser.getCharacter().getName(), pastLoser.getCharacter().pronoun(), pastLoser.getCharacter().pronoun(), pastLoser.getCharacter().possessiveAdjective()));
    }

    private void ineligibleSpotCheck() {
        if (!p1.canStartCombat(p2)) {
            p1.state.ineligibleCombatReplacement(p1, p2).orElse(() -> ineligibleMessages(p1, p2)).run();
        } else {
            p2.state.ineligibleCombatReplacement(p2, p1).orElse(() -> ineligibleMessages(p2, p1)).run();
        }
        location.endEncounter();
    }

    /**
     * @param p The Character making the decision.
     * @param fight Whether the Character wishes to fight (true) or flee (false).
     * @param guaranteed Whether the Character's option is guaranteed to work. If so, the provided
     */
    private void fightOrFlight(Participant p, boolean fight, Optional<String> guaranteed) {
        if (p == p1) {
            p1ff = fight;
            p1Guaranteed = guaranteed;
        } else {
            p2ff = fight;
            p2Guaranteed = guaranteed;
        }
        checkin++;
        if (checkin >= 2) {
            doFightOrFlight();
        }
    }
    
    private void doFightOrFlight() {
        if (p1ff && p2ff) {
            startFight(p1, p2);
        } else if (p1ff) {
            fightOrFlee(p1, p2);
        } else if (p2ff) {
            fightOrFlee(p2, p1);
        } else {
            bothFlee();
        }
    }

    public Combat startFight(Participant p1, Participant p2) {
        fightTime = 2;
        if (p1.getCharacter() instanceof Player && p2.getCharacter() instanceof NPC) {
            this.fight = new Combat(p1, p2, p1.getCharacter().location()); // Not sure if order matters
        } else if (p2.getCharacter() instanceof Player && p1.getCharacter() instanceof NPC) {
            this.fight = new Combat(p2, p1, p2.getCharacter().location());
        } else {
            this.fight = new Combat(p1, p2, location);
        }
        p1.getCharacter().notifyCombatStart(fight, p2.getCharacter());
        p2.getCharacter().notifyCombatStart(fight, p1.getCharacter());
        return this.fight;
    }
    
    // One Character wishes to Fight while the other attempts to flee.
    private void fightOrFlee(Participant fighter, Participant fleer) {
        Optional<String> fighterGuaranteed = (fighter == p1) ? p1Guaranteed : p2Guaranteed;
        Optional<String> fleerGuaranteed = (fleer == p1) ? p1Guaranteed : p2Guaranteed;

        // Fighter wins automatically
        if (fighterGuaranteed.isPresent() && fleerGuaranteed.isEmpty()) {
            fighter.getCharacter().message(fighterGuaranteed.get());
            startFight(fighter, fleer);
            return;
        }

        // Fleer wins automatically
        if (fleerGuaranteed.isPresent()) {
            fleer.getCharacter().message(fleerGuaranteed.get());
            p2.flee();
            location.endEncounter();
            return;
        }

        // Roll to see who's will triumphs
        if (rollFightVsFlee(fighter.getCharacter(), fleer.getCharacter())) {
            fighter.getCharacter().message(fleer.getCharacter().getName() + " dashes away before you can move.");
            fleer.flee();
            location.endEncounter();
        } else {
            fighter.getCharacter().message(String.format(
                    "%s tries to run, but you stay right on %s heels and catch %s.",
                    fleer.getCharacter().getName(), fleer.getCharacter().possessiveAdjective(), fleer.getCharacter().objectPronoun()));
            fleer.getCharacter().message(String.format(
                    "You quickly try to escape, but %s is quicker. %s corners you and attacks.",
                    fighter.getCharacter().getName(), Global.capitalizeFirstLetter(fighter.getCharacter().pronoun())));
            startFight(fighter, fleer);
        }
    }
    
    /** Weights a roll with the fighter and fleers stats to determine who prevails. Returns
     * true if the fleer escapes, false otherwise.
     */
    private boolean rollFightVsFlee(Character fighter, Character fleer) {
        return fleer.check(Attribute.Speed, 10 + fighter.get(Attribute.Speed) + (fighter.has(Trait.sprinter) ? 5 : 0)
                        + (fleer.has(Trait.sprinter) ? -5 : 0));
    }

    private void bothFlee() {
        if (p1Guaranteed.isPresent()) {
            p1.getCharacter().message(p1Guaranteed.get());
            p2.getCharacter().message(p1Guaranteed.get());
            p1.flee();
        } else if (p2Guaranteed.isPresent()) {
            p1.getCharacter().message(p2Guaranteed.get());
            p2.getCharacter().message(p2Guaranteed.get());
            p2.flee();
        } else if (p1.getCharacter().get(Attribute.Speed) + Global.random(10) >= p2.getCharacter().get(Attribute.Speed) + Global.random(10)) {
            p2.getCharacter().message(p1.getCharacter().getName() + " dashes away before you can move.");
            p1.flee();
        } else {
            p1.getCharacter().message(p2.getCharacter().getName() + " dashes away before you can move.");
            p2.flee();
        }
        location.endEncounter();
    }

    public void ambush(Participant attacker, Participant target) {
        target.getCharacter().addNonCombat(new nightgames.match.Status(new Flatfooted(target.getCharacter(), 3)));
        var msg = Global.format("{self:SUBJECT-ACTION:catch|catches} {other:name-do} by surprise and {self:action:attack|attacks}!", attacker.getCharacter(), target.getCharacter());
        p1.getCharacter().message(msg);
        p2.getCharacter().message(msg);
        startFight(attacker, target);
    }

    public void caught(Participant attacker, Participant target) {
        target.getCharacter().message("You jerk off frantically, trying to finish as fast as possible. Just as you feel the familiar sensation of imminent orgasm, you're grabbed from behind. "
                + "You freeze, cock still in hand. As you turn your head to look at your attacker, "
                + attacker.getCharacter().getName()
                + " kisses you on the lips and rubs the head of your penis with her "
                + "palm. You were so close to the edge that just you cum instantly.");
        if (!target.getCharacter().mostlyNude()) {
            target.getCharacter().message("You groan in resignation and reluctantly strip off your clothes and hand them over.");
        }
        attacker.getCharacter().message("You spot " + target.getCharacter().getName()
                + " leaning against the wall with her hand working excitedly between her legs. She is mostly, but not completely successful at "
                + "stifling her moans. She hasn't noticed you yet, and as best as you can judge, she's pretty close to the end. It'll be an easy victory for you as long as you work fast. "
                + "You sneak up and hug her from behind while kissing the nape of her neck. She moans and shudders in your arms, but doesn't stop fingering herself. She probably realizes "
                + "she has no chance of winning even if she fights back. You help her along by licking her neck and fondling her breasts as she hits her climax.");

        if (!target.getCharacter().mostlyNude()) {
            attacker.getCharacter().gain(target.getCharacter().getTrophy());
        }
        attacker.getCharacter().gainXP(attacker.getCharacter().getVictoryXP(target.getCharacter()));
        attacker.getCharacter().tempt(20);
        attacker.incrementScore(attacker.pointsForVictory(target),
                "for a win, by being in the right place at the wrong time");
        attacker.state = new Action.Ready();

        target.getCharacter().gainXP(target.getCharacter().getDefeatXP(attacker.getCharacter()));
        target.getCharacter().nudify();
        target.invalidateAttacker(attacker);
        target.getCharacter().getArousal().renew();
        target.state = new Action.Ready();
        location.endEncounter();
    }

    public void spider(Participant attacker, Participant target) {
        attacker.getCharacter().gainXP(attacker.getCharacter().getVictoryXP(target.getCharacter()));
        target.getCharacter().gainXP(target.getCharacter().getDefeatXP(attacker.getCharacter()));
        Spiderweb.onSpiderwebDefeat(attacker, target, location.getTrap().orElseThrow());
    }

    public void intrude(Participant intruder, Participant assist) {
        fight.intervene(intruder, assist);
    }

    public void battle() {
        fightTime--;
        if (fightTime <= 0 && !fight.isEnded()) {
            fight.go();
        } else {
            Global.getMatch().resume();
        }
    }

    public Combat getCombat() {
        return fight;
    }

    public void trap(Participant opportunist, Participant target, Trap.Instance trap) {
        if (opportunist.getCharacter().human()) {
            Global.gui().message("You leap out of cover and catch " + target.getCharacter().getName() + " by surprise.");
        } else if (target.getCharacter().human()) {
            Global.gui().message("Before you have a chance to recover, " + opportunist.getCharacter().getName() + " pounces on you.");
        }

        var startingPosition = trap.capitalize(opportunist, target);
        startFight(opportunist, target);
        startingPosition.ifPresent(sp -> fight.setStanceRaw(sp));

        if (fight.getP1Character().human() || fight.getP2Character().human()) {
            Global.gui().watchCombat(fight);
        }
    }

    public String smokeMessage(Character c) {
        return String.format("%s a smoke bomb and %s.", 
                        Global.capitalizeFirstLetter(c.subjectAction("drop", "drops"))
                        , c.action("disappear", "disappears"));
    }

    public final class IntrusionOption {
        private Participant intruder;
        public Participant target;

        private IntrusionOption(Participant intruder, Participant target) {
            this.intruder = intruder;
            this.target = target;
        }

        public Character getTargetCharacter() {
            return target.getCharacter();
        }

        public void callback() {
            intrude(intruder, target);
        }
    }

    public Set<IntrusionOption> getCombatIntrusionOptions(Participant intruder) {
        if (fight == null ||
                intruder.getCharacter().equals(p1.getCharacter()) ||
                intruder.getCharacter().equals(p2.getCharacter())) {
            return Set.of();
        }
        return Set.of(new IntrusionOption(intruder, p1),
                new IntrusionOption(intruder, p2));
    }

    public void watch() {
        Global.gui().watchCombat(fight);
        fight.go();
    }
}
