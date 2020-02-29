package nightgames.areas;

import nightgames.actions.Action;
import nightgames.characters.Character;
import nightgames.match.Participant;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    class Bathe implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Bathe();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class Craft implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Craft();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class Energize implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Energize();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class Hide implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Hide();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class Recharge implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Recharge();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class Resupply implements ActionFactory {
        boolean permissioned;
        Set<Character> allowedCharacters;

        public Resupply() {
            permissioned = false;
        }

        public Resupply(Set<Participant> participants) {
            permissioned = true;
            allowedCharacters = participants.stream().map(Participant::getCharacter).collect(Collectors.toSet());
        }

        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Resupply();
            if (action.usable(c) && (!permissioned || allowedCharacters.contains(c))) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

    class Scavenge implements ActionFactory {
        @Override
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Scavenge();
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

}
