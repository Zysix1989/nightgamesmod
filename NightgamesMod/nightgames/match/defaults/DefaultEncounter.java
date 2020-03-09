package nightgames.match.defaults;

import nightgames.areas.Area;
import nightgames.areas.AreaIdentity;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.status.*;
import nightgames.trap.Spiderweb;
import nightgames.trap.Trap;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Optional;

public class DefaultEncounter {
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

    public DefaultEncounter(Participant first, Participant second, Area location) {
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

    private static void checkEnthrall(Participant p1, Participant p2) {
        Status enthrall = p1.getCharacter().getStatus(Stsflag.enthralled);
        if (enthrall != null) {
            if (((Enthralled) enthrall).master != p2.getCharacter()) {
                p1.getCharacter().removelist.add(enthrall);
                p1.getCharacter().addNonCombat(new nightgames.match.Status(new Flatfooted(p1.getCharacter(), 2)));
                p1.getCharacter().addNonCombat(new nightgames.match.Status(new Hypersensitive(p1.getCharacter())));
                p1.getCharacter().message("At " + p2.getCharacter().getName() + "'s interruption, you break free from the"
                        + " succubus' hold on your mind. However, the shock all but"
                        + " short-circuits your brain; you "
                        + " collapse to the floor, feeling helpless and"
                        + " strangely oversensitive");
                p2.getCharacter().message(String.format(
                        "%s doesn't appear to notice you at first, but when you wave your hand close to %s face %s "
                                + "eyes open wide and %s immediately drops to the floor. Although the display leaves you "
                                + "somewhat worried about %s health, %s is still in a very vulnerable position and you never "
                                + "were one to let an opportunity pass you by.",
                        p1.getCharacter().getName(), p1.getCharacter().possessiveAdjective(),
                        p1.getCharacter().possessiveAdjective(), p1.getCharacter().pronoun(),
                        p1.getCharacter().possessiveAdjective(), p1.getCharacter().pronoun()));
            }
        }
    }

    /**
     * Checks for and runs any scenarios that arise from two Characters encountering each other. 
     * Returns true if something has come up that prevents them from being presented with the usual
     * campus Actions.
     */
    public boolean spotCheck() {
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
        if (hidden.state == State.hidden) {
            dc += (hidden.getCharacter().get(Attribute.Cunning) * 2 / 3) + 20;
        }
        if (hidden.getCharacter().has(Trait.Sneaky)) {
            dc += 20;
        }
        dc -= dc * 5 / Math.max(1, spotter.getCharacter().get(Attribute.Perception));
        return spotter.getCharacter().check(Attribute.Cunning, dc);
    }

    private void showerScene(Participant attacker, Participant victim) {
        attacker.getCharacter().showerScene(
                victim,
                () -> showerAmbush(attacker, victim),
                () -> steal(attacker.getCharacter(), victim.getCharacter()),
                () -> aphrodisiactrick(attacker.getCharacter(), victim.getCharacter()),
                () -> {});
    }

    private void eligibleSpotCheck() {
        if (p1.state == State.shower) {
            showerScene(p2, p1);
            return;
        } else if (p2.state == State.shower) {
            showerScene(p1, p2);
            return;
        } else if (p1.state == State.webbed) {
            spider(p2, p1);
            return;
        } else if (p2.state == State.webbed) {
            spider(p1, p2);
            return;
        } else if (p1.state == State.crafting || p1.state == State.searching) {
            p2.getCharacter().spy(p1, () -> ambush(p2, p1), () -> location.endEncounter());
            return;
        } else if (p2.state == State.crafting || p2.state == State.searching) {
            p1.getCharacter().spy(p2, () -> ambush(p1, p2), () -> location.endEncounter());
            return;
        } else if (p1.state == State.masturbating) {
            caught(p2, p1);
            return;
        } else if (p2.state == State.masturbating) {
            caught(p1, p2);
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
    
    private void ineligibleSpotCheck() {
        // We can skip a lot of flavor lines if there aren't any humans around
        if (p1.getCharacter().human() || p2.getCharacter().human()) {
            if (p1.state == State.masturbating) {
                p1.getCharacter().message(String.format(
                        "%s catches you masturbating, but fortunately %s's not yet allowed to attack you, so %s just "
                                + "watches you pleasure yourself with an amused grin.",
                        p2.getCharacter().getName(), p2.getCharacter().pronoun(), p2.getCharacter().pronoun()));
                p2.getCharacter().message(String.format(
                        "You stumble onto %s with %s hand between %s legs, masturbating. Since you just fought you still can't touch %s, so "
                                + "you just watch the show until %s orgasms.",
                        p1.getCharacter().getName(), p1.getCharacter().possessiveAdjective(), p1.getCharacter().possessiveAdjective(), p1.getCharacter().objectPronoun(),
                        p1.getCharacter().pronoun()));
            } else if (p2.state == State.masturbating) {
                p2.getCharacter().message(String.format(
                        "%s catches you masturbating, but fortunately %s's not yet allowed to attack you, so %s just "
                                + "watches you pleasure yourself with an amused grin.",
                        p1.getCharacter().getName(), p1.getCharacter().pronoun(), p1.getCharacter().pronoun()));
                p1.getCharacter().message(String.format(
                        "You stumble onto %s with %s hand between %s legs, masturbating. Since you just fought you still can't touch %s, so "
                                + "you just watch the show until %s orgasms.",
                        p2.getCharacter().getName(), p2.getCharacter().possessiveAdjective(), p2.getCharacter().possessiveAdjective(), p2.getCharacter().objectPronoun(),
                        p2.getCharacter().pronoun()));
            } else {
                if (p1.canStartCombat(p2)) {
                    p1.getCharacter().message("You encounter " + p2.getCharacter().getName() + ", but you still haven't recovered from your last fight.");
                    p2.getCharacter().message(String.format(
                            "You find %s still naked from your last encounter, but %s's not fair game again until %s replaces %s clothes.",
                            p1.getCharacter().getName(), p1.getCharacter().pronoun(), p1.getCharacter().pronoun(), p1.getCharacter().possessiveAdjective()));
                } else {
                    p1.getCharacter().message(String.format(
                            "You find %s still naked from your last encounter, but %s's not fair game again until %s replaces %s clothes.",
                            p2.getCharacter().getName(), p2.getCharacter().pronoun(), p2.getCharacter().pronoun(), p2.getCharacter().possessiveAdjective()));

                    p2.getCharacter().message("You encounter " + p1.getCharacter().getName() + ", but you still haven't recovered from your last fight.");
                }
            }
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
            checkin++;
        } else {
            p2ff = fight;
            p2Guaranteed = guaranteed;
            checkin++;
        }
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

    protected void startFight(Participant p1, Participant p2) {
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

    private static JtwigTemplate SHOWER_TARGET_MESSAGE = JtwigTemplate.inlineTemplate("You aren't in the shower long " +
            "before you realize you're not alone. Before you can turn around, " +
            "{% if (target.hasDick()) %}" +
            "a soft hand grabs your exposed penis. " +
            "{% else if (target.hasBreasts()) %}" +
            "hands reach around to cup your breasts. " +
            "{% else %}" +
            "you feel someone grab a handful of your ass. " +
            "{{ attacker.subject().properNoun() }} has the drop on you.");

    private static JtwigTemplate SHOWER_ATTACKER_MESSAGE = JtwigTemplate.inlineTemplate(
            "You stealthily walk up behind {{ target.object().properNoun() }}, enjoying the view of " +
                    "{{ target.possessiveAdjective() }} wet, naked body. When you pinch " +
                    "{{ target.possessiveAdjective() }} smooth butt, {{ target.subject().pronoun() }} jumps and lets " +
                    "out a surprised yelp. Before {{ target.subject().pronoun() }} can recover from " +
                    "{{ target.possessiveAdjective() }} surprise, you pounce!");

    private static JtwigTemplate POOL_TARGET_MESSAGE = JtwigTemplate.inlineTemplate(
            "The relaxing water causes you to lower your guard a bit, so you don't " +
                    "notice {{ attacker.object().properNoun() }} until {{ attacker.subject().pronoun() }}'s standing " +
                    "over you. There's no chance to escape; you'll have to face {{ attacker.object().pronoun() }} nude.");

    private static JtwigTemplate POOL_ATTACKER_MESSAGE = JtwigTemplate.inlineTemplate(
            "You creep up to the jacuzzi where {{ target.subject().properNoun() }} is soaking comfortably. " +
                    "As you get close, you notice that {{ target.possessiveAdjective() }} eyes are " +
                    "closed and {{ target.subject().pronoun() }} may well be sleeping. You crouch by the " +
                    "edge of the jacuzzi for a few seconds and just admire {{ target.possessiveAdjective() }} " +
                    "nude body " +
                    "{% if (targetCharacter.hasBreasts()) %} " +
                    "with {{ target.possessiveAdjective() }} breasts just above the surface. " +
                    "{% else %}" +
                    "for a few seconds. " +
                    "{% endif %}" +
                    "You lean down and give {{ target.object().pronoun() }} a light kiss on the forehead " +
                    "to wake {{ target.object().pronoun() }} up. {{ target.subject().properNoun() }} opens her " +
                    "eyes and swears under {{ target.possessiveAdjective() }} breath when " +
                    "{{ target.subject().pronoun() }} sees you. {{ target.subject().pronoun() }} scrambles " +
                    "out of the tub, but you easily catch {{ target.object().pronoun() }} before " +
                    "{{ target.subject().pronoun() }} can get away.");

    public void showerAmbush(Participant attacker, Participant target) {
        var targetModel = JtwigModel.newModel()
                .with("attacker", attacker.getCharacter().getGrammar())
                .with("target", target.getLocation());
        var attackerModel = JtwigModel.newModel()
                .with("target", target.getCharacter().getGrammar());
        if (location.id() == AreaIdentity.shower) {
            target.getCharacter().message(SHOWER_TARGET_MESSAGE.render(targetModel));
            attacker.getCharacter().message(SHOWER_ATTACKER_MESSAGE.render(attackerModel));
        } else if (location.id() == AreaIdentity.pool) {
            target.getCharacter().message(POOL_TARGET_MESSAGE.render(targetModel));
            attacker.getCharacter().message(POOL_ATTACKER_MESSAGE.render(attackerModel));
        }
        
        startFight(p1, p2);
        p2.getCharacter().undress(fight);
        p1.getCharacter().emote(Emotion.dominant, 50);
        p2.getCharacter().emote(Emotion.nervous, 50);
        target.getCharacter().add(fight, new Flatfooted(target.getCharacter(), 4));
    }

    protected void caught(Participant attacker, Participant target) {
        attacker.getCharacter().gainXP(attacker.getCharacter().getVictoryXP(target.getCharacter()));
        target.getCharacter().gainXP(target.getCharacter().getDefeatXP(attacker.getCharacter()));
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
        target.getCharacter().nudify();
        target.getCharacter().defeated(attacker.getCharacter());
        target.getCharacter().getArousal().renew();
        attacker.getCharacter().tempt(20);
        Global.getMatch().score(attacker.getCharacter(),  1);
        attacker.state = State.ready;
        target.state = State.ready;
        location.endEncounter();
    }

    protected void spider(Participant attacker, Participant target) {
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

    public Participant getFirstParticipant() {
        return p1;
    }

    public Participant getSecondParticipant() {
        return p2;
    }

    public void steal(Character thief, Character target) {
        if (thief.human()) {
            Global.gui()
                  .message("You quietly swipe " + target.getName()
                                  + "'s clothes while she's occupied. It's a little underhanded, but you can still turn them in for cash just as if you defeated her.");
        }
        thief.gain(target.getTrophy());
        target.nudify();
        Global.getMatch().findParticipant(target).state = State.lostclothes;
        location.endEncounter();
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

    public boolean checkIntrude(Character c) {
        return fight != null && !c.equals(p1.getCharacter()) && !c.equals(p2.getCharacter());
    }

    public void watch() {
        Global.gui().watchCombat(fight);
        fight.go();
    }

    /** Causes the attacker to be defeated, sending the given message after XP has been gained
     * but before the rest of the defeat logic.
     * 
     * Note: this code seems like it might crop up elsewhere; it has already done so once
     * in the Spiderweb class. If any more do it's worth extracting this to somewhere
     * more general, but at the moment I'm not sure where that might be.
     */
    private void encounterDefeat(Character attacker, Character target, String message) {
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        
        if (message != null) {
            Global.gui().message(message);
        }
        
        if (!target.mostlyNude()) {
            attacker.gain(target.getTrophy());
        }
        target.nudify();
        target.defeated(attacker);
        target.getArousal().renew();
        attacker.tempt(20);
        Global.getMatch().score(attacker,  1);
        Global.getMatch().findParticipant(attacker).state = State.ready;
        Global.getMatch().findParticipant(target).state = State.ready;
        location.endEncounter();
    }

    public void aphrodisiactrick(Character attacker, Character target) {
        attacker.consume(Item.Aphrodisiac, 1);
        encounterDefeat(attacker, target, getAphrodisiacTrickMessage(attacker, target));
    }
    
    /** Returns null if no message is to be sent */
    private String getAphrodisiacTrickMessage(Character attacker, Character target) {
        if (location.id() == AreaIdentity.shower) {
            return getAphrodisiacTrickShowerMessage(attacker, target);
        } else if (location.id() == AreaIdentity.pool) {
            return getAphrodisiacTrickPoolMessage(attacker, target);
        }
        return null;
    }
    
    private String getAphrodisiacTrickShowerMessage(Character attacker, Character target) {
        if (attacker.human()) {
            if (target.hasPussy() && !target.hasDick()) {
                return Global.format("You empty the bottle of aphrodisiac onto the shower floor, letting the heat from the shower turn it to steam. You watch "
                            + "{other:name} and wait for a reaction. Just when you start to worry that it was all washed down the drain, you see {other:possessive} "
                            + "hand slip between {other:possessive} legs. {other:POSSESSIVE} fingers go to work pleasuring {other:reflexive} and soon {other:pronoun}'s "
                            + "utterly engrossed in {other:possessive} masturbation, allowing you to safely get closer without being noticed. {other:NAME}'s "
                            + "completely unreserved in {other:possessive} assumption of solitude and you feel a voyeuristic thrill at the show. You can't "
                            + "remain an observer, however. For this to count as a victory you need to be in physical contact with {other:direct-object} "
                            + "when {other:pronoun} orgasms. When you judge that {other:pronoun}'s in the home stretch, you embrace {other:direct-object} from "
                            + "behind and kiss {other:direct-object} neck. {other:NAME} freezes in surprise and you move your hand between {other:possessive} "
                            + "legs to replace {other:possessive} own. {other:POSSESSIVE} pussy is hot, wet, and trembling with need. You stick two fingers into "
                            + "{other:direct-object} and rub {other:possessive} clit with your thumb. {other:NAME} climaxes almost immediately. You give "
                            + "{other:direct-object} a kiss on the cheek and leave while {other:pronoun}'s still too dazed to realize what happened. You're "
                            + "feeling pretty horny, but after a show like that it's hardly surprising.\n",
                            attacker, target);
            } else if (target.hasDick()) {
                return Global.format("You empty the bottle of aphrodisiac onto the shower floor, letting the heat from the shower turn it to steam. You watch "
                            + "{other:name} and wait for a reaction. Just when you start to worry that it was all washed down the drain, you see {other:possessive} "
                            + "hand slip down to encircle {other:possessive} cock. {other:POSSESSIVE} hand builds a steady rhythm pleasuring {other:reflexive} "
                            + "and soon {other:pronoun}'s utterly engrossed in {other:possessive} masturbation, allowing you to safely get closer without being "
                            + "noticed. {other:NAME}'s completely unreserved in {other:possessive} assumption of solitude and you feel a voyeuristic thrill at "
                            + "the show. You can't remain an observer, however. For this to count as a victory you need to be in physical contact with "
                            + "{other:direct-object} when {other:pronoun} orgasms. When you judge that {other:pronoun}'s in the home stretch, you embrace "
                            + "{other:direct-object} from behind and kiss {other:possessive} neck. {other:NAME} freezes in surprise and you move your hand "
                            + "between {other:possessive} legs to replace {other:possessive} own. {other:POSSESSIVE} dick is hard, slick, and trembling with "
                            + "need. You begin stroking it rapidly and {other:pronoun} climaxes almost immediately. You give {other:direct-object} a kiss on "
                            + "the cheek and leave while {other:pronoun}'s still too dazed to realize what happened. You're feeling pretty horny, but after a show "
                            + "like that it's hardly surprising.\n",
                            attacker, target);
            } else {
                return Global.format("You empty the bottle of aphrodisiac onto the shower floor, letting the heat from the shower turn it to steam. You watch "
                            + "{other:name} and wait for a reaction. Just when you start to worry that it was all washed down the drain, you see {other:possessive} "
                            + "hand reach behind {other:reflexive}. {other:POSSESSIVE} fingers go to work pleasuring {other:possessive} ass and soon "
                            + "{other:pronoun}'s utterly engrossed in {other:possessive} masturbation, allowing you to safely get closer without being noticed. "
                            + "{other:NAME}'s completely unreserved in {other:possessive} assumption of solitude and you feel a voyeuristic thrill at the show. "
                            + "You can't remain an observer, however. For this to count as a victory you need to be in physical contact with {other:direct-object} "
                            + "when {other:pronoun} orgasms. When you judge that {other:pronoun}'s in the home stretch, you embrace {other:direct-object} from "
                            + "behind and kiss {other:direct-object} neck. {other:NAME} freezes in surprise and you move your hand between {other:possessive} "
                            + "cheeks to replace {other:possessive} own. {other:POSSESSIVE} asshole is hot, tight, and pulsing with need. You stick two fingers into "
                            + "{other:direct-object}, curling and probing. {other:NAME} climaxes almost immediately. You give {other:direct-object} a kiss "
                            + "and leave while {other:pronoun}'s still too dazed to realize what happened. You're feeling pretty horny, but after a show like that "
                            + "it's hardly surprising.\n",
                            attacker, target);
            }
        } else if (target.human()) {
            if (target.hasPussy() && !target.hasDick()) {
                return Global.format("The hot shower takes your fatigue away, but you can't seem to calm down. Your nipples are almost painfully hard. You need to deal with "
                            + "this while you have the chance. You rub your labia rapidly, hoping to finish before someone stumbles onto you. Right before you cum, you "
                            + "are suddenly grabbed from behind and spun around. {other:NAME} has caught you at your most vulnerable and, based on {other:possessive} "
                            + "expression, may have been waiting for this moment. {other:PRONOUN} kisses you and pushes two fingers into your swollen pussy. In just a "
                            + "few strokes you cum so hard it's almost painful.\n",
                                target, attacker);
            } else if (target.hasDick()) {
                return Global.format("The hot shower takes your fatigue away, but you can't seem to calm down. Your cock is almost painfully hard. You need to deal with "
                            + "this while you have the chance. You jerk off quickly, hoping to finish before someone stumbles onto you. Right before you cum, you are "
                            + "suddenly grabbed from behind and spun around. {other:NAME} has caught you at your most vulnerable and, based on {other:possessive} "
                            + "expression, may have been waiting for this moment. {other:PRONOUN} kisses you and firmly grasps your twitching dick. In just a few "
                            + "strokes you cum so hard it's almost painful.\n",
                            target, attacker);
            } else {
                return Global.format("The hot shower takes your fatigue away, but you can't seem to calm down. Your nipples are almost painfully hard. You need to deal with "
                            + "this while you have the chance. You rub your asshole rapidly, hoping to finish before someone stumbles onto you. Right before you cum, you "
                            + "are suddenly grabbed from behind and spun around. {other:NAME} has caught you at your most vulnerable and, based on {other:possessive} "
                            + "expression, may have been waiting for this moment. {other:PRONOUN} kisses you and pushes two fingers into your behind. In just a "
                            + "few strokes you cum so hard it's almost painful.\n", 
                            target, attacker);
            }
        }
        return null;
    }
    
    private String getAphrodisiacTrickPoolMessage(Character attacker, Character target) {
        if (attacker.human()) {
            if (target.hasPussy() && !target.hasDick()) {
                return Global.format("You sneak up to the jacuzzi and empty the aphrodisiac into the water without {other:name} noticing. You slip away and find a hiding "
                            + "spot. In a couple minutes, you notice {other:direct-object} stir. {other:PRONOUN} glances around, but fails to see you and closes "
                            + "{other:possessive} eyes and relaxes again. There's something different now, though, and {other:possessive} soft moan confirms it. "
                            + "You grin and quietly approach for a second time. You can see {other:possessive} hand moving under the surface of the water as "
                            + "{other:pronoun} enjoys {other:reflexive} tremendously. {other:POSSESSIVE} moans rise in volume and frequency. Now's the right moment. "
                            + "You lean down and kiss {other:direct-object} on the lips. {other:POSSESSIVE} masturbation stops immediately, but you reach underwater "
                            + "and finger {other:direct-object} to orgasm. When {other:name} recovers, {other:pronoun} glares at you for your unsportsmanlike trick, "
                            + "but {other:pronoun} can't manage to get really mad in the afterglow of {other:possessive} climax. You're pretty turned on by the "
                            + "encounter, but you can chalk this up as a win.\n",
                            attacker, target);
            } else if (target.hasDick()) {
                return Global.format("You sneak up to the jacuzzi and empty the aphrodisiac into the water without {other:name} noticing. You slip away and find a hiding "
                            + "spot. In a couple minutes, you notice {other:direct-object} stir. {other:PRONOUN} glances around, but fails to see you and closes "
                            + "{other:possessive} eyes and relaxes again. There's something different now, though, and {other:possessive} soft moan confirms it. "
                            + "You grin and quietly approach for a second time. You can see {other:possessive} hand moving under the surface of the water as "
                            + "{other:pronoun} enjoys {other:reflexive} tremendously. {other:POSSESSIVE} moans rise in volume and frequency. Now's the right moment. "
                            + "You lean down and kiss {other:direct-object} on the lips. {other:POSSESSIVE} masturbation stops immediately, but you reach underwater "
                            + "and stroke {other:direct-object} to orgasm. When {other:name} recovers, {other:pronoun} glares at you for your unsportsmanlike trick, "
                            + "but {other:pronoun} can't manage to get really mad in the afterglow of {other:possessive} climax. You're pretty turned on by the "
                            + "encounter, but you can chalk this up as a win.\n",
                            attacker, target);
            } else {
                return Global.format("You sneak up to the jacuzzi and empty the aphrodisiac into the water without {other:name} noticing. You slip away and find a hiding "
                            + "spot. In a couple minutes, you notice {other:direct-object} stir. {other:PRONOUN} glances around, but fails to see you. {other:name} "
                            + "shifts {other:possessive} legs to the side and a hand drifts behind {other:possessive} back.  You can barely make out muscles moving "
                            + "in {other:possessive} forearm, and a soft moan confirms your suspicions. You grin and quietly approach for a second time. You can see "
                            + "{other:possessive} hand moving under the surface of the water as {other:pronoun} enjoys {other:reflexive} tremendously. {other:POSSESSIVE} "
                            + "moans rise in volume and frequency. Now's the right moment. You lean down and kiss {other:direct-object} on the lips. {other:POSSESSIVE} "
                            + "masturbation stops immediately, but you pull {other:direct-object} half out of the water and face-down onto the tile.  You plunge your "
                            + "fingers into {other:name}'s upturned bottom and finger {other:direct-object} to a shuddering orgasm. When {other:name} recovers "
                            + "{other:pronoun} glares at you for your unsportsmanlike trick, but {other:pronoun} can't manage to get really mad in the afterglow of "
                            + "{other:possessive} climax. You're pretty turned on by the encounter, but you can chalk this up as a win.\n",
                            attacker, target);
            }
        } else if (target.human()) {
            if (target.hasPussy() && !target.hasDick()) {
                return Global.format("As you relax in the jacuzzi, you start to feel extremely horny. Your hand is between your legs before you're even aware of it. "
                                + "You rub yourself underwater and you're just about ready to cum when you hear nearby footsteps. Shit, you'd almost completely forgotten "
                                + "you were in the middle of a match! The footsteps are {other:name}'s, who sits down at the edge of the jacuzzi while smiling confidently. "
                                + "You look for a way to escape, but it's hopeless. You were so close to finishing you desperately need to cum. {other:NAME} seems to be "
                                + "thinking the same thing, as {other:pronoun} dips {other:possessive} bare feet into the water and grinds {other:possessive} heel into "
                                + "your vulva. You clutch {other:possessive} leg and buck helplessly against the back of {other:possessive} foot, cumming in seconds.\n",
                                target, attacker);
            } else if (target.hasDick()) {
                return Global.format("As you relax in the jacuzzi, you start to feel extremely horny. Your cock is in your hand before you're even aware of it. You stroke "
                            + "yourself off underwater and you're just about ready to cum when you hear nearby footsteps. Shit, you'd almost completely forgotten you "
                            + "were in the middle of a match! The footsteps are {other:name}'s, who sits down at the edge of the jacuzzi while smiling confidently. You "
                            + "look for a way to escape, but it's hopeless. You were so close to finishing you desperately need to cum. {other:NAME} seems to be thinking "
                            + "the same thing, as {other:pronoun} dips {other:possessive} bare feet into the water and grasps your penis between them. {other:PRONOUN} "
                            + "pumps you with {other:possessive} feet and you shoot your load into the water in seconds.\n",
                            target, attacker);
            } else {
                return Global.format("As you relax in the jacuzzi, you start to feel extremely horny. Your hand is between your cheeks before you're even aware of it. "
                                + "You play with your rear underwater and you're just about ready to cum when you hear nearby footsteps. Shit, you'd almost completely forgotten "
                                + "you were in the middle of a match! The footsteps are {other:name}'s, who sits down at the edge of the jacuzzi while smiling confidently. "
                                + "You look for a way to escape, but it's hopeless. You were so close to finishing you desperately need to cum. {other:NAME} seems to be "
                                + "thinking the same thing, as {other:pronoun} pulls you roughly out of the jacuzzi and plunges two fingers into your upturned ass. You "
                                + "writhe helplessly on the smooth tile under {other:name}'s ministrations, cumming in seconds.\n",
                                target, attacker);
            }
        }
        return null;
    }
}
