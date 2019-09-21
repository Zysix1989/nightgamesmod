package nightgames.items;

import java.util.List;
import nightgames.characters.Character;
import nightgames.combat.Combat;

public interface Consumable extends Loot {
    List<ItemEffect> getEffects();

    ItemAmount amount(int amount);

    boolean usable(Combat c, Character self, Character target);
}
