package nightgames.match.actions;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.match.Action;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Energized;
import nightgames.status.Stsflag;

public class Energize extends Action {
    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return String.format(" doing something with a large book. When %s's finished, you can see a sort of aura coming from %s.", c.pronoun(), c.possessiveAdjective());
        }
    }

    public final class Instance extends Action.Instance {
        public final Action self;

        private Instance(Action self, Participant user) {
            super(user);
            this.self = self;
        }

        @Override
        public Action.Aftermath execute() {
            return executeOuter(user);
        }
    }

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
    public Instance newInstance(Participant user) {
        return new Instance(this, user);
    }

    @Override
    public Action.Aftermath executeOuter(Participant user) {
        user.getCharacter().message("You duck into the creative writing room and find a spellbook sitting out " +
                "in the open. Aisha must have left it for you. The spellbook builds mana continuously and the " +
                "first lesson you learned was how to siphon off the excess. You absorb as much as you can hold, " +
                "until you're overflowing with mana.");
        user.getCharacter().getMojo().build(user.getCharacter().getMojo().max());
        user.getCharacter().addNonCombat(new Status(new Energized(user.getCharacter(), 20)));
        return new Aftermath();
    }

}
