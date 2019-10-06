package nightgames.characters.body;

public interface Sizable<T> {
    void temporarySizeChange(int modifier, int duration);
    void timePasses();
    T getSize();
}
