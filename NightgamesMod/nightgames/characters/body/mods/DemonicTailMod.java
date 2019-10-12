package nightgames.characters.body.mods;

import java.util.Optional;

public class DemonicTailMod extends PartMod {
    public static final String TYPE = "demonic tail";

    public DemonicTailMod() {
        super(TYPE, .05, 1.2, 1);
    }

    @Override
    public Optional<String> getFluids() {
        return Optional.of("tail-cum");
    }

    @Override
    public String describeAdjective(String partType) {
        return "demonic";
    }
}
