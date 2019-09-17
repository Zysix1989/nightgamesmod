package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;

public class BodyReplaceEffect extends BodyModEffect {
    public BodyReplaceEffect(String selfverb, String otherverb, BodyPart affected) {
        super(selfverb, otherverb, affected);
    }

    public BodyReplaceEffect(String selfverb, String otherverb, BodyPart affected, int duration) {
        super(selfverb, otherverb, affected, duration);
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        BodyPart original = user.body.getRandom(affected.getType());
        int duration = selfDuration >= 0 ? selfDuration : item.duration;

        String message;

        if (original == affected) {
            boolean eventful = user.body
                .temporaryAddOrReplacePartWithType(affected, original, duration);
            message =
                eventful ? Global.format(String.format("{self:NAME-POSSESSIVE} %s was reenforced",
                    original.fullDescribe(user)), user, opponent) : "";
        } else if (original != null) {
            user.body.temporaryAddOrReplacePartWithType(affected, original, duration);
            message = Global.format(String.format("{self:NAME-POSSESSIVE} %s turned into %s",
                original.fullDescribe(user),
                Global.prependPrefix(affected.prefix(), affected.fullDescribe(user))), user,
                opponent);
        } else {
            user.body.temporaryAddPart(affected, duration);
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
