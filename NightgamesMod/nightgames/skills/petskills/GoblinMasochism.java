package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.Masochistic;
import nightgames.status.Stsflag;

public class GoblinMasochism extends SimpleEnemySkill {
    public GoblinMasochism(Character self) {
        super("Goblin Masochism", self);
        addTag(SkillTag.debuff);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return super.usable(c, target) && !target.is(Stsflag.masochism);
    }

    @Override
    public int getMojoCost(Combat c) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        c.write(getSelf(), Global.format("{self:SUBJECT} draws a riding crop and hits her own balls with it. She shivers with delight at the pain and both of you can "
                        + "feel an aura of masochism radiate off her.", getSelf(), target));
        getSelf().pain(c, getSelf(), 10);
        c.getP1Character().add(c, new Masochistic(c.getP1Character()));
        c.getP2Character().add(c, new Masochistic(c.getP2Character()));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new GoblinMasochism(user);
    }

    @Override
    public int speed() {
        return 8;
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public boolean makesContact(Combat c) {
        return false;
    }
}
