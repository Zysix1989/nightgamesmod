package nightgames.areas;

import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.match.Participant;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface ActionFactory {
    Optional<Action> createActionFor(Character c);

    class Movement implements ActionFactory {
        private final Area adjacentRoom;
        private final String label;
        private final Move.SkillCheck skillCheck;

        private Movement(Area adjacentRoom, String label, Move.SkillCheck check) {
            this.adjacentRoom = adjacentRoom;
            this.label = label;
            this.skillCheck = check;
        }

        public static Movement movement(Area adjacentRoom) {
            return new Movement(adjacentRoom,
                    "Move(" + adjacentRoom.name + ")",
                    ch -> !ch.bound());
        }

        public static Movement shortcut(Area adjacentRoom) {
            return new Movement(
                adjacentRoom,
                    "Take shortcut to " + adjacentRoom.name,
                    ch -> ch.getPure(Attribute.Cunning) >= 28 && !ch.bound());
        }

        public static Movement ninjaLeap(Area adjacentRoom) {
            return new Movement(
                    adjacentRoom,
                    "Ninja Leap("+adjacentRoom.name+")",
                    ch -> ch.getPure(Attribute.Ninjutsu)>=5 && !ch.bound());
        }
        public Optional<Action> createActionFor(Character c) {
            var action = new nightgames.actions.Move(adjacentRoom,
                    label,
                    skillCheck);
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
