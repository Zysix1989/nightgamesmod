package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.match.Participant;

import java.util.Set;
import java.util.stream.Collectors;

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


    private final Set<Character> validCharacters;
    private final Set<Trigger> actionStartTriggers;


    public ResupplyFTC(Set<Participant> validParticipants, Set<Trigger> actionStartTriggers) {
        super();
        validCharacters = validParticipants.stream().map(Participant::getCharacter).collect(Collectors.toSet());
        this.actionStartTriggers = actionStartTriggers;
    }

    @Override
    public boolean usable(Participant user) {
        return super.usable(user) && validCharacters.contains(user.getCharacter());
    }
}
