package nightgames.areas;

import nightgames.actions.Action;
import nightgames.characters.Character;

import java.util.Optional;

public interface ActionFactory {
    Optional<Action> createActionFor(Character c);

    class ActionFactoryInstance implements ActionFactory {
        private final Action action;

        public ActionFactoryInstance(Action action) {
            this.action = action;
        }

        public Optional<Action> createActionFor(Character c) {
            if (action.usable(c)) {
                return Optional.of(action);
            }
            return Optional.empty();
        }
    }

}
