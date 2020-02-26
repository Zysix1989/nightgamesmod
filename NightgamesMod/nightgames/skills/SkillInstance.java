package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;

import java.util.Collection;

public class SkillInstance {
    private Skill skill;
    private Combat combat;
    private Character target;

    SkillInstance(Skill s, Combat c, Character target) {
        this.skill = s;
        this.combat = c;
        this.target = target;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getAccuracy() {
        return target.getChanceToHit(skill.getSelf(), combat, skill.accuracy(combat, target));
    }

    public String getLabel() {
        return skill.getLabel(combat);
    }

    public String getDescription() {
        return skill.describe(combat);
    }

    public int getMojoBuilt() {
        return skill.getMojoBuilt(combat);
    }

    public int getMojoCost() {
        return skill.getMojoCost(combat);
    }

    public int getCooldownRemaining() {
        if (skill.user().cooldownAvailable(skill)) {
            return 0;
        }
        return skill.user().getCooldown(skill);
    }

    public Tactics getType() {
        return skill.type(combat);
    }

    public Collection<String> getChoices() {
        return skill.subChoices(combat);
    }

    public void fire() {
        combat.act(skill.user(), skill);
        combat.resume();
    }

    public void fire(String choice) {
        skill.setChoice(choice);
        fire();
    }
}
