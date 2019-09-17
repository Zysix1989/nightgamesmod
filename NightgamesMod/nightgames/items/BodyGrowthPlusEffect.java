package nightgames.items;

import nightgames.characters.body.BodyPart;

public class BodyGrowthPlusEffect extends BodyModEffect {

    public BodyGrowthPlusEffect(String selfverb, String otherverb, BodyPart affected) {
        super(selfverb, otherverb, affected, Effect.growplus);
    }

    public BodyGrowthPlusEffect(String selfverb, String otherverb, BodyPart affected, Effect effect, int duration) {
        super(selfverb, otherverb,affected,Effect.growplus, duration);
    }
}
