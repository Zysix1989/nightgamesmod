package nightgames.match.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Flatfooted;

import java.util.Optional;

public class PassAmbush extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " slip into an alcove.";
        }
    }

    public final class Instance extends Action.Instance {
        public final Action self;

        private Instance(Action self, Participant user) {
            super(user);
            this.self = self;
        }

        @Override
        public Action.Aftermath execute() {
            return executeOuter(user);
        }
    }

    public static class State implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().message("You are hiding in an alcove in the pass.");
        }

        @Override
        public boolean isDetectable() {
            return false;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            return Optional.of(() -> {
                int attackerScore = 30 + p.getCharacter().get(Attribute.Speed) * 10 + p.getCharacter().get(Attribute.Perception) * 5
                                + Global.random(30);
                int victimScore = other.getCharacter().get(Attribute.Speed) * 10 + other.getCharacter().get(Attribute.Perception) * 5 + Global.random(30);
                String attackerMessage;
                String victimMessage;
                if (attackerScore > victimScore) {
                    attackerMessage = "You wait in a small alcove, waiting for someone to pass you."
                            + " Eventually, you hear footsteps approaching and you get ready."
                            + " As soon as {other:name} comes into view, you jump out and push"
                            + " {other:direct-object} against the opposite wall. The impact seems to"
                            + " daze {other:direct-object}, giving you an edge in the ensuing fight.";

                    victimMessage = "Of course you know that walking through a narrow pass is a"
                            + " strategic risk, but you do so anyway. Suddenly, {self:name}"
                            + " flies out of an alcove, pushing you against the wall on the"
                            + " other side. The impact knocks the wind out of you, putting you"
                            + " at a disadvantage.";
                } else {
                    attackerMessage = "While you are hiding behind a rock, waiting for someone to"
                            + " walk around the corner up ahead, you hear a soft cruch behind"
                            + " you. You turn around, but not fast enough. {other:name} is"
                            + " already on you, and has grabbed your shoulders. You are unable"
                            + " to prevent {other:direct-object} from throwing you to the ground,"
                            + " and {other:pronoun} saunters over. \"Were you waiting for me,"
                            + " {self:name}? Well, here I am.\"";

                    victimMessage = "You are walking through the pass when you see {self:name}"
                            + " crouched behind a rock. Since {self:pronoun} is very focused"
                            + " in looking the other way, {self:pronoun} does not see you coming."
                            + " Not one to look a gift horse in the mouth, you sneak up behind"
                            + " {self:direct-object} and grab {self:direct-object} in a bear hug."
                            + " Then, you throw {self:direct-object} to the side, causing"
                            + " {self:direct-object} to fall to the ground.";

                }

                p.getCharacter().message(Global.format(attackerMessage, p.getCharacter(), other.getCharacter()));
                other.getCharacter().message(Global.format(victimMessage, p.getCharacter(), other.getCharacter()));
                encounter.startFight(p, other);

                if (attackerScore > victimScore) {
                    other.getCharacter().addNonCombat(new Status(new Flatfooted(other.getCharacter(), 3)));
                } else {
                    p.getCharacter().addNonCombat(new Status(new Flatfooted(p.getCharacter(), 3)));
                }
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

    public PassAmbush() {
        super("Try Ambush");
    }

    @Override
    public boolean usable(Participant user) {
        return !(user.state instanceof State)
                && !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user) {
        return new Instance(this, user);
    }

    @Override
    public Action.Aftermath executeOuter(Participant user) {
        user.getCharacter().message("You try to find a decent hiding place in the irregular rock faces lining the pass.");
        user.state = new State();
        return new Aftermath();
    }

}
