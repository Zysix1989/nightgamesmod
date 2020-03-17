package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.Decider;
import nightgames.characters.Trait;
import nightgames.characters.WeightedSkill;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.pet.PetCharacter;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.skills.Wait;

import java.util.*;
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
        WeightedSkill bestEnemySkill = prioritizePet(target, allowedEnemySkills, c);
        WeightedSkill bestMasterSkill = prioritizePet(character.getSelf().owner, allowedMasterSkills, c);

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

    /**Decides which weightedskill a summoned pet uses*/
    public WeightedSkill prioritizePet(Character target, List<Skill> plist, Combat c) {
        List<WeightedSkill> weightedList = plist.stream().map(skill -> new WeightedSkill(1.0, skill)).collect(Collectors.toList());
        return prioritizePetWithWeights(character, target, weightedList, c);
    }

    public static WeightedSkill prioritizePetWithWeights(PetCharacter self, Character target, List<WeightedSkill> plist, Combat c) {
        if (plist.isEmpty()) {
            return new WeightedSkill(1.0, new Wait(self));
        }
        // The higher, the better the AI will plan for "rare" events better
        final int RUN_COUNT = 3;
        // Decrease to get an "easier" AI. Make negative to get a suicidal AI.
        final double RATING_FACTOR = 0.02f;

        // Starting fitness
        Character master = self.getSelf().owner();
        Character other = c.getOpponentCharacter(self);
        double masterFit = master.getFitness(c);
        double otherFit = master.getOtherFitness(c, other);

        // Now simulate the result of all actions
        ArrayList<WeightedSkill> moveList = new ArrayList<>();
        double sum = 0;

        for (WeightedSkill wskill : plist) {
            // Run it a couple of times
            double rating, raw_rating = 0;
            if (wskill.skill.type(c) == Tactics.damage && self.has(Trait.sadist)) {
                wskill.weight += 1.0;
            }
            for (int j = 0; j < RUN_COUNT; j++) {
                raw_rating += ratePetMove(self, wskill.skill, target, c, masterFit, otherFit);
            }

            // Sum up rating, add to map
            rating = (double) Math.pow(2, RATING_FACTOR * raw_rating + wskill.weight + wskill.skill.priorityMod(c)
                    + Global.getMatch().getCondition().getSkillModifier().encouragement(wskill.skill, c, self));
            sum += rating;
            moveList.add(new WeightedSkill(sum, raw_rating, rating, wskill.skill));
        }
        if (sum == 0 || moveList.size() == 0) {
            return null;
        }
        // Select
        double s = Global.randomdouble() * sum;
        for (WeightedSkill entry : moveList) {
            if (entry.weight > s) {
                return entry;
            }
        }
        return moveList.get(moveList.size() - 1);
    }

    private static double ratePetMove(PetCharacter self, Skill skill, Character target, Combat c, double masterFit, double otherFit) {
        return Decider.rateActionWithObserver(self, self.getSelf().owner(), target, c, masterFit, otherFit, (combat, selfCopy, other) -> {
            skill.setSelf(selfCopy);
            skill.resolve(combat, other);
            skill.setSelf(self);
            return true;
        });
    }
}
