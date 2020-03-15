package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.match.Action;
import nightgames.match.Participant;

public final class Move extends Action {

    public interface SkillCheck {
        boolean check(Character c);
    }

    private final class Aftermath extends Action.Aftermath {
        private Aftermath(Participant usedAction) {
            super(usedAction);
        }

        @Override
        public String describe(Character c) {
            return destination.getMovementToAreaDescription(c);
        }
    }

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public Action.Aftermath execute() {
            var aftermath = new Aftermath(user);
            user.travel(destination, aftermath.describe());
            return aftermath;
        }

        public Area getDestination() {
            return destination;
        }

        public boolean maybeDetectOccupancy(int perception) {
            return detectDestination && destination.ping(perception);
        }
    }

    private Area destination;
    private boolean detectDestination;
    private SkillCheck skillCheck;

    private Move(Area destination, String name, boolean detectDestination, SkillCheck check) {
        super(name);
        this.detectDestination = detectDestination;
        this.destination = destination;
        this.skillCheck = check;
    }

    public static Move normal(Area adjacentRoom) {
        return new Move(adjacentRoom,
                "Move(" + adjacentRoom.name + ")",
                true,
                ch -> !ch.bound() && !ch.has(Trait.immobile));
    }

    public static Move shortcut(Area adjacentRoom) {
        return new Move(adjacentRoom,
                "Take shortcut to " + adjacentRoom.name,
                false, // who can tell what's going on in that winding system of tunnels?
                ch -> ch.getPure(Attribute.Cunning) >= 28 && !ch.bound() && !ch.has(Trait.immobile));
    }

    public static Move ninjaLeap(Area adjacentRoom) {
        return new Move(adjacentRoom,
                "Ninja Leap("+adjacentRoom.name+")",
                true, // got to be able to spot the landing
                ch -> ch.getPure(Attribute.Ninjutsu)>=5 && !ch.bound() && !ch.has(Trait.immobile));
    }

    @Override
    public boolean usable(Participant user) {
        return skillCheck.check(user.getCharacter());
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

    public Area getDestination() {
        return destination;
    }

}
