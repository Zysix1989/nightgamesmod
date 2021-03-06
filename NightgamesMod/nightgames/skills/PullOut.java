package nightgames.skills;

import java.util.Optional;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.AssPart;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.PussyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;
import nightgames.status.BodyFetish;
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
        return !target.hasStatus(Stsflag.knotted) && getSelf().canAct() && (c.getStance().isFaceSitting(getSelf())
                        || c.getStance().havingSex(c, getSelf()) && c.getStance().dom(getSelf())) && !blockedByAddiction(getSelf());
    }

    public static boolean blockedByAddiction(Character user) {
        if (!user.human()) {
            return false;
        }
        Optional<Addiction> addiction = user.getAddiction(AddictionType.BREEDER);
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
        } else if (c.getStance().isFacesatOn(target)) {
            result = Result.special;
        }
        boolean isLocked = getSelf().hasStatus(Stsflag.leglocked) || getSelf().hasStatus(Stsflag.armlocked);
        int baseDifficulty = isLocked ? 17 : 10;
        if (target.has(Trait.stronghold)) {
            baseDifficulty += 5;
        }
        int powerMod = Math.min(20, Math.max(5, target.get(Attribute.Power) - getSelf().get(Attribute.Power)));

        if (target.is(Stsflag.enthralled) || target.is(Stsflag.trance) 
                        || target.is(Stsflag.lovestruck)) {
            powerMod-=target.get(Attribute.Power)/2;
        }
        if (getSelf().is(Stsflag.bondage) || getSelf().is(Stsflag.charmed) 
                        || target.is(Stsflag.lovestruck)) {
            powerMod+=target.get(Attribute.Power)/4;
        }

        if (c.getStance().en == Stance.anal) {
            if (target.has(Trait.bewitchingbottom)) {
                Optional<BodyFetish> fetish = getSelf().body.getFetish(AssPart.TYPE);
                if(fetish.isPresent()) {
                    baseDifficulty += 7 * fetish.get().magnitude;
                }
            }
            if (!target.has(Trait.powerfulcheeks)) {
                writeOutput(c, result, target);
                c.setStance(c.getStance().insertRandom(c));
                return true;
            } else if (getSelf().check(Attribute.Power, 
                            baseDifficulty - getSelf().getEscape(c, target) + powerMod)) {
                if (isLocked) {
                    c.write(getSelf(), Global.format("Despite {other:name-possessive} inhumanly tight"
                                    + " ass and {other:possessive} strong grip on {self:direct-object},"
                                    + " {self:pronoun-action:manage|manages} to pull {self:body-part:cock}"
                                    + " ever so slowly out of {other:direct-object}.", getSelf(), target));
                } else {
                    c.write(getSelf(), Global.format("{other:NAME-POSSESSIVE} ass clenches powerfully"
                                    + " around {self:name-possessive} {self:body-part:cock} as"
                                    + " {self:pronoun-action:try|tries} to pull out of"
                                    + " it, but it proves insufficient as the hard shaft escapes its"
                                    + " former prison.", getSelf(), target));
                }
                c.setStance(c.getStance().insertRandom(c));
            } else if (!isLocked) {
                c.write(getSelf(), Global.format("{self:SUBJECT-ACTION:try|tries} to pull out of"
                                + " {other:name-possessive} lustrous ass, but {other:pronoun-action:squeeze|squeezes}"
                                + " {other:possessive} asscheeks tightly around your {self:body-part:cock},"
                                + " preventing your extraction.", getSelf(), target));
                getSelf().body.pleasure(target, target.body.getRandomAss(), getSelf().body.getRandomCock(), 6, c, this);
            } else {
                String lockDesc = getSelf().hasStatus(Stsflag.leglocked) ? "legs" : "arms";
                c.write(getSelf(), Global.format("{self:SUBJECT-ACTION:try|tries} to pull out of"
                                + " {other:name-possessive} lustrous ass, but the combination"
                                + " of {other:possessive} tightly squeezing ass and"
                                + " powerful %s locks {self:pronoun} firmly inside of {other:direct-object}."
                                , getSelf(), target, lockDesc));
                getSelf().body.pleasure(target, target.body.getRandomAss(), getSelf().body.getRandomCock(), 10, c, this);
            }
        } else if (result == Result.special) {
            writeOutput(c, Result.special, target);
            c.setStance(new StandingOver(getSelf(), target), getSelf(), true);
        } else {
            if (isLocked || target.has(Trait.tight) && c.getStance().inserted(getSelf())) {
                boolean escaped = getSelf().check(Attribute.Power,
                                10 - getSelf().getEscape(c, target) + target.get(Attribute.Power));
                if (escaped) {
                    writeOutput(c, result, target);
                } else {
                    if (getSelf().hasStatus(Stsflag.leglocked)) {
                        BodyPart part = c.getStance().anallyPenetrated(c, getSelf()) ? target.body.getRandomAss()
                                        : target.body.getRandomPussy();
                        String partString = part.describe(target);
                        if (getSelf().human()) {
                            c.write(getSelf(), "You try to pull out of " + target.getName() + "'s " + partString
                                            + ", but her legs immediately tighten against your waist, holding you inside her. "
                                            + "The mere friction from her action sends a shiver down your spine.");
                        } else {
                            c.write(getSelf(), String.format("%s tries to pull out of %s %s, but %s legs immediately pull"
                                            + " %s back in, holding %s inside %s.", getSelf().subject(), target.nameOrPossessivePronoun(),
                                            partString, target.possessiveAdjective(), getSelf().objectPronoun(), getSelf().nameDirectObject(),
                                            target.objectPronoun()));
                        }
                    } else if (getSelf().hasStatus(Stsflag.armlocked)) {
                        if (getSelf().human()) {
                            c.write(getSelf(), "You try to pull yourself off of " + target.getName()
                                            + ", but she merely pulls you back on top of her, surrounding you in her embrace.");
                        } else {
                            c.write(getSelf(), String.format("%s tries to pull %s off of %s, but with "
                                            + "a gentle pull of %s hands, %s collapses back on top of %s.",
                                            getSelf().subject(), getSelf().reflexivePronoun(),
                                            target.nameDirectObject(), target.possessiveAdjective(),
                                            getSelf().pronoun(), target.objectPronoun()));
                        }
                    } else if (target.has(Trait.tight) && c.getStance().inserted(getSelf())) {
                        BodyPart part = c.getStance().anallyPenetrated(c, target) ? target.body.getRandomAss()
                                        : target.body.getRandomPussy();
                        String partString = part.describe(target);
                        if (getSelf().human()) {
                            c.write(getSelf(), "You try to pull yourself out of " + target.getName() + "'s " + partString
                                            + ", but she clamps down hard on your cock while smiling at you. You almost cum from the sensation, and quickly abandon ideas about your escape.");
                        } else {
                            c.write(getSelf(), String.format("%s tries to pull %s out of %s %s, but %s down "
                                            + "hard on %s cock, and prevent %s from pulling out.", getSelf().subject(),
                                            getSelf().reflexivePronoun(), target.possessiveAdjective(), partString,
                                            target.subjectAction("pull"), target.possessiveAdjective(),
                                            getSelf().objectPronoun()));
                        }
                    }
                    int m = 8;
                    if (c.getStance().inserted(getSelf())) {
                        BodyPart part = c.getStance().anallyPenetrated(c, target) ? target.body.getRandomAss()
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
                                s.binding, getSelf().possessiveAdjective()));
                int m = 8;
                getSelf().body.pleasure(target, target.body.getRandomPussy(), getSelf().body.getRandomCock(), m, c, this);
                return false;
            } else 
                writeOutput(c, result, target);
            c.setStance(c.getStance().insertRandom(c));
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
            return "You pull your dick completely out of " + target.getName() + "'s ass.";
        } else if (modifier == Result.normal) {
            return "You pull completely out of " + target.getName()
                            + "'s pussy, causing her to let out a disappointed little whimper.";
        } else {
            return "You pull yourself off " + target.getName()
                            + "'s face, causing her to gasp lungfuls of the new fresh air offer to her.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.anal) {
            return String.format("%s the pressure in %s anus recede as %s pulls out.",
                            target.subjectAction("feel"), target.possessiveAdjective(),
                            getSelf().subject());
        } else if (modifier == Result.reverse) {
            return String.format("%s lifts %s hips more than normal, letting %s dick slip completely out of %s.",
                            getSelf().subject(), getSelf().possessiveAdjective(),
                            target.nameOrPossessivePronoun(), getSelf().objectPronoun());
        } else if (modifier == Result.normal) {
            return String.format("%s pulls %s dick completely out of %s pussy, leaving %s feeling empty.",
                            getSelf().subject(), getSelf().possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.objectPronoun());
        } else {
            return String.format("%s lifts herself off %s face, giving %s a brief respite.",
                            getSelf().subject(), target.nameOrPossessivePronoun(), target.objectPronoun());
        }
    }

    @Override
    public String describe(Combat c) {
        return "Aborts penetration";
    }

    @Override
    public boolean makesContact(Combat c) {
        return true;
    }
}
