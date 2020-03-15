package nightgames.modifier.action;

import nightgames.match.Action;

import java.util.function.Predicate;

public class BanActionModifier extends ActionModifier {
    private final String description;
    private final Predicate<Action> predicate;

    public BanActionModifier(String description, Predicate<Action> predicate) {
        this.description = description;
        this.predicate = predicate;
    }

    @Override
    public boolean actionIsBanned(Action a) {
        return super.actionIsBanned(a) || predicate.test(a);
    }

    @Override
    public String toString() {
        return "Banned: " + description;
    }

}
