package nightgames.modifier.item;

import nightgames.items.Item;
import nightgames.match.Participant;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class ItemModifier {


    public Set<Item> bannedItems() {
        return Collections.emptySet();
    }

    public Map<Item, Integer> ensuredItems() {
        return Collections.emptyMap();
    }

    public boolean itemIsBanned(Participant p, Item i) {
        return !playerOnly() || p.getCharacter().human() && bannedItems().contains(i);
    }

    public boolean playerOnly() {
        return true;
    }

    @Override
    public abstract String toString();
}
