package nightgames.modifier.action;

import nightgames.match.Action;

public abstract class ActionModifier {

    public boolean actionIsBanned(Action act) {
        return false;
    }

    @Override
    public abstract String toString();
}
