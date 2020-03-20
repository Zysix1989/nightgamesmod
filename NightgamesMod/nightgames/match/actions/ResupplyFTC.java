package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.match.Action;
import nightgames.match.Participant;
import nightgames.match.ftc.FTCMatch;
import nightgames.match.ftc.Hunter;
import nightgames.match.ftc.Prey;

import java.util.Set;

public final class ResupplyFTC extends Resupply implements Action.LocationDescription {
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
    private final String description;


    private ResupplyFTC(Participant owner, Set<Trigger> actionStartTriggers, String description) {
        super();
        this.owner = owner;
        this.actionStartTriggers = actionStartTriggers;
        this.description = description;
    }

    public static ResupplyFTC newHunterBase(Hunter owner, FTCMatch.Flag flag) {
        return new ResupplyFTC(owner, Set.of(flag.getSink()), String.format("%s %s base here.",
                owner.getCharacter().subjectAction("has"),
                owner.getCharacter().getGrammar().possessiveAdjective()));
    }

    public static ResupplyFTC newPreyCamp(Prey owner, FTCMatch.Flag flag) {
        return new ResupplyFTC(owner, Set.of(flag.getSource()), String.format("It's just a small camp where %s can " +
                "get a new flag if a hunter captures it.", owner.getCharacter().getGrammar().subject().defaultNoun()));
    }

    @Override
    public boolean usable(Participant user) {
        return super.usable(user) && owner.equals(user);
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

    @Override
    public String describeLocation() {
        return description;
    }
}
