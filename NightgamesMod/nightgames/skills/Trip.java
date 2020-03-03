package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Falling;
import nightgames.status.Slimed;

public class Trip extends Skill {
    public Trip(Character self) {
        super("Trip", self, 2);
        addTag(SkillTag.positioning);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return !target.wary() && c.getStance()
                                  .mobile(getSelf())
                        && !c.getStance()
                             .prone(target)
                        && c.getStance()
                            .front(getSelf())
                        && getSelf().canAct();
    }

    private boolean isSlime() {
        return getSelf().get(Attribute.Slime) > 11;
    }

    private boolean isArcane() {

        return getSelf().get(Attribute.Arcane) >= 10;
    }
    

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.roll(getSelf(), c, accuracy(c, target)) && getSelf().check(Attribute.Cunning, target.knockdownDC())) {
            if (isSlime()) {
                writeOutput(c, Result.special, target);
                if (getSelf().has(Trait.VolatileSubstrate)) {
                    target.add(c, new Slimed(target, getSelf(), Global.random(2, 4)));
                }
            } else {
                writeOutput(c, Result.normal, target);
            }
            target.add(c, new Falling(target));
        } else {
            if (isSlime()) {
                writeOutput(c, Result.weak, target);
            } else {
                writeOutput(c, Result.miss, target);
            }
            return false;
        }
        return true;
    }

    @Override
    public int getMojoCost(Combat c) {
        return 10;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Cunning) >= 16 || user.get(Attribute.Arcane) >= 10;
    }

    @Override
    public Skill copy(Character user) {
        return new Trip(user);
    }

    @Override
    public int speed() {
        return 2;
    }

    @Override
    public int accuracy(Combat c, Character target) {
        double cunningDifference = getSelf().get(Attribute.Cunning) - c.getOpponentCharacter(getSelf())
                                                                       .get(Attribute.Cunning);
        double accuracy = 2.5f * cunningDifference + 75 - target.knockdownDC();
        if (isSlime()) {
            accuracy += 25;
        }

        if (isArcane()) {
            accuracy/=2;
            accuracy+=getSelf().get(Attribute.Arcane)*5;
        }

        return (int) Math.round(Global.clamp(accuracy, isSlime() ? 50 : 25, 150));
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return "You try to trip " + target.getName() + ", but she keeps her balance.";
        } else if (modifier == Result.special) {
            return String.format(
                            "You reshape your hands into a sheet of slime and slide it under %s's feet."
                                            + " When you quickly pull it back, %s falls flat on %s back.",
                            target.getName(), target.pronoun(), target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format(
                            "You reshape your hands into a sheet of slime and slide it towards %s."
                                            + " In the nick of time, %s jumps clear, landing safely back on %s feet.",

                            target.getName(), target.pronoun(), target.possessiveAdjective());

        } else if (modifier == Result.item) {
            return String.format(
                            "%s %s a glowing bar hovering behind %s knees, and %s it towards %s. The bar pushes against %s knees, but %s %s to keep %s balance.",
                            getSelf().getName(), target.action("conjure"), target.nameOrPossessivePronoun(), target.action("sweep"), getSelf().reflexivePronoun(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("manage"), target.possessiveAdjective());
        } else if (modifier == Result.divine) {
            return String.format(
                            "%s %s a glowing bar hovering behind %s knees, and %s it towards %s. The bar pushes against %s knees, and %s %s to keep %s balance, and %s to the ground.",
                            getSelf().getName(), target.action("conjure"), target.nameOrPossessivePronoun(), target.action("sweep"), getSelf().reflexivePronoun(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("fail"), target.possessiveAdjective(), target.action("topple"));
        } else {
            return "You catch " + target.getName() + " off balance and trip " + target.objectPronoun() + ".";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s hooks %s ankle, but %s %s without falling.", getSelf().subject(),
                            target.nameOrPossessivePronoun(), target.pronoun(), target.action("recover"));
        } else if (modifier == Result.special) {
            return String.format(
                            "%s shoves a mass of %s slime under %s feet, destabilizing %s. With a few"
                                            + " pulls, %s throws %s onto %s back.",
                            getSelf().getName(), getSelf().possessiveAdjective(), target.nameOrPossessivePronoun(),
                            target.objectPronoun(), getSelf().pronoun(), target.objectPronoun(),
                            target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format(
                            "%s forms some of %s slime into a sheet and slides it towards %s feet."
                                            + " %s %s away from it, and %s harmlessly retracts the slime.",
                            getSelf().getName(), getSelf().possessiveAdjective(), target.nameOrPossessivePronoun(),
                            Global.capitalizeFirstLetter(target.pronoun()), target.action("jump"), getSelf().pronoun());
        } else {
            return String.format("%s takes %s feet out from under %s and sends %s sprawling to the floor.",
                            getSelf().subject(), target.nameOrPossessivePronoun(), target.objectPronoun(),
                            target.objectPronoun());
        }
    }
    
    @Override
    public String getLabel(Combat c) {
        if (getSelf().get(Attribute.Arcane) >= 10) {
            return "Tripping Construct";
        } else {
            return getName(c);
        }
    }

    @Override
    public String describe(Combat c) {
        return "Attempt to trip your opponent";
    }

    @Override
    public boolean makesContact(Combat c) {
        return true;
    }
}
