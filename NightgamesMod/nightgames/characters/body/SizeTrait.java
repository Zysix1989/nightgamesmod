package nightgames.characters.body;

import java.util.ArrayList;

class SizeTrait<SizeType extends _Size<SizeType>> {
    private SizeType size;
    private ArrayList<TemporarySizeModification> sizeModifications;

    SizeTrait(SizeType size) {
        this.size = size;
        sizeModifications = new ArrayList<>();
    }

    void temporarySizeChange(int modifier, int duration) {
        sizeModifications.add(new TemporarySizeModification(modifier, duration));
    }

    void timePasses() {
        sizeModifications.forEach(TemporarySizeModification::reduceDuration);
        sizeModifications.removeIf(TemporarySizeModification::isExpired);
    }

    SizeType getSize() {
        return size.applyModifications(sizeModifications);
    }
}
