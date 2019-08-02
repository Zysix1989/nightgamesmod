package nightgames.skills;

import java.util.HashSet;

public class SkillGroup {
    public Tactics tactics;
    public HashSet<Skill> skills;

    public SkillGroup(Tactics tactics, HashSet<Skill> skills) {
        this.tactics = tactics;
        this.skills = skills;
    }
}
