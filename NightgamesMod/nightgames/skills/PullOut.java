package nightgames.skills;

import java.util.Optional;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;
import nightgames.status.CockBound;
import nightgames.status.Stsflag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.Addiction.Severity;
import nightgames.status.addiction.AddictionType;

public class PullOut extends Skill {

    public PullOut(Character self) {
        super("Pull Out", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return !target.hasStatus(Stsflag.knotted) && getSelf().canAct() && (c.getStance().en == Stance.facesitting
                        || c.getStance().inserted() && c.getStance().dom(getSelf())) && !blockedByAddiction(getSelf());
    }

    public static boolean blockedByAddiction(Character user) {
        if (!user.human()) {
            return false;
        }
        Player p = Global.getPlayer();
        Optional<Addiction> addiction = p.getAddiction(AddictionType.BREEDER);
        if (!addiction.isPresent()) {
            return false;
        }
        Addiction add = addiction.get();
        return add.atLeast(Severity.HIGH) || add.combatAtLeast(Severity.HIGH);
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        Result result = Result.normal;
        if (c.getStance().inserted(getSelf())) {
            if (c.getStance().en == Stance.anal) {
                result = Result.anal;
            }
        } else if (c.getStance().inserted(target)) {
            result = Result.reverse;
        } else {
            result = Result.special;
        }
        if (c.getStance().en == Stance.anal) {
            writeOutput(c, result, target);
            c.setStance(c.getStance().insertRandom());
        } else if (result == Result.special) {
            writeOutput(c, Result.special, target);
            c.setStance(new StandingOver(getSelf(), target));
        } else {
            if (getSelf().hasStatus(Stsflag.leglocked) || getSelf().hasStatus(Stsflag.armlocked)
                            || target.has(Trait.tight) && c.getStance().inserted(getSelf())) {
                int difficulty = 10 - getSelf().escape(c,target) + target.get(Attribute.Power);
                if (target.is(Stsflag.enthralled) || target.is(Stsflag.trance) || target.is(Stsflag.lovestruck)) {difficulty-=target.get(Attribute.Power)/2;}
                if (getSelf().is(Stsflag.bondage) || getSelf().is(Stsflag.charmed) || target.is(Stsflag.lovestruck)) {difficulty+=target.get(Attribute.Power)/4;}          
                boolean escaped = getSelf().check(Attribute.Power,difficulty);
                if (escaped) {
                    writeOutput(c, result, target);
                } else {
                    if (getSelf().hasStatus(Stsflag.leglocked)) {
                        BodyPart part = c.getStance().anallyPenetrated(getSelf()) ? target.body.getRandom("ass")
                                        : target.body.getRandomPussy();
                        String partString = part.describe(target);
                        if (getSelf().human()) {
                            c.write(getSelf(), "You try to pull out of " + target.name() + "'s " + partString
                                            + ", but her legs immediately tighten against your waist, holding you inside her. "
                                            + "The mere friction from her action sends a shiver down your spine.");
                        } else {
                            c.write(getSelf(), String.format("%s tries to pull out of %s %s, but %s legs immediately pull"
                                            + " %s back in, holding %s inside %s.", getSelf().subject(), target.nameOrPossessivePronoun(),
                                            partString, target.possessivePronoun(), getSelf().directObject(), getSelf().nameDirectObject(),
                                            target.directObject()));
                        }
                    } else if (getSelf().hasStatus(Stsflag.armlocked)) {
                        if (getSelf().human()) {
                            c.write(getSelf(), "You try to pull yourself off of " + target.name()
                                            + ", but she merely pulls you back on top of her, surrounding you in her embrace.");
                        } else {
                            c.write(getSelf(), String.format("%s tries to pull %s off of %s, but with "
                                            + "a gentle pull of %s hands, %s collapses back on top of %s.",
                                            getSelf().subject(), getSelf().reflectivePronoun(),
                                            target.nameDirectObject(), target.possessivePronoun(),
                                            getSelf().pronoun(), target.directObject()));
                        }
                    } else if (target.has(Trait.tight) && c.getStance().inserted(getSelf())) {
                        BodyPart part = c.getStance().anallyPenetrated(target) ? target.body.getRandom("ass")
                                        : target.body.getRandomPussy();
                        String partString = part.describe(target);
                        if (getSelf().human()) {
                            c.write(getSelf(), "You try to pull yourself out of " + target.name() + "'s " + partString
                                            + ", but she clamps down hard on your cock while smiling at you. You almost cum from the sensation, and quickly abandon ideas about your escape.");
                        } else {
                            c.write(getSelf(), String.format("%s tries to pull %s out of %s %s, but %s down "
                                            + "hard on %s cock, and prevent %s from pulling out.", getSelf().subject(),
                                            getSelf().reflectivePronoun(), target.possessivePronoun(), partString,
                                            target.subjectAction("pull"), target.possessivePronoun(),
                                            getSelf().directObject()));
                        }
                    }
                    int m = 8;
                    if (c.getStance().inserted(getSelf())) {
                        BodyPart part = c.getStance().anallyPenetrated(target) ? target.body.getRandom("ass")
                                        : target.body.getRandomPussy();
                        getSelf().body.pleasure(target, part, getSelf().body.getRandomInsertable(), m, c, this);
                    }
                    getSelf().struggle();
                    return false;
                }
            } else if (getSelf().hasStatus(Stsflag.cockbound)) {
                CockBound s = (CockBound) getSelf().getStatus(Stsflag.cockbound);
                c.write(getSelf(), String.format("%s to pull out of %s %s, but %s instantly reacts "
                                + "and pulls %s dick back in.", getSelf().subjectAction("try", "tries"),
                                target.nameOrPossessivePronoun(), 
                                target.body.getRandomPussy().describe(target),
                                s.binding, getSelf().possessivePronoun()));
                int m = 8;
                getSelf().body.pleasure(target, target.body.getRandom("pussy"), getSelf().body.getRandom("cock"), m, c, this);
                return false;
            } else 
                writeOutput(c, result, target);
            c.setStance(c.getStance().insertRandom());
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new PullOut(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.reverse) {
            return "You rise up and let " + target.nameOrPossessivePronoun() + " girl-cock slip out of your "
                            + (c.getStance().en == Stance.anal ? "ass." : "pussy");
        } else if (modifier == Result.anal) {
            return "You pull your dick completely out of " + target.name() + "'s ass.";
        } else if (modifier == Result.normal) {
            return "You pull completely out of " + target.name()
                            + "'s pussy, causing her to let out a disappointed little whimper.";
        } else {
            return "You pull yourself off " + target.name()
                            + "'s face, causing her to gasp lungfuls of the new fresh air offer to her.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.anal) {
            return String.format("%s the pressure in %s anus recede as %s pulls out.",
                            target.subjectAction("feel"), target.possessivePronoun(),
                            getSelf().subject());
        } else if (modifier == Result.reverse) {
            return String.format("%s lifts %s hips more than normal, letting %s dick slip completely out of %s.",
                            getSelf().subject(), getSelf().possessivePronoun(),
                            target.nameOrPossessivePronoun(), getSelf().directObject());
        } else if (modifier == Result.normal) {
            return String.format("%s pulls %s dick completely out of %s pussy, leaving %s feeling empty.",
                            getSelf().subject(), getSelf().possessivePronoun(),
                            target.nameOrPossessivePronoun(), target.directObject());
        } else {
            return String.format("%s lifts herself off %s face, giving %s a brief respite.",
                            getSelf().subject(), target.nameOrPossessivePronoun(), target.directObject());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Aborts penetration";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
