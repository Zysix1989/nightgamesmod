package nightgames.modifier.action;

import nightgames.match.Action;

import java.util.Set;

public class BanActionModifier extends ActionModifier {
    private static final String name = "ban-action";

    private final Set<Action> absolutes;

    public BanActionModifier(Action... actions) {
        absolutes = Set.of(actions);
    }

    @Override
    public boolean actionIsBanned(Action a) {
        return super.actionIsBanned(a) || absolutes.contains(a);
    }

    @Override public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "Banned:" + absolutes.toString();
    }

}
