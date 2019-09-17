package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;


class BodyGrowthMultipleEffect extends BodyModEffect {
    private int selfDuration;

    BodyGrowthMultipleEffect(String selfVerb, String otherVerb, BodyPart affected, int duration) {
        super(selfVerb, otherVerb, affected);
        selfDuration = duration;
    }


    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        String message;
        user.body.temporaryAddPart(affected, selfDuration);
        message = Global.format(
            String.format("{self:SUBJECT} grew %s",
                Global.prependPrefix(affected.prefix(), affected.fullDescribe(user))),
            user, opponent);
        if (c != null && !message.isEmpty()) {
            c.write(message);
        }
        return !message.isEmpty();
    }
}
