package nightgames.beans;

public interface PropertyChangeListener<T> {
    void onChange(T oldValue, T newValue);
}
