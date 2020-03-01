package nightgames.trap;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.stance.Position;

import java.util.Map;
import java.util.Optional;

public class Spiderweb extends Trap {
    private static class Instance extends Trap.Instance {
        private int strength;

        public Instance(Trap self, Character owner) {
            super(self, owner);
            strength = owner.get(Attribute.Cunning) + owner.get(Attribute.Science) + owner.getLevel() / 2;
        }

        @Override
        public void trigger(Participant target) {
            if (target.getCharacter().human()) {
                if (target.getCharacter().mostlyNude()) {
                    Global.gui().message(
                            "You feel the tripwire underfoot too late to avoid it. A staggering amount of rope flies up to entangle your limbs and pull you off the ground. "
                                    + "Oh hell. You're completely immobilized and suspended in midair. Surprisingly, it's not that uncomfortable, but if someone finds you before you can get free, "
                                    + "you'll be completely defenseless.");
                } else {
                    Global.gui().message(
                            "You feel the tripwire underfoot too late to avoid it. A staggering amount of rope flies up to entangle your limbs and pull you off the ground. "
                                    + "Something snags your clothes and pulls them off of you with unbelievable precision."
                                    + "Oh hell. You're completely immobilized and suspended naked in midair. Surprisingly, it's not that uncomfortable, but if someone finds you before you can get free, "
                                    + "you'll be completely defenseless.");
                }
            } else if (target.getCharacter().location().humanPresent()) {
                Global.gui().message("You hear a snap as " + target.getCharacter().getName()
                        + " triggers your spiderweb trap and ends up helplessly suspended in midair like a naked present.");
            }
            target.getCharacter().state = State.webbed;
            target.getCharacter().delay(1);
            target.getCharacter().location().opportunity(target.getCharacter(), this);
        }

        @Override
        public Optional<Position> capitalize(Character attacker, Character victim) {
            onSpiderwebDefeat(attacker, victim, this);
            return super.capitalize(attacker, victim);
        }
    }
    
    public Spiderweb() {
        super("Spiderweb");
    }
    private static final Map<Item, Integer> REQUIRED_ITEMS = Map.of(Item.Rope, 4,
            Item.Spring, 2,
            Item.Tripwire, 1);

    protected Map<Item, Integer> requiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.has(Trait.spider) && !owner.has(Trait.roboweb);
    }

    @Override
    public String instanceCreationMessage(Character owner) {
        return "With quite a bit of time and effort, you carefully setup a complex series of spring loaded snares. " +
                "Anyone who gets caught in this will be rendered as helpless as a fly in a web.";
    }

    @Override
    public InstantiateResult instantiate(Character owner) {
        deductCostsFrom(owner);
        return new InstantiateResult(this.instanceCreationMessage(owner), new Instance(this, owner));
    }
    
    public static void onSpiderwebDefeat(Character attacker, Character victim, Trap.Instance trap) {
        printSpiderwebLines(attacker, victim);

        // This code is identical to the encounter defeat code. If there are more use
        // cases like this it needs to be encapsulated somewhere more general.
        if (victim.mostlyNude()) {
            attacker.gain(victim.getTrophy());
        }
        victim.nudify();
        victim.defeated(attacker);
        victim.getArousal().renew();
        attacker.tempt(20);
        Global.getMatch().score(attacker,  1);
        attacker.state = State.ready;
        victim.state = State.ready;
        victim.location().endEncounter();
        victim.location().remove(trap);
    }
    
    private static void printSpiderwebLines(Character attacker, Character victim) {
        if (attacker.human()) {
            Global.gui().message(
                            victim.getName() + " is naked and helpless in the giant rope web. You approach slowly, taking in the lovely view of her body. You trail your fingers "
                                            + "down her front, settling between her legs to tease her sensitive pussy lips. She moans and squirms, but is completely unable to do anything in her own defense. "
                                            + "You are going to make her cum, that's just a given. If you weren't such a nice guy, you would leave her in that trap afterward to be everyone else's prey "
                                            + "instead of helping her down. You kiss and lick her neck, turning her on further. Her entrance is wet enough that you can easily work two fingers into her "
                                            + "and begin pumping. You gradually lick your way down her body, lingering at her nipples and bellybutton, until you find yourself eye level with her groin. "
                                            + "You can see her clitoris, swollen with arousal, practically begging to be touched. You trap the sensitive bud between your lips and attack it with your tongue. "
                                            + "The intense stimulation, coupled with your fingers inside her, quickly brings her to orgasm. While she's trying to regain her strength, you untie the ropes "
                                            + "binding her hands and feet and ease her out of the web.");
        } else if (victim.human()) {
            Global.gui().message("You're trying to figure out a way to free yourself, when you see " + attacker.getName()
                            + " approach. You groan in resignation. There's no way you're "
                            + "going to get free before she finishes you off. She smiles as she enjoys your vulnerable state. She grabs your dangling penis and puts it in her mouth, licking "
                            + "and sucking it until it's completely hard. Then the teasing starts. She strokes you, rubs you, and licks the head of your dick. She uses every technique to "
                            + "pleasure you, but stops just short of letting you ejaculate. It's maddening. Finally you have to swallow your pride and beg to cum. She pumps you dick in earnest "
                            + "now and fondles your balls. When you cum, you shoot your load onto her face and chest. You hang in the rope web, literally and figuratively drained. "
                            + attacker.getName() + " " + "gratiously unties you and helps you down.");
        }
    }

}
