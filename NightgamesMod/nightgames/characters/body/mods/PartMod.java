package nightgames.characters.body.mods;

import java.util.Optional;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.BodyPartMod;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;

public abstract class PartMod implements BodyPartMod {
    private final String modType;
    protected final double hotness;
    protected final double pleasure;
    protected final double sensitivity;

    public PartMod(String modType, double hotness, double pleasure, double sensitivity) {
        this.modType = modType;
        this.hotness = hotness;
        this.pleasure = pleasure;
        this.sensitivity = sensitivity;
    }

    /**
     * This should be overridden if there needs to be more than only one variant of the attributeModifier active at the same time.
     */
    public String getVariant() {
        return modType;
    }

    @Override
    public String getModType() {
        return modType;
    }

    public String adjective(GenericBodyPart part) {
        return modType;
    }

    // override these if needed
    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { return 0; }
    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {}
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) { return 0; }

    public void tickHolding(Combat c, Character self, Character opponent, BodyPart part, BodyPart otherOrgan) {}
    public int counterValue(BodyPart part, BodyPart otherPart, Character self, Character other) { return 0; }
    public void onOrgasm(Combat c, Character self, Character opponent, BodyPart part) {}
    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {}
    public void receiveCum(Combat c, Character self, BodyPart part, Character donor, BodyPart sourcePart) {}
    
    public double modPleasure(Character self) {
        return pleasure;
    }

    public double getHotness() {
        return hotness;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public Optional<String> getFluids() {
        return Optional.empty();
    }

    public Optional<Boolean> getErogenousOverride() {
        return Optional.empty();
    }

    public String getLongDescriptionOverride(Character self, BodyPart part, String previousDescription) {
        return previousDescription;
    }

    public Optional<String> getDescriptionOverride(BodyPart part) {
        return Optional.empty();
    }

    public abstract String describeAdjective(String partType);
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof PartMod) {
            return other.toString().equals(this.toString());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "PartMod:" + getClass().getSimpleName() + ":" + modType;
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public Optional<Integer> attributeModifier(Attribute a) {
        return Optional.empty();
    }
}