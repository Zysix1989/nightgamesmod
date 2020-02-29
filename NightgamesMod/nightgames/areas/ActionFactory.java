package nightgames.areas;

import nightgames.actions.Action;
import nightgames.characters.Character;

import java.util.Optional;

public interface ActionFactory {
    Optional<Action> createActionFor(Character c);

    class Movement implements ActionFactory {
        private final Area adjacentRoom;

        Movement(Area adjacentRoom) {
            this.adjacentRoom = adjacentRoom;
        }

        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Move(adjacentRoom);
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class ShortcutMovement implements ActionFactory {
        private final Area adjacentRoom;

        ShortcutMovement(Area adjacentRoom) {
            this.adjacentRoom = adjacentRoom;
        }

        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Shortcut(adjacentRoom);
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }
}
