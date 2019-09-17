package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
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
        if (original != null) {
            BodyPart newPart = original.downgrade();
            if (newPart == original) {
                boolean eventful = user.body
                    .temporaryAddOrReplacePartWithType(newPart, original, item.duration);
                message = eventful ? Global
                    .format(String.format("{self:NAME-POSSESSIVE} %s was reenforced",
                        original.fullDescribe(user)), user, opponent) : "";
            } else {
                user.body.temporaryAddOrReplacePartWithType(newPart, original, item.duration);
                message = Global.format(
                    String.format("{self:NAME-POSSESSIVE} %s shrunk into %s",
                        original.fullDescribe(user), Global.prependPrefix(
                            newPart.prefix(), newPart.fullDescribe(user))),
                    user, opponent);
            }
        } else {
            message = "";
        }
       if (c != null && !message.isEmpty()) {
            c.write(message);
        }
        return !message.isEmpty();
    }
}
