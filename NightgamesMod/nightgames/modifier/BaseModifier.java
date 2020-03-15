package nightgames.modifier;

import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Match;
import nightgames.modifier.action.ActionPredicate;
import nightgames.modifier.clothing.ClothingModifier;
import nightgames.modifier.clothing.ClothingModifierCombiner;
import nightgames.modifier.item.ItemModifier;
import nightgames.modifier.item.ItemModifierCombiner;
import nightgames.modifier.skill.SkillModifier;
import nightgames.modifier.skill.SkillModifierCombiner;
import nightgames.modifier.status.StatusModifier;
import nightgames.modifier.status.StatusModifierCombiner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class BaseModifier implements Modifier {

    protected static final BiConsumer<Character, Match> EMPTY_CONSUMER = (c, m) -> {
    };

    protected ClothingModifier clothing;
    protected ItemModifier items;
    protected StatusModifier status;
    protected SkillModifier skills;
    protected ActionPredicate actions;
    protected BiConsumer<Character, Match> custom;

    protected Map<Character, Map<Item, Integer>> moddedItems;

    protected BaseModifier() {
        this.clothing = ClothingModifierCombiner.NULL_MODIFIER;
        this.items = ItemModifierCombiner.NULL_MODIFIER;
        this.status = StatusModifierCombiner.NULL_MODIFIER;
        this.skills = SkillModifierCombiner.NULL_MODIFIER;
        this.actions = act -> true;
        this.custom = EMPTY_CONSUMER;
        moddedItems = new HashMap<>();
    }

    @Override
    public void handleOutfit(Character c) {
        if (c.human() || !clothing.playerOnly()) {
            clothing.apply(c.outfit);
        }
    }

    @Override
    public void handleItems(Character c) {
        moddedItems.putIfAbsent(c, new HashMap<>());
        Map<Item, Integer> inventory = new HashMap<>(c.getInventory());
        inventory.forEach((item, count) -> {
            if (items.itemIsBanned(c, item)) {
                c.getInventory().remove(item);
                moddedItems.get(c).putIfAbsent(item, 0);
                moddedItems.get(c).compute(item, (i, cnt) -> cnt - count);
            }
        });
        items.ensuredItems().forEach((item, count) -> {
            while (!c.has(item, count)) {
                c.gain(item);
                moddedItems.get(c).putIfAbsent(item, 0);
                moddedItems.get(c).compute(item, (i, cnt) -> cnt + 1);
            }
        });
    }

    @Override
    public void handleStatus(Character c) {
        status.apply(c);
    }

    @Override
    public SkillModifier getSkillModifier() {
        return skills;
    }

    @Override
    public void handleTurn(Character c, Match m) {
        custom.accept(c, m);
    }

    @Override
    public boolean allowAction(Action act, Character c) {
        return !c.human() || actions.allowAction(act);
    }

    @Override
    public void undoItems(Character c) {
        if (moddedItems.containsKey(c)) {
            moddedItems.get(c).forEach((item, count) -> c.gain(item, -count));
        }
    }

    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                        "Modifier\n\tClothing: %s\n\tItems: %s\n\t"
                                        + "Status: %s\n\tSkills: %s\n\tActions: %s\n\tCustom Effect: %s\n",
                        clothing.toString(), items.toString(), status.toString(), skills.toString(), actions.toString(),
                        custom == EMPTY_CONSUMER ? "no" : "yes");
    }
}
