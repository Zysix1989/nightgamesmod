package nightgames.modifier.skill;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.modifier.ModifierCategory;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SkillModifier implements ModifierCategory<SkillModifier> {

    public Set<Skill> bannedSkills() {
        return Collections.emptySet();
    }

    public Set<Tactics> bannedTactics() {
        return Collections.emptySet();
    }

    public Map<Skill, Double> encouragedSkills() {
        return Collections.emptyMap();
    }

    public boolean playerOnly() {
        return true;
    }

    public Set<Skill> allowedSkills(Combat c) {
        Set<Skill> skills = new HashSet<>(Global.getSkillPool());
        skills.removeIf(s -> bannedSkills().contains(s));
        skills.removeIf(s -> bannedTactics().contains(s.type(c)));
        return skills;
    }

    public boolean allowedSkill(Combat c, Skill s) {
        return !(bannedSkills().contains(s) || bannedTactics().contains(s.type(c)));
    }

    public double encouragement(Skill s, Combat c, Character user) {
        return encouragedSkills().getOrDefault(s, 0.0);
    }

    @Override
    public abstract String toString();

}
