package nightgames.modifier.action;

import nightgames.match.Action;

import java.util.function.Predicate;

public class BanActionPredicate implements Predicate<Action> {
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
    public Predicate<Action> and(Predicate<? super Action> other) {
        var result = Predicate.super.and(other);
        if (other instanceof BanActionPredicate) {
            return new BanActionPredicate("(and " + toString() + " " + other.toString() + ")", result);
        }
        return result;
    }

    @Override
    public BanActionPredicate negate() {
        return new BanActionPredicate("(not " + toString() + ")", Predicate.super.negate());
    }

    @Override
    public Predicate<Action> or(Predicate<? super Action> other) {
        var result = Predicate.super.or(other);
        if (other instanceof BanActionPredicate) {
            return new BanActionPredicate("(or " + toString() + " " + other.toString() + ")", result);
        }
        return result;
    }

    @Override
    public String toString() {
        return "(not " + description + ")";
    }

}
