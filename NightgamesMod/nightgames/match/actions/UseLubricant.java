package nightgames.match.actions;

import nightgames.characters.Character;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Oiled;

public class UseLubricant extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return String.format(" rubbing body oil on every inch of %s skin. Wow, you wouldn't mind watching that again.", c.possessiveAdjective());
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

    public UseLubricant() {
        super("Oil up");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public Instance newInstance(Participant user) {
        return new Instance(this, user);
    }

    @Override
    public Action.Aftermath executeOuter(Participant user) {
        user.getCharacter().message("You cover yourself in slick oil. It's a weird feeling, but it should make " +
                "it easier to escape from a hold.");
        user.getCharacter().addNonCombat(new Status(new Oiled(user.getCharacter())));
        user.getCharacter().consume(Item.Lubricant, 1);
        return new Aftermath();
    }

}
