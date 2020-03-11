package nightgames.actions;

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
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return destination.getMovementToAreaDescription(c);
        }
    }

    private Area destination;
    private SkillCheck skillCheck;

    private Move(Area destination, String name, SkillCheck check) {
        super(name);
        this.destination = destination;
        this.skillCheck = check;
    }

    public static Move normal(Area adjacentRoom) {
        return new Move(adjacentRoom,
                "Move(" + adjacentRoom.name + ")",
                ch -> !ch.bound() && !ch.has(Trait.immobile));
    }

    public static Move shortcut(Area adjacentRoom) {
        return new Move(adjacentRoom,
                "Take shortcut to " + adjacentRoom.name,
                ch -> ch.getPure(Attribute.Cunning) >= 28 && !ch.bound() && !ch.has(Trait.immobile));
    }

    public static Move ninjaLeap(Area adjacentRoom) {
        return new Move(adjacentRoom,
                "Ninja Leap("+adjacentRoom.name+")",
                ch -> ch.getPure(Attribute.Ninjutsu)>=5 && !ch.bound() && !ch.has(Trait.immobile));
    }

    @Override
    public boolean usable(Participant user) {
        return skillCheck.check(user.getCharacter());
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.travel(destination);
        return new Aftermath();
    }

    public Area getDestination() {
        return destination;
    }

}
