package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;

class BodyGrowthPlusEffect extends BodyModEffect {

    BodyGrowthPlusEffect(String selfVerb, String otherVerb, BodyPart affected) {
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
        } else {
            BodyPart newPart = original.upgrade();
            if (newPart == original) {
                boolean eventful = true;
                user.body.temporaryAddOrReplacePartWithType(
                    newPart,
                    original,
                    item.duration);
                message = eventful ?
                    Global.format(String.format("{self:NAME-POSSESSIVE} %s was reenforced",
                        original.fullDescribe(user)), user, opponent)
                    : "";
            } else {
                user.body.temporaryAddOrReplacePartWithType(newPart, original, item.duration);
                message = Global.format(
                    String.format("{self:NAME-POSSESSIVE} %s grew into %s%s",
                        original.fullDescribe(user),
                        newPart.prefix(),
                        newPart.fullDescribe(user)),
                    user,
                    opponent);
            }
        }
        if (c != null && !message.isEmpty()) {
            c.write(message);
        }
        return !message.isEmpty();
    }
}
