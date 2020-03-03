package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.Map;

public class Decoy extends Trap {
    private static class Instance extends Trap.Instance {

        public Instance(Trap self, Participant owner) {
            super(self, owner);
        }

        private static final String VICTIM_TRIGGER_MESSAGE = "You follow the noise you've been hearing for a while, " +
                "which turns out to be coming from a disposable cell phone. Seems like someone is playing a trick " +
                "and you fell for it. You shut off the phone and toss it aside.";
        private static final JtwigTemplate OWNER_TRIGGER_TEMPLATE = JtwigTemplate.inlineTemplate(
                "{{ victim.subject().defaultNoun() }} finds the decoy phone and deactivates it.");
        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                Global.gui().message(VICTIM_TRIGGER_MESSAGE);
            } else if (target.getCharacter().location().humanPresent()) {
                var model = JtwigModel.newModel()
                        .with("victim", target.getCharacter().getGrammar());
                Global.gui().message(OWNER_TRIGGER_TEMPLATE.render(model));
            }
            target.getCharacter().location().clearTrap();
        }
    }
    
    public Decoy() {
        super("Decoy");
    }

    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Phone, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean requirements(Participant user) {
        return user.getCharacter().get(Attribute.Cunning) >= 6 && !user.getCharacter().has(Trait.direct);
    }

    @Override
    public InstantiateResult instantiate(Participant owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(CREATION_MESSAGE, new Instance(this, owner));
    }

    private static final String CREATION_MESSAGE = "Your program a phone to play a prerecorded audio track five " +
            "minutes from now. It should be noticeable from a reasonable distance until someone switches it off.";
}
