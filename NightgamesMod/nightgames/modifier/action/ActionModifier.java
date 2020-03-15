package nightgames.modifier.action;

import nightgames.match.Action;

public interface ActionModifier {
    boolean allowAction(Action act);
}
