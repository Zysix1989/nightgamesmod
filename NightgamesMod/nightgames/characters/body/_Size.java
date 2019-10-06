package nightgames.characters.body;

import java.util.Collection;

// DO NOT LEAK THIS TYPE FROM THE PACKAGE
interface _Size<SizeType> {
    SizeType withModifications(Collection<TemporarySizeModification> modification);
    SizeType withModifier(int modifier);
}
