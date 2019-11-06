package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart.Size;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;


public class BreastGrowthSuper extends Skill {
    public BreastGrowthSuper(Character self) {
        super("Super Breast Growth", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Arcane) >= 30 && user.getName()=="Cassie";
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && c.getStance().mobile(getSelf()) && !c.getStance().prone(getSelf())
                        && c.getStance().behind(getSelf()) && target.breastsAvailable();
    }

    @Override
    public float priorityMod(Combat c) {
        return 0;
    }

    @Override
    public int getMojoCost(Combat c) {
        return 30;
    }

    @Override
    public String describe(Combat c) {
        return "Massively grow your opponent's boobs to make her more sensitive.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        System.out.println("starting to resolve SuperBreastGrowth");
        Result res;
        if (target.roll(getSelf(), c, accuracy(c,target))) {
            if (target.body.getRandomBreasts().getSize() == Size.FlatChest) {
                res = Result.special;
            } else {
                res = Result.normal;
            }
        } else {
            res = Result.miss;
        }
        return res != Result.miss;
    }

    @Override
    public Skill copy(Character user) {
        return new BreastGrowthSuper(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String message;
        if (modifier == Result.normal) {
            message = String.format(
                            "You channel your arcane energies into %s breasts, "
                                            + "causing them to grow rapidly. %s knees buckle with the new"
                                            + " sensitivity you bestowed on %s boobs.",
                            target.nameOrPossessivePronoun(),
                            Global.capitalizeFirstLetter(target.possessivePronoun()), target.possessivePronoun());
            if (damage > 0) {
                message += " You realize some of the effects are permanent!";
            }
        } else if (modifier == Result.special) {
            message = String.format(
                            "You channel your arcane energies into %s flat chest, "
                                            + "causing small mounds to rapidly grow on %s. %s knees buckle with the"
                                            + " sensitivity you bestowed on %s new boobs.",
                            target.nameOrPossessivePronoun(), target.objectPronoun(),
                            Global.capitalizeFirstLetter(target.possessivePronoun()), target.possessivePronoun());
            if (damage > 0) {
                message += " You realize some of the effects are permanent!";
            }
        } else {
            message = String.format(
                            "You attempt to channel your arcane energies into %s breasts, but "
                                            + "%s %s out of the way, causing your spell to fail.",
                            target.nameOrPossessivePronoun(), target.pronoun(), target.action("dodge"));
        }
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        String message;
        if (modifier == Result.normal) {
//            message = String.format(
//                            "%s wraps her hands around . %s %s breasts grow hot, and they start expanding!"
//                                            + " %s to hold them back with %s hands, but the growth continues untill they are a full cup size"
//                                            + " bigger than begore. The new sensations from %s substantially larger breasts make %s tremble.",
//                            getSelf().name(), Global.capitalizeFirstLetter(target.subjectAction("feel")),
//                            target.possessivePronoun(), Global.capitalizeFirstLetter(target.pronoun()),
//                            target.action("try", "tries"), target.possessivePronoun(), target.objectPronoun());
            message = "Cassie wraps her hands around your chest and gropes you a few times. \"You know, I've always found breasts interesting,\" she says, "
                            + "\"They're probably the most eye-catching part of the female anatomy under most circumstances, but they're not nearly as erogenous as genitals.\""
                            + " Cassie wraps her hands around your breasts, gripping your nipples between her thumb and forefinger, pushing herself against your back, her breath "
                            + "hot on the back of your neck. \"I wonder if I can change that though...\" she whispers breathily into your ear, switching between tweaking your "
                            + "nipples and tracing over your breasts with burning-hot fingers, holding on more tightly as you try to squirm out of her grip. "
                            + "\"There's this new spell I've been working on, but I have a feeling it has the same tendency to malfunction with permanent effects like "
                            + "the last one I came up with, and I have no desire to end up a massive-breasted cow-slut that comes every time someone touches her breasts, "
                            + "so I've been having trouble testing it.\" You think you know where this is going, and you want no part in it. You try to throw Cassie off, "
                            + "but she hangs on to you gamely. \"I'm sure you wouldn't mind letting me test it on you, though, would you "+target.getName()+"?\""
                            + "Without waiting for a response, she starts muttering under her breath. A tingly feeling envelops your breasts, and light starts shining from them-"
                            + "you realize that under the cover of molesting you earlier, Cassie drew some kind of magic circle on your breasts! You open your mouth to protest "
                            + ", but suddenly a burning hot feeling ripples over your breasts as they swell dramatically in size, and the feeling of her hands touching them"
                            + "suddenly feels magnified dozens of times. She pinches your nipples and you almost come from the mammary stimulation alone, a lewd moan cutting off"
                            + " whatever you were going to say. You manage to hold on, but you're not sure you could take much more of her stimulating your breasts! This is really bad!";
            if (damage > 0) {
                message += Global.capitalizeFirstLetter(target.subjectAction("realize"))
                                + " some of the effects are permanent!";
            }
        } else if (modifier == Result.special) {
            message = String.format(
                            "%s moving and begins chanting. %s %s chest grow hot, and small, perky breasts start to form!"
                                            + " %s to hold them back with %s hands, but the growth continues untill they are a full A-cup."
                                            + " The new sensations from %s new breasts make %s tremble.",
                            getSelf().getName(), Global.capitalizeFirstLetter(target.subjectAction("feel")),
                            target.possessivePronoun(), Global.capitalizeFirstLetter(target.pronoun()),
                            target.action("try", "tries"), target.possessivePronoun(), target.objectPronoun());
        } else {
            message = String.format(
                            "%s moving and begins chanting. %s feeling some tingling in %s breasts, "
                                            + "but it quickly subsides as %s %s out of the way.",
                            getSelf().subjectAction("stop"),
                            Global.capitalizeFirstLetter(target.subjectAction("start")), target.possessivePronoun(),
                            target.pronoun(), target.action("dodge"));
        }
        return message;
    }

}
