package nightgames.characters.body;

public interface Sizable<T extends Comparable<T>> {
    void changeSize(int modifier);
    void temporarilyChangeSize(int modifier, int duration);
    void timePasses();
    T getSize();
}
