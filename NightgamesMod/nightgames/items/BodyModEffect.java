package nightgames.items;

import nightgames.characters.body.GenericBodyPart;

abstract class BodyModEffect extends ItemEffect {
    protected GenericBodyPart affected;

    BodyModEffect(String selfVerb, String otherVerb, GenericBodyPart affected) {
        super(selfVerb, otherVerb, true, true);
        this.affected = affected;
    }
}
