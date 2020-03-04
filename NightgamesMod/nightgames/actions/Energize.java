package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.global.Global;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Energized;
import nightgames.status.Stsflag;

public class Energize extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 75530820306364893L;

    public Energize() {
        super("Absorb Mana");
    }

    @Override
    public boolean usable(Participant user) {
        return user.getCharacter().get(Attribute.Arcane) >= 1
                && !user.getCharacter().is(Stsflag.energized)
                && !user.getCharacter().bound();
    }

    @Override
    public IMovement execute(Participant user) {
        if (user.getCharacter().human()) {
            Global.gui().message(
                            "You duck into the creative writing room and find a spellbook sitting out in the open. Aisha must have left it for you. The spellbook builds mana "
                                + "continuously and the first lesson you learned was how to siphon off the excess. You absorb as much as you can hold, until you're overflowing with mana.");
        }
        user.getCharacter().getMojo().build(user.getCharacter().getMojo().max());
        user.getCharacter().addNonCombat(new Status(new Energized(user.getCharacter(), 20)));
        return Movement.mana;
    }

}
