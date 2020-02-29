package nightgames.areas;

import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.match.Participant;

import java.util.Optional;
import java.util.Set;

public interface ActionFactory {
    Optional<Action> createActionFor(Character c);

    class ActionFactoryInstance implements ActionFactory {
        private final Action action;

        private ActionFactoryInstance(Action action) {
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

    class BushAmbush extends ActionFactoryInstance {
        public BushAmbush() {
            super(new nightgames.actions.BushAmbush());
        }
    }

    class PassAmbush extends ActionFactoryInstance {
        public PassAmbush() {
            super(new nightgames.actions.PassAmbush());
        }
    }

    class TreeAmbush extends ActionFactoryInstance {
        public TreeAmbush() {
            super(new nightgames.actions.TreeAmbush());
        }
    }

    class Bathe extends ActionFactoryInstance {
        public Bathe() {
            super(new nightgames.actions.Bathe());
        }
    }

    class Craft extends ActionFactoryInstance {
        public Craft() {
            super(new nightgames.actions.Craft());
        }
    }

    class Energize extends ActionFactoryInstance {
        public Energize() {
            super(new nightgames.actions.Energize());
        }
    }

    class Hide extends ActionFactoryInstance {
        public Hide() {
            super(new nightgames.actions.Hide());
        }
    }

    class Recharge extends ActionFactoryInstance {
        public Recharge() {
            super(new nightgames.actions.Recharge());
        }
    }

    class Resupply extends ActionFactoryInstance {
        public Resupply() {
            super(new nightgames.actions.Resupply());
        }

        public Resupply(Set<Participant> participants) {
            super(new nightgames.actions.Resupply(participants));
        }
    }

    class Scavenge extends ActionFactoryInstance {
        public Scavenge() {
            super(new nightgames.actions.Scavenge());
        }
    }
}
