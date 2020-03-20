package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.match.Participant;

import java.util.Set;

public class ResupplyFTC extends Resupply {
    public final class Instance extends Resupply.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            actionStartTriggers.forEach(trigger -> trigger.onActionStart(user));
            user.getCharacter().message("You get a change of clothes from the chest placed here.");
            super.execute();
        }
    }


    private final Set<Participant> validCharacters;
    private final Set<Trigger> actionStartTriggers;


    public ResupplyFTC(Set<Participant> validParticipants, Set<Trigger> actionStartTriggers) {
        super();
        validCharacters = Set.copyOf(validParticipants);
        this.actionStartTriggers = actionStartTriggers;
    }

    @Override
    public boolean usable(Participant user) {
        return super.usable(user) && validCharacters.contains(user);
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }
}
