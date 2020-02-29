package nightgames.areas;

import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.characters.Attribute;
import nightgames.characters.Character;

import java.util.Optional;

public interface ActionFactory {
    Optional<Action> createActionFor(Character c);

    class ActionFactoryInstance implements ActionFactory {
        private final Action action;

        public ActionFactoryInstance(Action action) {
            this.action = action;
        }

        public static ActionFactoryInstance movement(Area adjacentRoom) {
            return new ActionFactoryInstance(new Move(adjacentRoom,
                    "Move(" + adjacentRoom.name + ")",
                    ch -> !ch.bound()));
        }

        public static ActionFactoryInstance shortcut(Area adjacentRoom) {
            return new ActionFactoryInstance(new Move(
                adjacentRoom,
                    "Take shortcut to " + adjacentRoom.name,
                    ch -> ch.getPure(Attribute.Cunning) >= 28 && !ch.bound()));
        }

        public static ActionFactoryInstance ninjaLeap(Area adjacentRoom) {
            return new ActionFactoryInstance(new Move(
                    adjacentRoom,
                    "Ninja Leap("+adjacentRoom.name+")",
                    ch -> ch.getPure(Attribute.Ninjutsu)>=5 && !ch.bound()));
        }

        public Optional<Action> createActionFor(Character c) {
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

}
