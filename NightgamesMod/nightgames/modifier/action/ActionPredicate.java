package nightgames.modifier.action;

import nightgames.match.Action;

public interface ActionPredicate {
    boolean allowAction(Action act);
}
