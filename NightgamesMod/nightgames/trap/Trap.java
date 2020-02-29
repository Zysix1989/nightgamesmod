package nightgames.trap;

import nightgames.areas.Deployable;
import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;

import java.util.Map;
import java.util.Optional;

// TODO: Separate into TrapFactory and TrapInstance
public abstract class Trap {

    public static class Instance implements Deployable {
        protected Trap self;

        public Instance(Trap self) {
            this.self = self;
        }

        @Override
        public boolean resolve(Participant active) {
            if (active.getCharacter() != self.owner) {
                self.trigger(active, this);
                return true;
            }
            return false;
        }

        @Override
        public Character owner() {
            return self.owner;
        }

        @Override
        public int priority() {
            return 0;
        }

        public Trap getTrap() {
            return self;
        }
    }

    protected Character owner;
    private final String name;
    private int strength;

    protected Trap(String name, Character owner) {
        this.name = name;
        this.owner = owner;
        this.setStrength(0);
    }
    
    protected abstract void trigger(Participant target, Instance instance);

    public boolean recipe(Character owner) {
        return requiredItems().entrySet().stream().allMatch(entry-> owner.has(entry.getKey(), entry.getValue()));
    }

    public abstract boolean requirements(Character owner);

    protected abstract Map<Item, Integer> requiredItems();

    protected final void basicSetup(Character owner) {
        this.owner = owner;
        requiredItems().forEach(owner::consume);
    }

    public abstract String setup(Character owner);

    public static class InstantiateResult {
        public final String message;
        public final Instance instance;

        private InstantiateResult(String msg, Instance instance) {
            this.message = msg;
            this.instance = instance;
        }
    }

    public InstantiateResult instantiate(Character owner) {
        return new InstantiateResult(this.setup(owner), new Instance(this));
    }

    public void setStrength(Character user) {
        this.strength = user.getLevel();
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    @Override
    public final String toString() {
        return getName();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj != null && getName().equals(obj.toString());
    }
    
    public Optional<Position> capitalize(Character attacker, Character victim, Instance instance) {
        return Optional.empty();
    }

    public String getName() {
        return name;
    }

}
