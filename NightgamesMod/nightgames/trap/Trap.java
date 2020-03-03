package nightgames.trap;

import nightgames.areas.Deployable;
import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;

import java.util.Map;
import java.util.Optional;

public abstract class Trap {

    public abstract static class Instance implements Deployable {
        private final Trap self;
        protected final Participant owner;

        public Instance(Trap self, Participant owner) {
            this.self = self;
            this.owner = owner;
        }

        protected abstract void trigger(Participant target);

        @Override
        public boolean resolve(Participant active) {
            if (active != owner) {
                trigger(active);
                return true;
            }
            return false;
        }

        public Character owner() {
            return owner.getCharacter();
        }

        @Override
        public int priority() {
            return 0;
        }

        public String getName() {
            return self.name;
        }

        public Optional<Position> capitalize(Character attacker, Character victim) {
            return Optional.empty();
        }
    }

    private final String name;

    protected Trap(String name) {
        this.name = name;
    }

    public boolean recipe(Participant owner) {
        return requiredItems().entrySet().stream().allMatch(entry-> owner.getCharacter().has(entry.getKey(), entry.getValue()));
    }

    public abstract boolean requirements(Participant user);

    protected abstract Map<Item, Integer> requiredItems();

    protected void deductCostsFrom(Participant c) {
        requiredItems().forEach(c.getCharacter()::consume);
    }

    public static class InstantiateResult {
        public final String message;
        public final Instance instance;

        InstantiateResult(String msg, Instance instance) {
            this.message = msg;
            this.instance = instance;
        }
    }

    public abstract InstantiateResult instantiate(Participant owner);

    @Override
    public final String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

}
