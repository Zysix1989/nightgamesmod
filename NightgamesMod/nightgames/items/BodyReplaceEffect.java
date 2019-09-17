package nightgames.items;

import nightgames.characters.body.BodyPart;

public class BodyReplaceEffect extends BodyModEffect {
    public BodyReplaceEffect(String selfverb, String otherverb, BodyPart affected) {
        super(selfverb, otherverb, affected, Effect.replace);
    }

    public BodyReplaceEffect(String selfverb, String otherverb, BodyPart affected, int duration) {
        super(selfverb, otherverb, affected, Effect.replace, duration);
    }
}
