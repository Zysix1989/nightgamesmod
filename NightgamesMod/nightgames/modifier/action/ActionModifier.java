package nightgames.modifier.action;

import nightgames.match.Action;
import nightgames.modifier.ModifierComponent;

public abstract class ActionModifier implements ModifierComponent {

    public boolean actionIsBanned(Action act) {
        return false;
    }

    @Override
    public abstract String toString();
}
