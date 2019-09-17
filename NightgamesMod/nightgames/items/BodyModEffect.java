package nightgames.items;

import nightgames.characters.body.BodyPart;

abstract class BodyModEffect extends ItemEffect {
    protected BodyPart affected;
    protected int selfDuration;

    public BodyModEffect(String selfverb, String otherverb, BodyPart affected) {
        this(selfverb, otherverb, affected, -1);
    }

    public BodyModEffect(String selfverb, String otherverb, BodyPart affected, int duration) {
        super(selfverb, otherverb, true, true);
        this.affected = affected;
        selfDuration = duration;
    }
}
