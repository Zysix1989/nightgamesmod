package nightgames.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.global.Global;
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
    public boolean usable(Character user) {
        return user.get(Attribute.Arcane) >= 1 && !user.is(Stsflag.energized) && !user.bound();
    }

    @Override
    public IMovement execute(Character user) {
        if (user.human()) {
            Global.gui().message(
                            "You duck into the creative writing room and find a spellbook sitting out in the open. Aisha must have left it for you. The spellbook builds mana "
                                + "continuously and the first lesson you learned was how to siphon off the excess. You absorb as much as you can hold, until you're overflowing with mana.");
        }
        user.getMojo().build(user.getMojo().max());
        user.addNonCombat(new Energized(user, 20));
        return Movement.mana;
    }

    @Override
    public IMovement consider() {
        return Movement.mana;
    }

}
