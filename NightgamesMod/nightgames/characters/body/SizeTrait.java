package nightgames.characters.body;

import java.util.ArrayList;

class SizeTrait<SizeType extends _Size<SizeType>> {
    private SizeType size;
    private ArrayList<TemporarySizeModification> sizeModifications;

    SizeTrait(SizeType size) {
        this.size = size;
        sizeModifications = new ArrayList<>();
    }

    SizeTrait( SizeTrait<SizeType> original) {
        this.size = original.size;
        this.sizeModifications = new ArrayList<>(original.sizeModifications);
    }

    void changeSize(int modifier) {
        size = size.withModifier(modifier);
    }

    void temporarilyChangeSize(int modifier, int duration) {
        sizeModifications.add(new TemporarySizeModification(modifier, duration));
    }

    void timePasses() {
        sizeModifications.forEach(TemporarySizeModification::reduceDuration);
        sizeModifications.removeIf(TemporarySizeModification::isExpired);
    }

    SizeType getSize() {
        return size.withModifications(sizeModifications);
    }

    void setSize(SizeType target) {
        this.size = target;
    }
}
