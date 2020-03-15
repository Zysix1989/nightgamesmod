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

    @Override
    public Predicate<T> and(Predicate<? super T> other) {
        var result = Predicate.super.and(other);
        if (other instanceof DescribablePredicate) {
            return new DescribablePredicate<>("(and " + toString() + " " + other.toString() + ")", result);
        }
        return result;
    }

    @Override
    public DescribablePredicate<T> negate() {
        return new DescribablePredicate<>("(not " + toString() + ")", Predicate.super.negate());
    }

    @Override
    public Predicate<T> or(Predicate<? super T> other) {
        var result = Predicate.super.or(other);
        if (other instanceof DescribablePredicate) {
            return new DescribablePredicate<>("(or " + toString() + " " + other.toString() + ")", result);
        }
        return result;
    }

    @Override
    public String toString() {
        return "(not " + description + ")";
    }

}
