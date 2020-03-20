package nightgames.modifier;

import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Match;
import nightgames.match.Participant;
import nightgames.modifier.action.DescribablePredicate;
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

public abstract class BaseModifier {

    protected static final BiConsumer<Character, Match> EMPTY_CONSUMER = (c, m) -> {
    };

    protected ClothingModifier clothing;
    protected ItemModifier items;
    protected StatusModifier status;
    protected SkillModifier skills;
    protected DescribablePredicate<Action.Instance> actions;
    protected BiConsumer<Character, Match> custom;

    protected Map<Character, Map<Item, Integer>> moddedItems;

    protected BaseModifier() {
        this.clothing = ClothingModifierCombiner.NULL_MODIFIER;
        this.items = ItemModifierCombiner.NULL_MODIFIER;
        this.status = StatusModifierCombiner.NULL_MODIFIER;
        this.skills = SkillModifierCombiner.NULL_MODIFIER;
        this.actions = DescribablePredicate.True();
        this.custom = EMPTY_CONSUMER;
        moddedItems = new HashMap<>();
    }

    /**
     * Ensure that the character has an appropriate outfit
     */
    public void handleOutfit(Character c) {
        if (c.human() || !clothing.playerOnly()) {
            clothing.apply(c.outfit);
        }
    }

    /**
     * Ensure that the character has a legal inventory
     */
    public void handleItems(Participant p) {
        moddedItems.putIfAbsent(p.getCharacter(), new HashMap<>());
        Map<Item, Integer> inventory = new HashMap<>(p.getCharacter().getInventory());
        inventory.forEach((item, count) -> {
            if (items.itemIsBanned(p, item)) {
                p.getCharacter().getInventory().remove(item);
                moddedItems.get(p.getCharacter()).putIfAbsent(item, 0);
                moddedItems.get(p.getCharacter()).compute(item, (i, cnt) -> cnt - count);
            }
        });
        items.ensuredItems().forEach((item, count) -> {
            while (!p.getCharacter().has(item, count)) {
                p.getCharacter().gain(item);
                moddedItems.get(p.getCharacter()).putIfAbsent(item, 0);
                moddedItems.get(p.getCharacter()).compute(item, (i, cnt) -> cnt + 1);
            }
        });
    }

    /**
     * Apply any required statuses
     */
    public void handleStatus(Character c) {
        status.apply(c);
    }

    /**
     * Get a SkillModifier specific to the current Match
     */
    public SkillModifier getSkillModifier() {
        return skills;
    }

    /**
     * Process non-combat turn
     */
    public void handleTurn(Character c, Match m) {
        custom.accept(c, m);
    }

    protected DescribablePredicate<Action.Instance> getActionFilter() {
        return actions;
    }

    public DescribablePredicate<Action.Instance> getActionFilterFor(Character c) {
        return getActionFilter().or(new DescribablePredicate<>("NPCs ignore conditions", act -> !c.human()));
    }

    /**
     * Undo all changes to the character's inventory made by handleItems
     */
    public void undoItems(Character c) {
        if (moddedItems.containsKey(c)) {
            moddedItems.get(c).forEach((item, count) -> c.gain(item, -count));
        }
    }

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

    public abstract int bonus();

    public abstract String name();

    public abstract String intro();

    public abstract String acceptance();
}
