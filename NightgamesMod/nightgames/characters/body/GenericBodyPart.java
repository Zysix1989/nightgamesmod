package nightgames.characters.body;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.mods.PartMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.json.JsonUtils;

public class GenericBodyPart implements BodyPart {

    private static class TemporaryModApplication {
        private PartMod mod;
        private int duration;

        private TemporaryModApplication(PartMod mod, int duration) {
            this.mod = mod;
            this.duration = duration;
        }

        private void timePasses() {
            duration--;
        }

        private boolean isExpired() {
            return duration < 0;
        }
    }

    private final String type;
    final String desc;
    private final String prefix;
    public final double hotness;
    private final double sensitivity;
    private final double pleasure;
    private final String descLong;
    private final boolean notable;
    private List<PartMod> mods;
    private ArrayList<TemporaryModApplication> temporaryMods = new ArrayList<>();

    public GenericBodyPart(String desc, String descLong, double hotness, double pleasure, double sensitivity,
                    boolean notable, String type, String prefix) {
        this.desc = desc;
        this.descLong = descLong;
        this.hotness = hotness;
        this.pleasure = pleasure;
        this.sensitivity = sensitivity;
        this.type = type;
        this.notable = notable;
        this.prefix = prefix;
        this.mods = new ArrayList<>();
    }

    public GenericBodyPart(String desc, double hotness, double pleasure, double sensitivity, String type,
                    String prefix) {
        this(desc, "", hotness, pleasure, sensitivity, false, type, prefix);
    }

    public GenericBodyPart(String desc, double hotness, double pleasure, double sensitivity, boolean notable,
                    String type, String prefix) {
        this(desc, "", hotness, pleasure, sensitivity, notable, type, prefix);
    }

    private GenericBodyPart() {
        this("generic", "a generic body part", 0, 0, 0, false, "generic", "");
    }

    public GenericBodyPart(JsonObject js) {
        this();
        JsonUtils.getGson().fromJson(js, this.getClass());
    }

    protected GenericBodyPart(GenericBodyPart original) {
        this.desc = original.desc;
        this.descLong = original.descLong;
        this.hotness = original.hotness;
        this.pleasure = original.pleasure;
        this.sensitivity = original.sensitivity;
        this.type = original.type;
        this.notable = original.notable;
        this.prefix = original.prefix;
        this.mods = new ArrayList<>(original.mods);
    }

    @Override
    public String canonicalDescription() {
        return getPartMods().stream().sorted().map(PartMod::getModType).collect(Collectors.joining(" ")) + " " + desc;
    }

    @Override
    public void describeLong(StringBuilder b, Character c) {
        String parsedDesc = Global.format(descLong, c, c);
        for (PartMod mod : getPartMods()) {
            parsedDesc = mod.getLongDescriptionOverride(c, this, parsedDesc);
        }
        b.append(parsedDesc);
    }

    @Override
    public boolean isType(String type) {
        return this.getType().equalsIgnoreCase(type);
    }

    @Override
    public String getType() {
        return type;
    }

    protected String modlessDescription(Character c) {
        return desc;
    }
    
    public String getModDescriptorString(Character c) {
        return getPartMods().stream().sorted()
        .filter(mod -> mod.getDescriptionOverride(this).isEmpty())
        .map(mod -> mod.adjective(this))
        .filter(s -> !s.isEmpty())
        .map(string -> string + " ")
        .collect(Collectors.joining());
    }

    @Override
    public String describe(Character c) {
        Optional<String> override = getPartMods().stream().map(mod -> mod.getDescriptionOverride(this)).filter(Optional::isPresent).findFirst().flatMap(Function.identity());
        String normalDescription = override.orElseGet(() -> modlessDescription(c));

        return getModDescriptorString(c) + normalDescription;
    }

    @Override
    public double priority(Character c) {
        return (getPleasure(c, null) - 1) * 3;
    }

    @Override
    public String fullDescribe(Character c) {
        if (isNotable()) {
            return describe(c);
        } else {
            return "normal " + describe(c);
        }
    }

    @Override
    public String toString() {
        return fullDescribe(Global.noneCharacter());
    }

    @Override
    public double getHotness(Character self, Character opponent) {
        double bonus = 1.0;
        for (PartMod mod : getPartMods()) {
            bonus += mod.getHotness();
        }
        return hotness * bonus;
    }

    @Override
    public double getPleasure(Character self, BodyPart target) {
        double pleasureMod = pleasure;
        double pleasureBonus = 1.0;
        for (PartMod mod : getPartMods()) {
            pleasureBonus += mod.modPleasure(self);
        }
        pleasureMod *= pleasureBonus;
        if (type.equals(Body.HANDS) || type.equals(Body.FEET)) {
            pleasureMod += self.has(Trait.limbTraining1) ? .5 : 0;
            pleasureMod += self.has(Trait.limbTraining2) ? .7 : 0;
            pleasureMod += self.has(Trait.limbTraining3) ? .7 : 0;
            pleasureMod += self.has(Trait.dexterous) ? .4 : 0;
        }
        if (type.equals(Body.HANDS)) {
            pleasureMod += self.has(Trait.pimphand) ? .2 : 0;
        }
        return pleasureMod;
    }

    @Override
    public double getSensitivity(Character self, BodyPart target) {
        double bonus = 1.0;
        for (PartMod mod : getPartMods()) {
            bonus += mod.getSensitivity();
        }
        return sensitivity * bonus;
    }

    @Override
    public boolean isReady(Character self) {
        return true;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null)
            return false;
        if (!(other instanceof GenericBodyPart))
            return false;
        return canonicalDescription().equals(((GenericBodyPart)other).canonicalDescription());
    }

    @Override
    public int hashCode() {
        return (type + ":" + canonicalDescription()).hashCode();
    }

    @Override public JsonObject save() {
        return JsonUtils.getGson().toJsonTree(this, this.getClass()).getAsJsonObject();
    }

    @Override
    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart target) {
        for (PartMod mod : getPartMods()) {
            mod.onStartPenetration(c, self, opponent, this, target);
        }
    }

    @Override
    public void onOrgasm(Combat c, Character self, Character opponent) {
        for (PartMod mod : getPartMods()) {
            mod.onOrgasm(c, self, opponent, this);
        }
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan) {
        for (PartMod mod : getPartMods()) {
            mod.tickHolding(c, self, opponent, this, otherOrgan);
        }
    }

    @Override
    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart other, boolean selfCame) {
        for (PartMod mod : getPartMods()) {
            mod.onOrgasmWith(c, self, opponent, this, other, selfCame);
        }
    }

    @Override
    public double applyBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        int bonus = 0;
        for (PartMod mod : getPartMods()) {
            bonus += mod.applyBonuses(c, self, opponent, this, target, damage);
        }
        if (self.has(ClothingTrait.nursegloves) && type.equals(Body.HANDS)) {
            c.write(self, Global.format("{self:name-possessive} rubber gloves provide a "
                + "unique sensation as {self:subject-action:run|runs} {self:possessive} "
                + "hands over {other:possessive} " + target.describe(opponent) + ".", self, opponent));
            bonus += 5 + Global.random(5);
            if (Global.random(5) == 0) {
                c.write(self, "Unfortunately, the gloves wear out with their usage.");
                self.shred(ClothingSlot.arms);
            }
        }
        if (type.equals(Body.HANDS) && self.has(Trait.defthands)) {
            c.write(self, Global.format("{self:name-possessive} hands dance "
                + "across {other:possessive} " + target.describe(opponent) +
                ", hitting all the right spots.", self, opponent));
            bonus += Global.random(2, 6);
        }
        if (type.equals(Body.FEET) && self.has(Trait.nimbletoes)) {
            c.write(self, Global.format("{self:name-possessive} nimble toes adeptly "
                + "massage {other:possessive} " + target.describe(opponent)
                + " elicting a quiet gasp.", self, opponent));
            bonus += Global.random(2, 6);
        }
        return bonus;
    }

    public String getFluidsNoMods(Character c) {
        return "";
    }

    @Override
    public String getFluids(Character c) {
        Optional<String> nonJuicesMod = getPartMods().stream().filter(mod -> mod.getFluids().isPresent() && !mod.getFluids().get().equals("juices")).findFirst().flatMap(PartMod::getFluids);
        if (nonJuicesMod.isPresent()) {
            return nonJuicesMod.get();
        }
        Optional<String> anyMod = getPartMods().stream().filter(mod -> mod.getFluids().isPresent()).findFirst().flatMap(PartMod::getFluids);
        return anyMod.orElse(getFluidsNoMods(c));
    }

    @Override
    public final boolean isErogenous() {
        if (getPartMods().stream().anyMatch(mod -> mod.getErogenousOverride().isPresent())) {
            return getPartMods().stream().map(PartMod::getErogenousOverride).filter(Optional::isPresent).map(Optional::get).reduce(false, (a, b) -> a || b);
        }
        return getDefaultErogenous();
    }
    
    protected boolean getDefaultErogenous() {
        return false;
    }

    @Override
    public boolean isNotable() {
        return notable || !getPartMods().isEmpty();
    }

    @Override
    public double applyReceiveBonuses(Character self, Character opponent, BodyPart target, double damage, Combat c) {
        double bonus = 0;
        for (PartMod mod : getPartMods()) {
            bonus += mod.applyReceiveBonuses(c, self, opponent, this, target, damage);
        }
        return bonus;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public boolean isVisible(Character c) {
        return true;
    }

    @Override
    public double applySubBonuses(Character self, Character opponent, BodyPart with, BodyPart target, double damage,
                    Combat c) {
        return 0;
    }

    @Override
    public int mod(Attribute a, int total) {
        return 0;
    }

    @Override
    public int counterValue(BodyPart otherPart, Character self, Character other) {
        int counterValue = 0;
        for (PartMod mod : getPartMods()) {
            counterValue += mod.counterValue(this, otherPart, self, other);
        }
        return counterValue;
    }

    @Override
    public String adjective() {
        // implement when needed
        return type;
    }

    public GenericBodyPart withMod(PartMod mod) {
        GenericBodyPart newPart = copy();
        newPart.mods.removeIf(otherMod -> otherMod.getVariant().equals(mod.getVariant()));
        newPart.mods.add(mod);
        return newPart;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<BodyPartMod> getMods() {
        return (List<BodyPartMod>) (List)getPartMods();
    }

    protected List<PartMod> getPartMods() {
        var result = new ArrayList<>(mods);
        result.addAll(temporaryMods.stream().map(app -> app.mod).collect(Collectors.toList()));
        return result;
    }

    public void receiveCum(Combat c, Character self, Character donor, BodyPart sourcePart) {
        getMods().forEach(mod -> ((PartMod)mod).receiveCum(c, self, this, donor, sourcePart));
    }
    
    @Override
    public double getFetishEffectiveness() {
        return sensitivity==0?0.25:1.0;             //This Syntactic Sugar needs to be made clear through documentation. - DSM
    }
    
    @Override
    public double getFetishChance() {
        return sensitivity==0?0.1:0.25;
    }

    public GenericBodyPart copy() {
        return new GenericBodyPart(this);
    }

    public void addTemporaryMod(PartMod mod, int duration) {
        temporaryMods.add(new TemporaryModApplication(mod, duration));
    }

    public void removeTemporaryMod(PartMod target) {
        final var startingSize = temporaryMods.size();
        temporaryMods.removeIf(app -> app.mod == target);
        if (startingSize >= temporaryMods.size()) {
            throw new UnsupportedOperationException("couldn't find mod to remove");
        }
    }

    void timePasses(Combat c, Character self) {
        temporaryMods.forEach(TemporaryModApplication::timePasses);
        temporaryMods.stream()
            .filter(app -> !app.isExpired())
            .forEachOrdered(app ->
                Global.writeIfCombat(c, self,
                    Global.format("{self:NAME-POSSESSIVE} %s lost its %s.",
                        self, self, getType(), app.mod.describeAdjective(getType()))));
        temporaryMods.removeIf(TemporaryModApplication::isExpired);
    }

    public void purge() {
        temporaryMods.clear();
    }

    public void purge(String modType) {
        final var startingSize = mods.size();
        mods.removeIf(mod -> mod.getModType().equals(modType));
        if (startingSize <= mods.size()) {
            throw new UnsupportedOperationException(String.format("no mod with type %s", modType));
        }
    }
}