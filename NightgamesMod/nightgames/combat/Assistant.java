package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.Decider;
import nightgames.characters.Trait;
import nightgames.characters.WeightedSkill;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.pet.PetCharacter;
import nightgames.skills.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Assistant {
    private static final Set<SkillTag> PET_UNUSABLE_TAG = new HashSet<>();
    static {
        PET_UNUSABLE_TAG.add(SkillTag.suicidal);
        PET_UNUSABLE_TAG.add(SkillTag.petDisallowed);
        PET_UNUSABLE_TAG.add(SkillTag.counter);
    }

    private PetCharacter character;
    private Character master;

    Assistant(PetCharacter c, Character master) {
        this.character = c;
        this.master = master;
    }

    Assistant(Assistant a) {
        try {
            this.character = a.character.cloneWithOwner(a.master);
            this.master = a.master;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Assistant copy() {
        return new Assistant(this);
    }

    public PetCharacter getCharacter() {
        return this.character;
    }

    public Character getMaster() {
        return this.master;
    }

    public double getFitness() {
        return  (10 + character.getSelf().power()) * ((100 + character.percentHealth()) / 200.0) / 2;
    }

    public void vanquish(Combat c, Assistant other) {
        character.getSelf().vanquish(c, other.getCharacter().getSelf());
    }

    public boolean act(Combat c, Character target) {
        List<Skill> allowedEnemySkills = character.getSkills()
                .stream()
                .filter(skill -> Skill.isUsableOn(c, skill, target) &&
                        Collections.disjoint(skill.getTags(c), PET_UNUSABLE_TAG))
                .collect(Collectors.toList());
        Skill.filterAllowedSkills(c, allowedEnemySkills, character, target);

        List<Skill> allowedMasterSkills = character.getSkills()
                .stream()
                .filter(skill -> Skill.isUsableOn(c, skill, character.getSelf().owner) &&
                        (skill.getTags(c).contains(SkillTag.helping) ||
                                (character.getSelf().owner.has(Trait.showmanship) &&
                                        skill.getTags(c).contains(SkillTag.worship))) &&
                        Collections.disjoint(skill.getTags(c), PET_UNUSABLE_TAG))
                .collect(Collectors.toList());
        Skill.filterAllowedSkills(c, allowedMasterSkills, character, character.getSelf().owner);
        WeightedSkill bestEnemySkill = Decider.prioritizePet(character, target, allowedEnemySkills, c);
        WeightedSkill bestMasterSkill = Decider.prioritizePet(character, character.getSelf().owner, allowedMasterSkills, c);

        // don't let the ratings be negative.
        double masterSkillRating = Math.max(.001, bestMasterSkill.rating);
        double enemySkillRating = Math.max(.001, bestEnemySkill.rating);

        double roll = Global.randomdouble(masterSkillRating + enemySkillRating) - masterSkillRating;
        if (roll >= 0) {
            c.write(character, String.format("<b>%s uses %s against %s</b>\n", character.getTrueName(),
                    bestEnemySkill.skill.getLabel(c), target.nameDirectObject()));
            Skill.resolve(bestEnemySkill.skill, c, target);
        } else {
            c.write(character, String.format("<b>%s uses %s against %s</b>\n",
                    character.getTrueName(), bestMasterSkill.skill.getLabel(c), target.nameDirectObject()));
            Skill.resolve(bestMasterSkill.skill, c, character.self.owner());
        }
        return false;
    }
}
