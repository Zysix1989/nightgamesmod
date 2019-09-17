package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;

public class BodyGrowthPlusEffect extends BodyModEffect {

    public BodyGrowthPlusEffect(String selfverb, String otherverb, BodyPart affected) {
        super(selfverb, otherverb, affected, Effect.growplus);
    }

    public BodyGrowthPlusEffect(String selfverb, String otherverb, BodyPart affected, Effect effect, int duration) {
        super(selfverb, otherverb,affected,Effect.growplus, duration);
    }


    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        BodyPart original = user.body.getRandom(affected.getType());
        int duration = selfDuration >= 0 ? selfDuration : item.duration;

        String message;
        if (original == null) {
            user.body.temporaryAddPart(affected, duration);
            message = Global.format(String.format("{self:SUBJECT} grew %s",
                Global.prependPrefix(affected.prefix(), affected.fullDescribe(user))), user,
                opponent);
        } else {
            BodyPart newPart;
            if (affected.compare(original) <= 0) {
                newPart = original.upgrade();
            } else {
                newPart = affected;
            }
            if (newPart == original) {
                boolean eventful = user.body
                    .temporaryAddOrReplacePartWithType(newPart, original, duration);
                message = eventful ? Global
                    .format(String.format("{self:NAME-POSSESSIVE} %s was reenforced",
                        original.fullDescribe(user)), user, opponent) : "";
            } else {
                user.body.temporaryAddOrReplacePartWithType(newPart, original, duration);
                message = Global.format(String.format("{self:NAME-POSSESSIVE} %s grew into %s%s",
                    original.fullDescribe(user), newPart.prefix(), newPart.fullDescribe(user)),
                    user, opponent);
            }
        }
        if (c != null && !message.isEmpty()) {
            c.write(message);
        }
        return !message.isEmpty();
    }
}
