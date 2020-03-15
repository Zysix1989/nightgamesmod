package nightgames.modifier.action;

import nightgames.match.Action;

import java.util.function.Predicate;

public class BanActionPredicate implements ActionPredicate {
    private final String description;
    private final Predicate<Action> predicate;

    public BanActionPredicate(String description, Predicate<Action> predicate) {
        this.description = description;
        this.predicate = predicate;
    }

    @Override
    public boolean test(Action act) {
        return !predicate.test(act);
    }

    @Override
    public String toString() {
        return "Banned: " + description;
    }

}
