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


    private final Participant owner;
    private final Set<Trigger> actionStartTriggers;


    public ResupplyFTC(Participant owner, Set<Trigger> actionStartTriggers) {
        super();
        this.owner = owner;
        this.actionStartTriggers = actionStartTriggers;
    }

    @Override
    public boolean usable(Participant user) {
        return super.usable(user) && owner.equals(user);
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }
}
