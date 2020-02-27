package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.BD;
import nightgames.status.Stsflag;

public class GoblinBondage extends SimpleEnemySkill {
    public GoblinBondage(Character self) {
        super("Goblin Bondage", self);
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
        c.write(getSelf(), Global.format("{self:SUBJECT} pulls the bondage straps tighter around herself. You can see the leather and latex digging into her skin as "
                        + "her bondage fascinatation begins to affect both of you.", getSelf(), target));
        getSelf().pain(c, getSelf(), 10);
        c.getP1Character().add(c, new BD(c.getP1Character()));
        c.getP2Character().add(c, new BD(c.getP2Character()));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new GoblinBondage(user);
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
