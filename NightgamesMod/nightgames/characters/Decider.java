package nightgames.characters;

import nightgames.actions.*;
import nightgames.characters.custom.effect.CustomEffect;
import nightgames.combat.Combat;
import nightgames.daytime.Daytime;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.ftc.FTCMatch;
import nightgames.pet.PetCharacter;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.skills.Wait;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Decider {
    private static void addAllSkillsWithPriority(ArrayList<WeightedSkill> priority, HashSet<Skill> skills,
                    float weight) {
        for (Skill s : skills) {
            priority.add(new WeightedSkill(weight, s));
        }
    }

    /**Creates an ArrayList of weighted Skills to aid in the decisionmaking process. It goes through all available skills and sorts them into lists. 
     * 
     * The method then checks the character's current mood and assigns weights based upon skill type.
     * 
     * @param available
     * 
     * @param c
     * 
     * @param character
     * 
     * @return
     * Returns an ArrayList of weightedSkills.
     * 
     * */
    public static ArrayList<WeightedSkill> parseSkills(HashSet<Skill> available, Combat c, NPC character) {
        HashSet<Skill> damage = new HashSet<Skill>();
        HashSet<Skill> pleasure = new HashSet<Skill>();
        HashSet<Skill> fucking = new HashSet<Skill>();
        HashSet<Skill> position = new HashSet<Skill>();
        HashSet<Skill> debuff = new HashSet<Skill>();
        HashSet<Skill> recovery = new HashSet<Skill>();
        HashSet<Skill> calming = new HashSet<Skill>();
        HashSet<Skill> summoning = new HashSet<Skill>();
        HashSet<Skill> stripping = new HashSet<Skill>();
        HashSet<Skill> misc = new HashSet<Skill>();
        ArrayList<WeightedSkill> priority = new ArrayList<WeightedSkill>();
        for (Skill a : available) {
            if (a.type(c) == Tactics.damage) {
                damage.add(a);
            } else if (a.type(c) == Tactics.pleasure) {
                pleasure.add(a);
            } else if (a.type(c) == Tactics.fucking) {
                fucking.add(a);
            } else if (a.type(c) == Tactics.positioning) {
                position.add(a);
            } else if (a.type(c) == Tactics.debuff) {
                debuff.add(a);
            } else if (a.type(c) == Tactics.recovery) {
                recovery.add(a);
            } else if (a.type(c) == Tactics.calming) {
                calming.add(a);
            } else if (a.type(c) == Tactics.summoning) {
                summoning.add(a);
            } else if (a.type(c) == Tactics.stripping) {
                stripping.add(a);
            } else if (a.type(c) == Tactics.misc) {
                misc.add(a);
            }
        }
        switch (character.mood) {
            
            //Characters that are confident will gain position, strip and debuff the other guy - they do things equally.
            case confident:
                // i can do whatever i want
                addAllSkillsWithPriority(priority, position, 1.0f);
                addAllSkillsWithPriority(priority, stripping, 1.0f);
                addAllSkillsWithPriority(priority, debuff, 1.0f);
                addAllSkillsWithPriority(priority, pleasure, 1.0f);
                addAllSkillsWithPriority(priority, fucking, 1.0f);
                addAllSkillsWithPriority(priority, damage, 1.0f);
                addAllSkillsWithPriority(priority, summoning, .5f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
            //Characters that are angry will prioritize doing damage and gaining position.
            case angry:
                addAllSkillsWithPriority(priority, damage, 2.5f);
                addAllSkillsWithPriority(priority, position, 2.0f);
                addAllSkillsWithPriority(priority, debuff, 2.0f);
                addAllSkillsWithPriority(priority, stripping, 1.0f);
                addAllSkillsWithPriority(priority, pleasure, 0.0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                addAllSkillsWithPriority(priority, summoning, 0f);
                break;
            //Characters that are nervous prioritize summoning, debuffing, and recovering from bad situations.
            case nervous:
                addAllSkillsWithPriority(priority, summoning, 2.0f);
                addAllSkillsWithPriority(priority, debuff, 2.0f);
                addAllSkillsWithPriority(priority, calming, 2.0f);
                addAllSkillsWithPriority(priority, recovery, 2.0f);
                addAllSkillsWithPriority(priority, position, 1.0f);
                addAllSkillsWithPriority(priority, damage, .5f);
                addAllSkillsWithPriority(priority, pleasure, 0.0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
            //characters that are desperate will prioritize calming down and recovering.
            case desperate:
                // and probably a bit confused
                addAllSkillsWithPriority(priority, calming, 4.0f);
                addAllSkillsWithPriority(priority, recovery, 4.0f);
                addAllSkillsWithPriority(priority, debuff, 4.0f);
                addAllSkillsWithPriority(priority, misc, 3.0f);
                addAllSkillsWithPriority(priority, position, 2.0f);
                addAllSkillsWithPriority(priority, damage, 2.0f);
                addAllSkillsWithPriority(priority, pleasure, 1.0f);
                addAllSkillsWithPriority(priority, fucking, 1.0f);
                break;
            //characters that are horny will prioritize fucking and stripping, doing pleasure and gaining position.
            case horny:
                addAllSkillsWithPriority(priority, fucking, 5.0f);
                addAllSkillsWithPriority(priority, stripping, 1.0f);
                addAllSkillsWithPriority(priority, pleasure, 1.0f);
                addAllSkillsWithPriority(priority, position, 1.0f);
                addAllSkillsWithPriority(priority, debuff, 0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
            //Characters that feel dominant will prioritize gaining and keeping dominant positions and fucking and stripping.
            case dominant:
                addAllSkillsWithPriority(priority, position, 3.0f);
                addAllSkillsWithPriority(priority, fucking, 2.0f);
                addAllSkillsWithPriority(priority, stripping, 2.0f);
                addAllSkillsWithPriority(priority, pleasure, 2.0f);
                addAllSkillsWithPriority(priority, debuff, 2.0f);
                addAllSkillsWithPriority(priority, damage, 2.0f);
                addAllSkillsWithPriority(priority, summoning, 1.0f);
                addAllSkillsWithPriority(priority, misc, 1f);
                break;
        }
        /*
         * if(character.getArousal().percent()>85||character.getStamina().percent()<10){ priority.add(recovery); priority.add(damage); priority.add(pleasure); } if((c.stance.penetration(character)&&c.stance.dom(character))||c.stance.enumerate()==Stance.sixnine||(c.stance.dom(character)&&c.stance.enumerate()==Stance.behind)){ priority.add(pleasure); priority.add(pleasure); priority.add(damage); priority.add(recovery); } if(!target.canAct()){ priority.add(stripping); priority.add(pleasure); priority.add(summoning); priority.add(position); } else if(!target.nude()&&(target.getArousal().percent()>60||target.getStamina().percent()<50)){ priority.add(stripping); priority.add(pleasure); priority.add(damage); priority.add(position); } else if(c.stance.dom(character)){ priority.add(pleasure); priority.add(stripping); priority.add(summoning); priority.add(damage); priority.add(position); priority.add(debuff); } else{ priority.add(summoning); priority.add(pleasure); priority.add(debuff); priority.add(damage); priority.add(position); priority.add(stripping); priority.add(recovery); }
         */ return priority;
    }

    private static Optional<Action> searchForAction(Collection<Action> available, NPC character, Predicate<Action> predicate) {
        var action = available.stream().filter(predicate).findAny();
        if (action.isEmpty()) {
            var bestMove = Character.bestMove(character, character.location(), predicate);
            if (bestMove.isPresent()) {
                action = available.stream().filter(act -> act.equals(bestMove.get())).findAny();
            }
        }
        return action;
    }
    
    /**This method parses the actions available to the character and returns an action.*/
    public static Action parseMoves(Collection<Action> available, Collection<IMovement> radar, NPC character) {
        HashSet<Action> enemy = new HashSet<Action>();
        HashSet<Action> onlyWhenSafe = new HashSet<Action>();
        HashSet<Action> utility = new HashSet<Action>();
        HashSet<Action> tactic = new HashSet<Action>();
        if (character.mostlyNude()) {
            var resupplyAction = searchForAction(available, character, act -> act instanceof Resupply);
            if (resupplyAction.isPresent()) {
                return resupplyAction.get();
            }
        }
        if (character.getArousal().percent() >= 40 && !character.location().humanPresent() && radar.isEmpty()) {
            var masturbateAction = available.stream().filter(act -> act instanceof MasturbateAction).findAny();
            if (masturbateAction.isPresent()) {
                return masturbateAction.get();
            }
        }
        if (character.getStamina().percent() <= 60 || character.getArousal().percent() >= 30) {
            var batheAction = searchForAction(available, character, act -> act instanceof Bathe);
            if (batheAction.isPresent()) {
                return batheAction.get();
            }
        }
        if (character.get(Attribute.Science) >= 1 && !character.has(Item.Battery, 10)) {
            var rechargeAction = searchForAction(available, character, act -> act instanceof Recharge);
            if (rechargeAction.isPresent()) {
                return rechargeAction.get();
            }
        }
        FTCMatch match;
        if (Global.checkFlag(Flag.FTC)) {
            match = (FTCMatch) Global.getMatch();
            if (match.isPrey(character) && match.getFlagHolder() == null) {
                var action = searchForAction(available, character,
                        act -> act instanceof Move && ((Move) act).getDestination().name.equals("Central Camp"));
                if (action.isPresent()) {
                    return action.get();
                }
            } else if (!match.isPrey(character)
                    && character.has(Item.Flag)
                    && !match.isBase(character, character.location.get())) {
                var action = searchForAction(available, character,
                        act -> act instanceof Move && ((Move) act).getDestination().name.equals(match.getBase(character).name));
                if (action.isPresent()) {
                    return action.get();
                }
            } else if (!match.isPrey(character) && character.has(Item.Flag) && match.isBase(character, character.location.get())) {
                return searchForAction(available, character, act -> act instanceof Resupply)
                        .orElseThrow(() -> new RuntimeException("This is your base. There ought to be a resupply."));
            }
        }
        for (Action act : available) {
            if (radar.contains(act.consider())) {
                enemy.add(act);
            } else if (act instanceof Bathe
                    || act instanceof Craft
                    || act instanceof Scavenge
                    || act instanceof Hide
                    || act instanceof SetTrap
                    || act instanceof nightgames.actions.Wait
                    // TODO: The next two I do NOT understand
                    // If two weeks go by an I haven't figured out why they're here, remove them.
                    // Written 2020-03-03
                    || (act instanceof Move && ((Move) act).getDestination().name.equals("Engineering"))
                    || (act instanceof Move && ((Move) act).getDestination().name.equals("Dining"))
                    || act instanceof Disguise) {
                onlyWhenSafe.add(act);
            } else {
                utility.add(act);
            }
        }
        
        if (character.plan == Plan.hunting && !enemy.isEmpty()) {
            tactic.addAll(enemy);
        }
        if (!character.location().humanPresent()) {
            tactic.addAll(onlyWhenSafe);
        }
        tactic.addAll(utility);
        if (tactic.isEmpty()) {
            tactic.addAll(available);
        }
        // give disguise some priority when just picking something random
        if (tactic.stream().anyMatch(a -> a instanceof Disguise) && Global.random(5) == 0) {
            return tactic.stream().filter(a -> a instanceof Disguise).findFirst().get();
        }
        Action[] actions = tactic.toArray(new Action[tactic.size()]);
        return actions[Global.random(actions.length)];
    }

    /**This method determines what a character decides to do during the day.*/
    public static void visit(Character self) {
        if (Global.checkCharacterDisabledFlag(self)) {
            return;
        }
        int max = 0;
        Character bff = null;
        if (!self.attractions.isEmpty()) {
            for (String key : self.attractions.keySet()) {
                Character friend = Global.getCharacterByType(key);
                if (self.getAttraction(friend) > max && !friend.human()) {
                    max = self.getAttraction(friend);
                    bff = friend;
                }
            }
            if (bff != null) {
                self.gainAffection(bff, Global.random(3) + 1);
                bff.gainAffection(self, Global.random(3) + 1);
                switch (Global.random(3)) {
                    case 0:
                        Daytime.train(self, bff, Attribute.Power);
                    case 1:
                        Daytime.train(self, bff, Attribute.Cunning);
                    default:
                        Daytime.train(self, bff, Attribute.Seduction);
                }
            }
        }
    }

    /**Decides which weightedskill a summoned pet uses*/
    public static WeightedSkill prioritizePet(PetCharacter self, Character target, List<Skill> plist, Combat c) {
        List<WeightedSkill> weightedList = plist.stream().map(skill -> new WeightedSkill(1.0, skill)).collect(Collectors.toList());
        return prioritizePetWithWeights(self, target, weightedList, c);
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

    //TODO: Document this method. 
    public static Skill prioritizeNew(Character self, List<WeightedSkill> plist, Combat c) {
        if (plist.isEmpty()) {
            return null;
        }
        // The higher, the better the AI will plan for "rare" events better
        final int RUN_COUNT = 5;
        // Decrease to get an "easier" AI. Make negative to get a suicidal AI.
        final double RATING_FACTOR = 0.02f;

        // Starting fitness
        Character other = c.getOpponentCharacter(self);
        double selfFit = self.getFitness(c);
        double otherFit = self.getOtherFitness(c, other);

        // Now simulate the result of all actions
        ArrayList<WeightedSkill> moveList = new ArrayList<>();
        double sum = 0;
        for (WeightedSkill wskill : plist) {
            // Run it a couple of times
            double rating, raw_rating = 0;
            if (wskill.skill.type(c) == Tactics.fucking && self.has(Trait.experienced)) {
                wskill.weight += 1.0;
            }
            if (wskill.skill.type(c) == Tactics.damage && self.has(Trait.sadist)) {
                wskill.weight += 1.0;
            }
            for (int j = 0; j < RUN_COUNT; j++) {
                raw_rating += rateMove(self, wskill.skill, c, selfFit, otherFit);
            }

            if (self instanceof NPC) {
                wskill.weight += ((NPC)self).ai.getAiModifiers().modAttack(wskill.skill.getClass());
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
                return entry.skill;
            }
        }
        return moveList.get(moveList.size() - 1).skill;
    }

    private static double ratePetMove(PetCharacter self, Skill skill, Character target, Combat c, double masterFit, double otherFit) {
        return rateActionWithObserver(self, self.getSelf().owner(), target, c, masterFit, otherFit, (combat, selfCopy, other) -> {
            skill.setSelf(selfCopy);
            skill.resolve(combat, other);
            skill.setSelf(self);
            return true;
        });
    }

    //TODO: Document this method.
    private static double rateMove(Character self, Skill skill, Combat c, double selfFit, double otherFit) {
        return rateAction(self, c, selfFit, otherFit, (combat, selfCopy, other) -> {
            skill.setSelf(selfCopy);
            skill.resolve(combat, other);
            skill.setSelf(self);
            return true;
        });
    }

    //TODO: Document this method
    private static Character getCopyFromCombat(Combat c, Combat clonedCombat, Character self) {
        if (c.getP1Character() == self) {
            return clonedCombat.getP1Character();
        } else if (c.getP2Character() == self) {
            return clonedCombat.getP2Character();
        } else if (c.getOtherCombatants().contains(self)) {
            return clonedCombat.getOtherCombatants().stream().filter(other -> other.equals(self)).findAny().get();
        } else {
            throw new IllegalArgumentException("Tried to use a badly cloned combat");
        }
    }

    
    public static double rateAction(Character skillUser, Combat c, double selfFit, double otherFit, CustomEffect effect) {
        return rateActionWithObserver(skillUser, skillUser, c.getOpponentCharacter(skillUser), c, selfFit, otherFit, effect);
    }

    /**Clones the combat and returns a rating.
     * TODO: Document this method properly.
     * */
    public static double rateActionWithObserver(Character skillUser, Character fitnessObserver, Character target,
                    Combat c, double selfFit, double otherFit, CustomEffect effect) {
        // Clone ourselves a new combat... This should clone our characters, too
        Combat c2;
        try {
            c2 = c.clone();
        } catch (CloneNotSupportedException e) {
            return 0;
        }

        Global.debugSimulation += 1;
        Character newSkillUser = getCopyFromCombat(c, c2, skillUser);
        Character newObserver = getCopyFromCombat(c, c2, fitnessObserver);
        Character newOpponent = c2.getOpponentCharacter(newSkillUser);
        Character newTarget = getCopyFromCombat(c, c2, target);

        effect.execute(c2, newSkillUser, newTarget);
        Global.debugSimulation -= 1;
        double selfFitnessDelta = newObserver.getFitness(c) - selfFit;
        double otherFitnessDelta = newObserver.getOtherFitness(c, newOpponent) - otherFit;
        return selfFitnessDelta - otherFitnessDelta;
    }
}