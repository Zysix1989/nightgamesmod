package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.Sizable;
import nightgames.combat.Combat;
import nightgames.global.Global;

public class BodyDowngradeEffect extends BodyModEffect {
    BodyDowngradeEffect(String selfVerb, String otherVerb, BodyPart affected) {
        super(selfVerb, otherVerb, affected);
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        BodyPart original = user.body.getRandom(affected.getType());

        String message;
        if (original instanceof Sizable) {
            var sz = (Sizable) original;
            var startingSize = sz.getSize();
            var originalDescription = original.fullDescribe(user);
            sz.temporarilyChangeSize(-1, item.duration);
            var finalSize = sz.getSize();
            switch (finalSize.compareTo(startingSize)) {
                case 0:
                    message = Global
                        .format(String.format("{self:NAME-POSSESSIVE} %s was reinforced",
                            original.fullDescribe(user)), user, opponent);
                    break;
                case 1:
                    message = Global.format(
                        String.format("{self:NAME-POSSESSIVE} %s shrank into %s%s",
                            originalDescription,
                            original.prefix(),
                            original.fullDescribe(user)),
                        user,
                        opponent);
                    break;
                default:
                    throw new RuntimeException("Downgrading did not result in an downgrade");
            }
        } else if (original != null) {
            throw new RuntimeException("unreachable");
        } else {
            message = "";
        }
        if (c != null && !message.isEmpty()) {
            c.write(message);
        }
        return !message.isEmpty();
    }
}
