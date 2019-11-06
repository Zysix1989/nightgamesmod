package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;

class BodyGrowthEffect extends BodyModEffect {
    BodyGrowthEffect(String selfVerb, String otherVerb, BodyPart affected) {
        super(selfVerb, otherVerb, affected);
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        BodyPart original = user.body.getRandom(affected.getType());

        String message;

        if (original == affected) {
            user.body.setTemporaryPartDuration(affected, item.duration);
            message =
                Global.format(String.format("{self:NAME-POSSESSIVE} %s was reenforced",
                    original.fullDescribe(user)), user, opponent);
        } else {
            user.body.temporaryAddPart(affected, item.duration);
            message = Global.format(String.format("{self:SUBJECT} grew %s",
                Global.prependPrefix(affected.prefix(), affected.fullDescribe(user))), user,
                opponent);
        }

        if (c != null && !message.isEmpty()) {
            c.write(message);
        }
        return !message.isEmpty();
    }
}
