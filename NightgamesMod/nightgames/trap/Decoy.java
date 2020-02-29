package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;

import java.util.Map;

public class Decoy extends Trap {
    private static class Instance extends Trap.Instance {
        public Instance(Trap self) {
            super(self);
        }

        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                Global.gui().message(
                        "You follow the noise you've been hearing for a while, which turns out to be coming from a disposable cell phone. Seems like someone "
                                + "is playing a trick and you fell for it. You shut off the phone and toss it aside.");
            } else if (target.getCharacter().location().humanPresent()) {
                Global.gui().message(target.getCharacter().getName() + " finds the decoy phone and deactivates it.");
            }
            target.getCharacter().location().remove(this);
        }
    }

    public Decoy() {
        this(null);
    }
    
    public Decoy(Character owner) {
        super("Decoy", owner);
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Phone, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.get(Attribute.Cunning) >= 6 && !owner.has(Trait.direct);
    }

    @Override
    public String setup(Character owner) {
        basicSetup(owner);
        return "Your program a phone to play a prerecorded audio track five minutes from now. It should be noticable " +
                "from a reasonable distance until someone switches it off.";
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        return new InstantiateResult(this.setup(owner), new Instance(this));
    }
}
