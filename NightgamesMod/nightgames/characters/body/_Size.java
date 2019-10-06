package nightgames.characters.body;

import java.util.Collection;

// DO NOT LEAK THIS TYPE FROM THE PACKAGE
interface _Size<SizeType> {
    SizeType applyModifications(Collection<TemporarySizeModification> modification);
    SizeType applyModifier(int modifier);
}
