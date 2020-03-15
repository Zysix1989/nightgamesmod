package nightgames.match.actions;

import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Craft extends Action {
    private static final class Aftermath extends Action.Aftermath {
        private Aftermath(Participant usedAction) {
            super(usedAction);
        }

        @Override
        public String describe(Character c) {
            return " start mixing various liquids. Whatever it is doesn't look healthy.";
        }
    }

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            user.state = new State();
            messageOthersInLocation(new Aftermath(user).describe());
        }
    }

    public static class State implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            Collection<Item> craftedItems;
            int roll = Global.random(15);
            if (p.getCharacter().check(Attribute.Cunning, 25)) {
                if (roll == 9) {
                    craftedItems = List.of(Item.Aphrodisiac, Item.DisSol);
                } else if (roll >= 5) {
                    craftedItems = List.of(Item.Aphrodisiac);
                } else {
                    craftedItems = List.of(Item.Lubricant, Item.Sedative);
                }
            } else if (p.getCharacter().check(Attribute.Cunning, 20)) {
                if (roll == 9) {
                    craftedItems = List.of(Item.Aphrodisiac);
                } else if (roll >= 7) {
                    craftedItems = List.of(Item.DisSol);
                } else if (roll >= 5) {
                    craftedItems = List.of(Item.Lubricant);
                } else if (roll >= 3) {
                    craftedItems = List.of(Item.Sedative);
                } else {
                    craftedItems = List.of(Item.EnergyDrink);
                }
            } else if (p.getCharacter().check(Attribute.Cunning, 15)) {
                if (roll == 9) {
                    craftedItems = List.of(Item.Aphrodisiac);
                } else if (roll >= 8) {
                    craftedItems = List.of(Item.DisSol);
                } else if (roll >= 7) {
                    craftedItems = List.of(Item.Lubricant);
                } else if (roll >= 6) {
                    craftedItems = List.of(Item.EnergyDrink);
                } else {
                    craftedItems = List.of();
                }
            } else if (roll >= 7) {
                craftedItems = List.of(Item.Lubricant);
            } else if (roll >= 5) {
                craftedItems = List.of(Item.Sedative);
            } else {
                craftedItems = List.of();
            }
            Character character = p.getCharacter();
            character.message("You spend some time crafting some potions with the equipment.");
            craftedItems.forEach(character::gain);
            character.update();
            if (craftedItems.isEmpty()) {
                character.message("Your concoction turns a sickly color and releases a foul smelling smoke. You trash it before you do any more damage.");
            }
            p.state = new Ready();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            return Optional.of(() -> encounter.spy(other, p));
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("spot check for %s should have already been replaced",
                    p.getCharacter().getTrueName()));
        }
    }

    public Craft() {
        super("Craft Potion");
    }

    @Override
    public boolean usable(Participant user) {
        return user.getCharacter().get(Attribute.Cunning) > 15 && !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
