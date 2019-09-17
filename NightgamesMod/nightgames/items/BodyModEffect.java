package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;

public abstract class BodyModEffect extends ItemEffect {
    protected BodyPart affected;
    private Effect effect;
    protected int selfDuration;

    public enum Effect {
        upgrade,
        downgrade,
        replace,
        grow,
        growMultiple,
        growplus,
    }

    public BodyModEffect(String selfverb, String otherverb, BodyPart affected, Effect effect) {
        this(selfverb, otherverb, affected, effect, -1);
    }

    public BodyModEffect(String selfverb, String otherverb, BodyPart affected, Effect effect, int duration) {
        super(selfverb, otherverb, true, true);
        this.affected = affected;
        this.effect = effect;
        selfDuration = duration;
    }
}
