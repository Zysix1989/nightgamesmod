package nightgames.beans;

import java.util.HashSet;
import java.util.Set;

public class Property<T> {
    private T value;
    private Set<PropertyChangeListener<T>> changeListeners = new HashSet<>();

    public Property(T initialValue) {
        this.value = initialValue;
    }

    public T get() { return value; }

    public void set(T value) {
        changeListeners.forEach(changeListener -> changeListener.onChange(this.value, value));
        this.value = value;
    }

    public void addPropertyChangeListener(PropertyChangeListener<T> changeListener) {
        changeListeners.add(changeListener);
    }

    public void bindDirectional(Property<T> source, Property<T> target) {
        source.addPropertyChangeListener(((oldValue, newValue) -> target.set(newValue)));
    }
}
