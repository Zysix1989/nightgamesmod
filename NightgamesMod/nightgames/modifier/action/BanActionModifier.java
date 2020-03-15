package nightgames.modifier.action;

import nightgames.match.Action;

import java.util.Set;

public class BanActionModifier extends ActionModifier {
    private final Set<Action> absolutes;

    public BanActionModifier(Action action) {
        absolutes = Set.of(action);
    }

    @Override
    public boolean actionIsBanned(Action a) {
        return super.actionIsBanned(a) || absolutes.contains(a);
    }

    @Override
    public String toString() {
        return "Banned:" + absolutes.toString();
    }

}
