package nightgames.match.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.stance.Pin;
import nightgames.status.Bound;
import nightgames.status.Flatfooted;

import java.util.Optional;

public class TreeAmbush extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " climb up a tree.";
        }
    }

    public static class State implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().message("You are hiding in a tree, waiting to drop down on an unwitting foe.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            return Optional.of(() -> {
                other.getCharacter().addNonCombat(new Status(new Flatfooted(other.getCharacter(), 3)));
                if (p.getCharacter().has(Item.Handcuffs))
                    other.getCharacter().addNonCombat(new Status(new Bound(other.getCharacter(), 75, "handcuffs")));
                else
                    other.getCharacter().addNonCombat(new Status(new Bound(other.getCharacter(), 50, "zip-tie")));
                var fight = encounter.startFight(p, other);
                fight.setStance(new Pin(p.getCharacter(), other.getCharacter()));

                var victimMessage = "As you walk down the trail, you hear a slight rustling in the"
                        + " leaf canopy above you. You look up, but all you see is a flash of ";
                if (p.getCharacter().mostlyNude()) {
                    victimMessage += "nude flesh";
                } else {
                    victimMessage += "clothes";
                }
                victimMessage += " before you are pushed to the ground. Before you have a chance to process"
                        + " what's going on, your hands are tied behind your back and your"
                        + " attacker, who now reveals {self:reflective} to be {self:name},"
                        + " whispers in your ear \"Happy to see me, {other:name}?\"";
                other.getCharacter().message(Global.format(victimMessage, p.getCharacter(), other.getCharacter()));

                var attackerMessage = "Your patience finally pays off as {other:name} approaches the"
                        + " tree you are hiding in. You wait until the perfect moment,"
                        + " when {other:pronoun} is right beneath you, before you jump"
                        + " down. You land right on {other:possessive} shoulders, pushing"
                        + " {other:direct-object} firmly to the soft soil. Pulling our a ";
                if (p.getCharacter().has(Item.Handcuffs)) {
                    attackerMessage += "pair of handcuffs, ";
                } else {
                    attackerMessage += "zip-tie, ";
                }
                attackerMessage += " you bind {other:possessive} hands together. There are worse" + " ways to start a match.";
                p.getCharacter().message(Global.format(attackerMessage, p.getCharacter(), other.getCharacter()));
            });
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("spot check for %s should have already been replaced",
                    p.getCharacter().getTrueName()));
        }

    }

    public TreeAmbush() {
        super("Climb a Tree");
    }

    @Override
    public boolean usable(Participant user) {
        return (user.getCharacter().get(Attribute.Power) >= 20 || user.getCharacter().get(Attribute.Animism) >= 10)
                        && !(user.state instanceof State)
                        && !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (user.getCharacter().get(Attribute.Animism) >= 10) {
            user.getCharacter().message("Following your instincts, you clamber up a tree" + " to await an unwitting passerby.");
        } else {
            user.getCharacter().message("You climb up a tree that has a branch hanging over"
                    + " the trail. It's hidden in the leaves, so you should be"
                    + " able to surprise someone passing underneath.");
        }
        user.state = new State();
        return new Aftermath();
    }

}
