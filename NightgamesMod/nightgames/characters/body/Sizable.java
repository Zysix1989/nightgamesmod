package nightgames.characters.body;

public interface Sizable<T extends Comparable<T>> {
    void changeSize(int modifier);
    void temporarilyChangeSize(int modifier, int duration);
    void setSize(T size);
    void timePasses();
    T getSize();
}
