package nightgames.skills;

import java.util.Set;

public class SkillGroup {
    public Tactics tactics;
    public Set<SkillInstance> skills;

    public SkillGroup(Tactics tactics, Set<SkillInstance> skills) {
        this.tactics = tactics;
        this.skills = skills;
    }
}
