package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;

public abstract class AdditionalPussyMod extends PartMod {
    public static final String TYPE = "secondpussy";

    public AdditionalPussyMod() {
        super(TYPE, .2, .2, .3);
    }

    @Override
    public String adjective(GenericBodyPart part) {
        return "";
    }

    public Optional<String> getFluids() {
        return Optional.of("juices");
    }

    public Optional<String> getDescriptionOverride(BodyPart part) {
        return Optional.of(part.adjective() + " pussy");
    }

    public Optional<Boolean> getErogenousOverride() {
        return Optional.of(true);
    }

    @Override
    public String describeAdjective(String partType) {
        return "vaginal aspects";
    }


}
