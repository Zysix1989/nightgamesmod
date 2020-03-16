package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.stance.Mount;
import nightgames.status.Bound;
import nightgames.status.Flatfooted;

import java.util.Optional;

public class BushAmbush extends Action {
    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            if (user.getCharacter().get(Attribute.Animism) >= 10) {
                user.getCharacter().message("You crouch down in some dense bushes, ready" + " to pounce on passing prey.");
            } else {
                user.getCharacter().message("You spot some particularly dense bushes, and figure"
                        + " they'll make for a decent hiding place. You lie down in them,"
                        + " and wait for someone to walk past.");
            }
            user.state = new State();
            messageOthersInLocation(user.getCharacter().getGrammar().subject().defaultNoun() +
                    " dive into some bushes.");
        }
    }

    public static class State implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return true;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().message("You are hiding in dense bushes, waiting for someone to pass by.");
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
                fight.setStance(new Mount(p.getCharacter(), other.getCharacter()));

                var victimMessage = "You are having a little difficulty wading through the dense"
                        + " bushes. Your foot hits something, causing you to trip and fall flat"
                        + " on your face. A weight settles on your back and your arms are"
                        + " pulled behind your back and tied together with something. You"
                        + " are rolled over, and {self:name} comes into view as {self:pronoun}"
                        + " settles down on your belly. \"Hi, {other:name}. Surprise!\"";
                other.getCharacter().message(Global.format(victimMessage, p.getCharacter(), other.getCharacter()));


                var attackerMessage = "Hiding in the bushes, your vision is somewhat obscured. This is"
                        + " not a big problem, though, as the rustling leaves alert you to"
                        + " passing prey. You inch closer to where you suspect they are headed,"
                        + " and slowly {other:name} comes into view. Just as {other:pronoun}"
                        + " passes you, you stick out a leg and trip {other:direct-object}."
                        + " With a satisfying crunch of the leaves, {other:pronoun} falls."
                        + " Immediately you jump on {other:possessive} back and tie "
                        + "{other:possessive} hands together.";
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
    public BushAmbush() {
        super("Hide in Bushes");
    }

    @Override
    public boolean usable(Participant user) {
        return (user.getCharacter().get(Attribute.Cunning) >= 20 || user.getCharacter().get(Attribute.Animism) >= 10)
                && !(user.state instanceof State)
                && !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
