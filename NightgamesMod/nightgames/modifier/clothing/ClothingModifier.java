package nightgames.modifier.clothing;

import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.items.clothing.Outfit;
import nightgames.modifier.ModifierCategory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ClothingModifier implements ModifierCategory<ClothingModifier> {
    protected static final Set<Integer> ALL_LAYERS = Collections
                    .unmodifiableSet(IntStream.range(0, Clothing.N_LAYERS).boxed().collect(Collectors.toSet()));
    protected static final Set<ClothingSlot> ALL_SLOTS = Collections.unmodifiableSet(EnumSet.allOf(ClothingSlot.class));
    protected static final Map<ClothingSlot, Set<Integer>> ALL_SLOT_LAYER_COMBOS = Collections
                    .unmodifiableMap(EnumSet.allOf(ClothingSlot.class).stream().collect(Collectors.toMap(t -> t,
                                    t -> IntStream.range(0, Clothing.N_LAYERS).boxed().collect(Collectors.toSet()))));

    public Set<Integer> allowedLayers() {
        return ALL_LAYERS;
    }

    public Set<ClothingSlot> allowedSlots() {
        return ALL_SLOTS;
    }

    public Map<ClothingSlot, Set<Integer>> allowedSlotLayerCombos() {
        return ALL_SLOT_LAYER_COMBOS;
    }

    public Set<String> forbiddenItems() {
        return Collections.emptySet();
    }

    public Set<String> forcedItems() {
        return Collections.emptySet();
    }

    public Set<ClothingTrait> forbiddenClothingTraits() {
        return Collections.emptySet();
    }

    public boolean playerOnly() {
        return true;
    }

    public void apply(Outfit outfit) {
        Set<Clothing> equipped = new HashSet<>(outfit.getEquipped());
        equipped.forEach(outfit::unequip);

        // remove disallowed articles
        equipped.removeIf(c -> forbiddenItems().contains(c.getName()));

        // remove disallowed layers
        equipped.removeIf(c -> !allowedLayers().contains(c.getLayer()));

        // remove disallowed slots
        equipped.removeIf(c -> c.getSlots().stream().anyMatch(s -> !allowedSlots().contains(s)));

        // remove disallowed combinations
        equipped.removeIf(c -> !allowedSlotLayerCombos().entrySet().stream()
                        .allMatch(e -> !c.getSlots().contains(e.getKey()) || e.getValue().contains(c.getLayer())));

        // remove disallowed attributes
        equipped.removeIf(c -> forbiddenClothingTraits().stream().anyMatch(t -> c.attributes().contains(t)));

        // add forced items, first remove same slots
        equipped.removeIf(c -> forcedItems().stream().map(Clothing::getByID)
                        .anyMatch(c2 -> c2.getSlots().stream().anyMatch(s -> c.getSlots().contains(s))
                                        && c2.getLayer() == c.getLayer()));
        forcedItems().stream().map(Clothing::getByID).forEach(equipped::add);

        equipped.forEach(outfit::equip);
    }

    @Override
    public abstract String toString();

    public static void main(String[] args) {
        Clothing.buildClothingTable();

        Outfit test1 = new Outfit();
        test1.equip(Clothing.getByID("bra"));
        test1.equip(Clothing.getByID("panties"));
        test1.equip(Clothing.getByID("jeans"));
        test1.equip(Clothing.getByID("shirt"));

        Outfit test2 = new Outfit(test1);
        Outfit test3 = new Outfit(test1);
        Outfit test4 = new Outfit(test1);
        Outfit test5 = new Outfit(test1);

        new UnderwearOnlyModifier().apply(test2);
        System.out.println(test2);

        new NoPantiesModifier().apply(test3);
        System.out.println(test3);
    }
}
