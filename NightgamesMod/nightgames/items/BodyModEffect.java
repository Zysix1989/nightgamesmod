package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;

public abstract class BodyModEffect extends ItemEffect {
    protected BodyPart affected;
    protected int selfDuration;

    public enum Effect {
        upgrade,
        downgrade,
        replace,
        grow,
        growMultiple,
        growplus,
    }

    public BodyModEffect(String selfverb, String otherverb, BodyPart affected) {
        this(selfverb, otherverb, affected, -1);
    }

    public BodyModEffect(String selfverb, String otherverb, BodyPart affected, int duration) {
        super(selfverb, otherverb, true, true);
        this.affected = affected;
        selfDuration = duration;
    }
}
