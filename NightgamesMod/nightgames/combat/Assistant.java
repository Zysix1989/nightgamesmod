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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Assistant {
    private static final Set<SkillTag> PET_UNUSABLE_TAG = new HashSet<>();
    static {
        PET_UNUSABLE_TAG.add(SkillTag.suicidal);
        PET_UNUSABLE_TAG.add(SkillTag.petDisallowed);
        PET_UNUSABLE_TAG.add(SkillTag.counter);
    }

    private final PetCharacter character;
    private final Character master;

    Assistant(PetCharacter c, Character master) {
        this.character = c;
        this.master = master;
    }

    Assistant(Assistant a, Character newMaster) {
        try {
            this.character = a.character.cloneWithOwner(newMaster);
            this.master = newMaster;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Assistant copy(Character newMaster) {
        return new Assistant(this, newMaster);
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
        WeightedSkill bestEnemySkill = prioritizePet(target,
                skill -> Skill.isUsableOn(c, skill, target) &&
                        Collections.disjoint(skill.getTags(c), PET_UNUSABLE_TAG), c);
        WeightedSkill bestMasterSkill = prioritizePet(character.getSelf().owner,
                skill -> Skill.isUsableOn(c, skill, character.getSelf().owner) &&
                        (skill.getTags(c).contains(SkillTag.helping) ||
                                (character.getSelf().owner.has(Trait.showmanship) &&
                                        skill.getTags(c).contains(SkillTag.worship)) &&
                                        Collections.disjoint(skill.getTags(c), PET_UNUSABLE_TAG)),
                c);

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
    public WeightedSkill prioritizePet(Character target, Predicate<Skill> filter, Combat c) {
        List<Skill> plist = character.getSkills()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
        Skill.filterAllowedSkills(c, plist, character, target);
        List<WeightedSkill> weightedList = plist.stream().map(skill -> new WeightedSkill(1.0, skill)).collect(Collectors.toList());
        if (weightedList.isEmpty()) {
            return new WeightedSkill(1.0, new Wait(character));
        }
        // The higher, the better the AI will plan for "rare" events better
        final int RUN_COUNT = 3;
        // Decrease to get an "easier" AI. Make negative to get a suicidal AI.
        final double RATING_FACTOR = 0.02f;

        // Starting fitness
        double masterFit = character.getSelf().owner().getFitness(c);
        double otherFit = character.getSelf().owner().getOtherFitness(c, c.getOpponentCharacter(character));

        // Now simulate the result of all actions
        ArrayList<WeightedSkill> moveList = new ArrayList<>();
        double sum = 0;

        for (WeightedSkill wskill : weightedList) {
            // Run it a couple of times
            double rating, raw_rating = 0;
            if (wskill.skill.type(c) == Tactics.damage && character.has(Trait.sadist)) {
                wskill.weight += 1.0;
            }
            for (int j = 0; j < RUN_COUNT; j++) {
                raw_rating += Decider.rateActionWithObserver(character,
                        character.getSelf().owner(),
                        target,
                        c,
                        masterFit,
                        otherFit,
                        (combat, selfCopy, other1) -> {
                            wskill.skill.setSelf(selfCopy);
                            wskill.skill.resolve(combat, other1);
                            wskill.skill.setSelf(character);
                            return true;
                        });
            }

            // Sum up rating, add to map
            rating = Math.pow(2,
                    RATING_FACTOR * raw_rating +
                            wskill.weight +
                            wskill.skill.priorityMod(c) +
                            Global.getMatch().getCondition().getSkillModifier().encouragement(wskill.skill,
                                    c,
                                    character));
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

}
