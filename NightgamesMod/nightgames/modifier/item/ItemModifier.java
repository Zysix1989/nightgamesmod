package nightgames.modifier.item;

import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.modifier.ModifierCategory;

import java.util.*;

public abstract class ItemModifier implements ModifierCategory<ItemModifier> {


    public Set<Item> bannedItems() {
        return Collections.emptySet();
    }

    public Map<Item, Integer> ensuredItems() {
        return Collections.emptyMap();
    }

    public boolean itemIsBanned(Character c, Item i) {
        return !playerOnly() || c.human() && bannedItems().contains(i);
    }

    public void giveRequiredItems(Character c) {
        ensuredItems().forEach((item, count) -> {
            while (!c.has(item, count)) {
                c.gain(item);
            }
        });
    }

    public boolean playerOnly() {
        return true;
    }

    @Override public ItemModifier combine(ItemModifier next) {
        ItemModifier first = this;
        return new ItemModifier() {
            @Override public Set<Item> bannedItems() {
                // bans items banned by either modifier
                Set<Item> bannedItems = new HashSet<>(first.bannedItems());
                bannedItems.addAll(next.bannedItems());
                return bannedItems;
            }

            @Override public Map<Item, Integer> ensuredItems() {
                // ensures items ensured by either modifier. If both modifiers ensure an item, ensures the sum of each item's amount.
                // {Item A: 4, Item B: 6}
                // combined with
                // {Item B: 3, Item C: 1}
                // gives
                // {Item A: 4, Item B: 9, Item C: 1}
                Map<Item, Integer> ensuredItems = new HashMap<>(first.ensuredItems());
                for (Map.Entry<Item, Integer> entry : next.ensuredItems().entrySet()) {
                    ensuredItems.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> oldValue + newValue);
                }
                return ensuredItems;
            }

            @Override
            public void giveRequiredItems(Character c) {
                first.giveRequiredItems(c);
                next.giveRequiredItems(c);
            }

            @Override
            public boolean itemIsBanned(Character c, Item i) {
                return first.itemIsBanned(c, i) || next.itemIsBanned(c, i);
            }

            @Override
            public String toString() {
                return first.toString() + next.toString();
            }

        };
    }

    @Override
    public abstract String toString();
}
