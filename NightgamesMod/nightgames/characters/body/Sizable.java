package nightgames.characters.body;

public interface Sizable<T extends Comparable<T>> {
    void temporarilyChangeSize(int modifier, int duration);
    void timePasses();
    T getSize();
}
