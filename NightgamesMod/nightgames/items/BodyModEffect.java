package nightgames.items;

import nightgames.characters.body.BodyPart;

abstract class BodyModEffect extends ItemEffect {
    protected BodyPart affected;

    BodyModEffect(String selfVerb, String otherVerb, BodyPart affected) {
        super(selfVerb, otherVerb, true, true);
        this.affected = affected;
    }
}
