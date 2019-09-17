package nightgames.items;

import nightgames.characters.body.BodyPart;

abstract class BodyModEffect extends ItemEffect {
    protected BodyPart affected;
    protected int selfDuration;

    public BodyModEffect(String selfVerb, String otherVerb, BodyPart affected) {
        this(selfVerb, otherVerb, affected, -1);
    }

    public BodyModEffect(String selfVerb, String otherVerb, BodyPart affected, int duration) {
        super(selfVerb, otherVerb, true, true);
        this.affected = affected;
        selfDuration = duration;
    }
}
