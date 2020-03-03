package nightgames.skills.strategy;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Nurse;
import nightgames.skills.Skill;

public class NurseStrategy extends KnockdownThenActionStrategy {
    @Override
    public double weight(Combat c, Character self) {
        double weight = .5;
        if (self.has(Trait.lactating)) {
            weight *= 2;
        }
        if (self.has(Trait.magicmilk)) {
            weight *= 2;
        }
        if (self.getMood().equals(Emotion.angry) || self.getMood().equals(Emotion.nervous)) {
            weight *= .2;
        }
        if (!(new Nurse(self)).requirements(c, self, c.getOpponentCharacter(self))) {
            weight = 0;
        }
        return weight;
    }
    /*
    @Override
    protected Set<Skill> filterSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        Character other = c.getOpponent(self);
        
        Optional<Set<Skill>> preferredSkills = getPreferredSkills(c, self, allowedSkills);

        if (preferredSkills.isPresent()) {
            return preferredSkills.get();
        }

        Set<SkillTag> positioningTags = new HashSet<>();
        positioningTags.add(SkillTag.staminaDamage);
        positioningTags.add(SkillTag.positioning);

        Set<Skill> positioningSkills = allowedSkills.stream()
                        .filter(skill -> positioningTags.stream().anyMatch(tag -> skill.getTags().contains(tag)))
                        .filter(skill -> !skill.getTags().contains(SkillTag.suicidal))
                        .collect(Collectors.toSet());
        if (!c.getStance().mobile(self) || c.getStance().mobile(other)) {
            return positioningSkills;
        }
        return getPreferredAfterKnockdownSkills(c, self, allowedSkills).orElse(Collections.emptySet());
    }*/

    @Override
    protected Optional<Set<Skill>> getPreferredSkills(Combat c, Character self, Set<Skill> allowedSkills) {
        return emptyIfSetEmpty(allowedSkills.stream()
                        .filter(skill -> skill.getTags(c).contains(SkillTag.breastfeed)
                                        && !skill.getTags(c).contains(SkillTag.suicidal))
                        .collect(Collectors.toSet()));
    }
    
    @Override
    public CombatStrategy instance() {
        return new NurseStrategy();
    }

    @Override
    public int initialDuration(Combat c, Character self) {
        return Global.random(4, 7);
    }
}