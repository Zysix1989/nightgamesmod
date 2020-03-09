package nightgames.match.ftc;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.State;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.match.defaults.DefaultEncounter;
import nightgames.stance.Mount;
import nightgames.stance.Pin;
import nightgames.status.Bound;
import nightgames.status.Flatfooted;

public class FTCEncounter extends DefaultEncounter {

    public FTCEncounter(Participant first, Participant second, Area location) {
        super(first, second, location);
    }

    @Override
    public boolean spotCheck() {
        if (!(p1.canStartCombat(p2) && p2.canStartCombat(p1)))
            return super.spotCheck();
        if (p1.state == State.inTree) {
            treeAmbush(p1, p2);
        } else if (p2.state == State.inTree) {
            treeAmbush(p2, p1);
        } else if (p1.state == State.inBushes) {
            bushAmbush(p1, p2);
        } else if (p2.state == State.inBushes) {
            bushAmbush(p2, p1);
        } else if (p1.state == State.inPass) {
            passAmbush(p1, p2);
        } else if (p2.state == State.inPass) {
            passAmbush(p2, p1);
        } else {
            return super.spotCheck();
        }
        return true;
    }

    private void treeAmbush(Participant attacker, Participant victim) {
        startFightTimer();
        victim.getCharacter().addNonCombat(new Status(new Flatfooted(victim.getCharacter(), 3)));
        if (attacker.getCharacter().has(Item.Handcuffs))
            victim.getCharacter().addNonCombat(new Status(new Bound(victim.getCharacter(), 75, "handcuffs")));
        else
            victim.getCharacter().addNonCombat(new Status(new Bound(victim.getCharacter(), 50, "zip-tie")));
        startFight(attacker, victim);
        fight.setStance(new Pin(attacker.getCharacter(), victim.getCharacter()));

        var victimMessage = "As you walk down the trail, you hear a slight rustling in the"
                + " leaf canopy above you. You look up, but all you see is a flash of ";
        if (attacker.getCharacter().mostlyNude()) {
            victimMessage += "nude flesh";
        } else {
            victimMessage += "clothes";
        }
        victimMessage += " before you are pushed to the ground. Before you have a chance to process"
                + " what's going on, your hands are tied behind your back and your"
                + " attacker, who now reveals {self:reflective} to be {self:name},"
                + " whispers in your ear \"Happy to see me, {other:name}?\"";
        victim.getCharacter().message(victimMessage);

        var attackerMessage = "Your patience finally pays off as {other:name} approaches the"
                + " tree you are hiding in. You wait until the perfect moment,"
                + " when {other:pronoun} is right beneath you, before you jump"
                + " down. You land right on {other:possessive} shoulders, pushing"
                + " {other:direct-object} firmly to the soft soil. Pulling our a ";
        if (attacker.getCharacter().has(Item.Handcuffs)) {
            attackerMessage += "pair of handcuffs, ";
        } else {
            attackerMessage += "zip-tie, ";
        }
        attackerMessage += " you bind {other:possessive} hands together. There are worse" + " ways to start a match.";
        attacker.getCharacter().message(attackerMessage);
    }

    private void bushAmbush(Participant attacker, Participant victim) {
        startFightTimer();
        victim.getCharacter().addNonCombat(new Status(new Flatfooted(victim.getCharacter(), 3)));
        if (attacker.getCharacter().has(Item.Handcuffs))
            victim.getCharacter().addNonCombat(new Status(new Bound(victim.getCharacter(), 75, "handcuffs")));
        else
            victim.getCharacter().addNonCombat(new Status(new Bound(victim.getCharacter(), 50, "zip-tie")));
        if (p1.getCharacter().human() || p2.getCharacter().human()) {
            startFight(attacker, victim);
            fight.setStance(new Mount(attacker.getCharacter(), victim.getCharacter()));
            String message = "";
            if (victim.getCharacter().human()) {
                message += "You are having a little difficulty wading through the dense"
                                + " bushes. Your foot hits something, causing you to trip and fall flat"
                                + " on your face. A weight settles on your back and your arms are"
                                + " pulled behind your back and tied together with something. You"
                                + " are rolled over, and {self:name} comes into view as {self:pronoun}"
                                + " settles down on your belly. \"Hi, {other:name}. Surprise!\"";
            } else {
                message += "Hiding in the bushes, your vision is somewhat obscured. This is"
                                + " not a big problem, though, as the rustling leaves alert you to"
                                + " passing prey. You inch closer to where you suspect they are headed,"
                                + " and slowly {other:name} comes into view. Just as {other:pronoun}"
                                + " passes you, you stick out a leg and trip {other:direct-object}."
                                + " With a satisfying crunch of the leaves, {other:pronoun} falls."
                                + " Immediately you jump on {other:possessive} back and tie "
                                + "{other:possessive} hands together.";
            }
            Global.gui().message(Global.format(message, attacker.getCharacter(), victim.getCharacter()));
        } else {
            Global.gui().refresh();
            startFight(attacker, victim);
            fight.setStance(new Pin(attacker.getCharacter(), victim.getCharacter()));
        }
    }

    private void passAmbush(Participant attacker, Participant victim) {
        int attackerScore = 30 + attacker.getCharacter().get(Attribute.Speed) * 10 + attacker.getCharacter().get(Attribute.Perception) * 5
                        + Global.random(30);
        int victimScore = victim.getCharacter().get(Attribute.Speed) * 10 + victim.getCharacter().get(Attribute.Perception) * 5 + Global.random(30);
        String message = "";
        if (attackerScore > victimScore) {
            if (attacker.getCharacter().human()) {
                message += "You wait in a small alcove, waiting for someone to pass you."
                                + " Eventually, you hear footsteps approaching and you get ready."
                                + " As soon as {other:name} comes into view, you jump out and push"
                                + " {other:direct-object} against the opposite wall. The impact seems to"
                                + " daze {other:direct-object}, giving you an edge in the ensuing fight.";
            } else if (victim.getCharacter().human()) {
                message += "Of course you know that walking through a narrow pass is a"
                                + " strategic risk, but you do so anyway. Suddenly, {self:name}"
                                + " flies out of an alcove, pushing you against the wall on the"
                                + " other side. The impact knocks the wind out of you, putting you"
                                + " at a disadvantage.";
            }
            startFight(attacker, victim);
            victim.getCharacter().addNonCombat(new Status(new Flatfooted(victim.getCharacter(), 3)));
        } else {
            if (attacker.getCharacter().human()) {
                message += "While you are hiding behind a rock, waiting for someone to"
                                + " walk around the corner up ahead, you hear a soft cruch behind"
                                + " you. You turn around, but not fast enough. {other:name} is"
                                + " already on you, and has grabbed your shoulders. You are unable"
                                + " to prevent {other:direct-object} from throwing you to the ground,"
                                + " and {other:pronoun} saunters over. \"Were you waiting for me,"
                                + " {self:name}? Well, here I am.\"";
            } else if (victim.getCharacter().human()) {
                message += "You are walking through the pass when you see {self:name}"
                                + " crouched behind a rock. Since {self:pronoun} is very focused"
                                + " in looking the other way, {self:pronoun} does not see you coming."
                                + " Not one to look a gift horse in the mouth, you sneak up behind"
                                + " {self:direct-object} and grab {self:direct-object} in a bear hug."
                                + " Then, you throw {self:direct-object} to the side, causing"
                                + " {self:direct-object} to fall to the ground.";
            }
            startFight(attacker, victim);
            attacker.getCharacter().addNonCombat(new Status(new Flatfooted(attacker.getCharacter(), 3)));
        }
        if (attacker.getCharacter().human() || victim.getCharacter().human()) {
            Global.gui().message(Global.format(message, attacker.getCharacter(), victim.getCharacter()));
        } else {

        }
    }
}
