package nightgames.items;

import nightgames.characters.body.BodyPart;

public class BodyDowngradeEffect extends BodyModEffect {
    public BodyDowngradeEffect(String selfverb, String otherverb, BodyPart affected) {
        super(selfverb, otherverb, affected, Effect.downgrade);
    }

    public BodyDowngradeEffect(String selfverb, String otherverb, BodyPart affected, int duration) {
        super(selfverb, otherverb, affected, Effect.downgrade, duration);
    }
}
