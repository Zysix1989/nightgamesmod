package nightgames.items;

import nightgames.characters.body.BodyPart;

public class BodyGrowthMultipleEffect extends BodyModEffect {
    public BodyGrowthMultipleEffect(String selfverb, String otherverb, BodyPart affected) {
        super(selfverb, otherverb, affected, Effect.growMultiple);
    }

    public BodyGrowthMultipleEffect(String selfverb, String otherverb, BodyPart affected, int duration) {
        super(selfverb, otherverb, affected, Effect.growMultiple, duration);
    }
}
