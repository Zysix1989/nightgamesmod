package nightgames.modifier.action;

import java.util.function.Predicate;

public class DescribablePredicate<T> implements Predicate<T> {
    private final String description;
    private final Predicate<T> predicate;

    public DescribablePredicate(String description, Predicate<T> predicate) {
        this.description = description;
        this.predicate = predicate;
    }

    public static <T> DescribablePredicate<T> True() {
        return new DescribablePredicate<>("true", obj -> true);
    }

    @Override
    public boolean test(T act) {
        return predicate.test(act);
    }

    public DescribablePredicate<T> and(DescribablePredicate<T> other) {
        return new DescribablePredicate<>("(and " + toString() + " " + other.toString() + ")", Predicate.super.and(other));
    }

    @Override
    public DescribablePredicate<T> negate() {
        return new DescribablePredicate<>("(not " + toString() + ")", Predicate.super.negate());
    }

    public DescribablePredicate<T> or(DescribablePredicate<T> other) {
        return new DescribablePredicate<>("(or " + toString() + " " + other.toString() + ")", Predicate.super.or(other));
    }

    @Override
    public String toString() {
        return "(not " + description + ")";
    }

}
