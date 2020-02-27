package nightgames.trap;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingSlot;
import nightgames.match.Participant;
import nightgames.stance.Position;
import nightgames.status.RemoteMasturbation;

import java.util.Optional;

public class RemoteControl extends Trap {

    public RemoteControl(Character owner) {
        super("Remote Control", owner);
    }

    @Override
    protected void trigger(Participant target) {
        if (target.getCharacter().human()) {
            String msg = "You see a small, oblong object laying on the floor,"
                            + " and bend over to pick it up. It's black and shiny, but has no"
                            + " real discernable features. Suddenly a ring of light appears around"
                            + " the thing, and you freeze in place and hear " + owner.nameOrPossessivePronoun()
                            + " voice in your mind." + " <i>\"Is someone there? Who did I catch? Ah, it's you, "
                            + target.getCharacter().getName() + "! Wonderful!\"</i> Without warning, the hand not holding the weird device"
                            + " flies down towards your crotch and";
            if (!target.getCharacter().outfit.slotOpen(ClothingSlot.bottom)) {
                msg += ", first removing all the clothing covering your nethers,";
                target.getCharacter().outfit.undressOnly(c -> c.getSlots()
                                                .contains(ClothingSlot.bottom));
            }
            String otherHand;
            if (target.getCharacter().hasDick()) {
                msg += " grabs hold of your " + target.getCharacter().body.getRandomCock()
                                                           .describe(target.getCharacter())
                                + ". You try to stop, try to let go of the black thing, but"
                                + " you don't seem to have any control at all. The hand on your"
                                + " cock moves deftly, but not in the way you would when"
                                + " masturbating. It's definitely effective, though. You"
                                + " don't think you're going to last too long doing this.";
                otherHand = "wrapped around your cock, pumping it intently.";
            } else if (target.getCharacter().hasPussy()) {
                msg += " strokes the outside of your " + target.getCharacter().body.getRandomPussy()
                                                                    .describe(target.getCharacter())
                                + ". " + owner.nameOrPossessivePronoun() + " experienced, feather-light"
                                + " touch soon gets you lubricated enough to allow your fingers passage"
                                + " deeper into your folds and the hole in their center. You gasp as "
                                + owner.subject() + ", by proxy, rubs your clit with 'your'"
                                + " thumb while probing your pussy with delicate thrusts of 'your' fingers.";
                otherHand = "between your thighs, working dilligently on your pussy.";
            } else {
                msg += " finds nothing there. <i>\"Oh, right. I forgot. You're really missing out," + target.getCharacter().getName()
                                + ", you ought to do something about that. Still, this"
                                + " doesn't mean there is </i>nothing<i> we can do...\"</i>"
                                + " Your errant limb bends back up, and you involuntarily wet a finger."
                                + " It then moves down behind your back, finding your ass. After a few"
                                + " probing touches, it plunges in and starts massaging your insides.";
                otherHand = "buried between your asscheeks, with a finger up your bottom.";
            }
            msg += " \"<i>I'm on my way to you now. Try not to cum before I get there, alright? The Remote Control"
                            + " is not very good at measuring how far along you are. See you soon!</i>\""
                            + " Your mind goes silent again, but your body is still out of your control,"
                            + " one hand holding the 'Remote Control', as it is appearantly called,"
                            + " the other " + otherHand;
            Global.gui().message(msg);
        } else {

        }
        target.getCharacter().addNonCombat(new RemoteMasturbation(target.getCharacter(), owner));
        target.getCharacter().location().opportunity(target.getCharacter(), this);
        target.getCharacter().location().alarm = true;
    }

    @Override
    public boolean recipe(Character owner) {
        return owner.has(Item.RemoteControl);
    }

    @Override
    public boolean requirements(Character owner) {
        return owner.has(Trait.RemoteControl);
    }

    @Override
    public String setup(Character owner) {
        this.owner = owner;
        owner.consume(Item.RemoteControl, 1);
        return "<b>RemoteControl setup text - should not be displayed.</b>";
    }
    
    @Override
    public Optional<Position> capitalize(Character attacker, Character victim) {
        attacker.location().remove(this);
        return super.capitalize(attacker, victim);
    }

}
