package nightgames.modifier.item;

import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.modifier.ModifierCategory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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

    @Override
    public abstract String toString();
}
