package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;

import java.util.Optional;
import java.util.Set;

public class Resupply extends Action {

    public interface Trigger {
        void onActionStart(Participant usedAction);
    }

    public class Instance extends Action.Instance {

        protected Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            user.state = new State();
            messageOthersInLocation(user.getCharacter().getGrammar().subject().defaultNoun() +
                    " heads for one of the safe rooms, probably to get a change of clothes.");
        }
    }

    public class State implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.invalidAttackers.clear();
            p.getCharacter().change();
            p.state = new Ready();
            p.getCharacter().getWillpower().renew();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            throw new UnsupportedOperationException(String.format("%s can't be attacked while resupplying",
                    p.getCharacter().getTrueName()));
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("%s can't be attacked while resupplying",
                    p.getCharacter().getTrueName()));
        }

    }

    public static final class EscapeRoute {
        private final Area destination;
        private final String message;

        public EscapeRoute(Area destination, String message) {
            this.destination = destination;
            this.message = message;
        }

        public boolean usable() {
            return destination.getOccupants().isEmpty();
        }

        public void use(Participant p) {
            p.travel(destination, message);
        }
    }


    protected Resupply() {
        super("Resupply");
    }

    public static Resupply withEscapeRoutes(Set<EscapeRoute> escapeRoutes) {
        return new ResupplyNormal(escapeRoutes);
    }

    public static Resupply limitToCharacters(Set<Participant> validParticipants, Set<Trigger> actionStartTriggers) {
        return new ResupplyFTC(validParticipants, actionStartTriggers);
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
