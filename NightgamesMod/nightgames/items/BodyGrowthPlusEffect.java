package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.Sizable;
import nightgames.combat.Combat;
import nightgames.global.Global;

class BodyGrowthPlusEffect extends BodyModEffect {

    BodyGrowthPlusEffect(String selfVerb, String otherVerb, GenericBodyPart affected) {
        super(selfVerb, otherVerb, affected);
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        BodyPart original = user.body.getRandom(affected.getType());

        String message;
        if (original == null) {
            user.body.temporaryAddPart(affected, item.duration);
            message = Global.format(String.format("{self:SUBJECT} grew %s",
                Global.prependPrefix(affected.prefix(), affected.fullDescribe(user))), user,
                opponent);
        } else if (original instanceof Sizable) {
            var sz = (Sizable) original;
            var startingSize = sz.getSize();
            var originalDescription = original.fullDescribe(user);
            sz.temporarilyChangeSize(1, item.duration);
            var finalSize = sz.getSize();
            switch (finalSize.compareTo(startingSize)) {
                case 0:
                    message = Global.format(String.format("{self:NAME-POSSESSIVE} %s was reinforced",
                        original.fullDescribe(user)), user, opponent);
                    break;
                case 1:
                    message = Global.format(
                        String.format("{self:NAME-POSSESSIVE} %s grew into %s%s",
                            originalDescription,
                            original.prefix(),
                            original.fullDescribe(user)),
                        user,
                        opponent);
                    break;
                default:
                    throw new RuntimeException("Upgrading did not result in an upgrade");
            }
        } else {
            throw new RuntimeException("unreachable");
        }
        if (c != null && !message.isEmpty()) {
            c.write(message);
        }
        return !message.isEmpty();
    }
}
