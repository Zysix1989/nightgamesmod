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

    class LeapMovement implements ActionFactory {
        private final Area adjacentRoom;

        LeapMovement(Area adjacentRoom) {
            this.adjacentRoom = adjacentRoom;
        }

        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Leap(adjacentRoom);
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class BushAmbush implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.BushAmbush();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class PassAmbush implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.PassAmbush();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class TreeAmbush implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.TreeAmbush();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }
}
