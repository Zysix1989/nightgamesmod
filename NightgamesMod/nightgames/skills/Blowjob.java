package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.mods.ExtendedTonguedMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.ReverseMount;
import nightgames.stance.SixNine;

public class Blowjob extends Skill {
    public Blowjob(String name, Character self) {
        super(name, self);
    }

    public Blowjob(Character self) {
        this("Blow", self);
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        boolean canUse = !c.getStance().isBeingFaceSatBy(c, getSelf(), target) && getSelf().canAct();
        return ((target.crotchAvailable() && target.hasDick() && c.getStance().oral(getSelf(), target)
                        && c.getStance().front(getSelf()) && canUse)
                        || (getSelf().canRespond() && isVaginal(c, target)));
    }

    @Override
    public float priorityMod(Combat c) {
        float priority = 0;
        if (c.getStance().penetratedBy(c, getSelf(), c.getOpponentCharacter(getSelf()))) {
            priority += 1.0f;
        }
        if (getSelf().has(Trait.silvertongue)) {
            priority += 1;
        }
        if (getSelf().has(Trait.experttongue)) {
            priority += 1;
        }
        return priority;
    }

    public boolean isVaginal(Combat c, Character target) {
        return c.getStance().isPartFuckingPartInserted(c,
            target,
            target.body.getRandomCock(),
            getSelf(),
            getSelf().body.getRandomPussy())
            && !c.getOpponentCharacter(getSelf()).has(Trait.strapped)
            && getSelf().body.getRandomPussy().moddedPartCountsAs(ExtendedTonguedMod.TYPE);
    }

    public boolean isFacesitting(Combat c, Character target) {
        return c.getStance().isBeingFaceSatBy(c, getSelf(), target);
    }

    @Override
    public int getMojoBuilt(Combat c) {
        if (isVaginal(c, c.getOpponentCharacter(getSelf()))) {
            return 10;
        } else if (c.getStance().isBeingFaceSatBy(c, getSelf(), c.getOpponentCharacter(getSelf()))) {
            return 0;
        } else {
            return 5;
        }
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        int arousalToTarget = 10 + Global.random(8);
        boolean facesitting = isFacesitting(c, target);
        if (getSelf().has(Trait.silvertongue)) {
            arousalToTarget += 4;
        }
        if (isVaginal(c, target)) {
            arousalToTarget += 4;
            writeOutput(c, arousalToTarget, Result.intercourse, target);
            target.body.pleasure(getSelf(), getSelf().body.getRandomPussy(), target.body.getRandom(
                CockPart.TYPE), arousalToTarget, c, this);
        } else if (facesitting) {
            writeOutput(c, arousalToTarget, Result.reverse, target);
            target.body.pleasure(getSelf(), getSelf().body.getRandomMouth(), target.body.getRandomCock(), arousalToTarget, c, this);
            target.buildMojo(c, 10);
        } else if (target.roll(getSelf(), c, accuracy(c, target))) {
            writeOutput(c, arousalToTarget, getSelf().has(Trait.silvertongue) ? Result.special : Result.normal, target);
            BodyPart mouth = getSelf().body.getRandomMouth();
            BodyPart cock = target.body.getRandomCock();
            target.body.pleasure(getSelf(), mouth, cock, arousalToTarget, c, this);
            if (mouth.isErogenous()) {
                getSelf().body.pleasure(target, cock, mouth, arousalToTarget, c, this);
            }

            if (ReverseMount.class.isInstance(c.getStance())) {
                c.setStance(new SixNine(getSelf(), target), getSelf(), true);
            }
        } else {
            writeOutput(c, Result.miss, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Seduction) >= 10 && !user.has(Trait.temptress);
    }

    @Override
    public int accuracy(Combat c, Character target) {
        return isVaginal(c, target) || isFacesitting(c, target) || !c.getStance().reachTop(target)? 200 : 75;
    }

    @Override
    public Skill copy(Character user) {
        return new Blowjob(user);
    }

    @Override
    public int speed() {
        return 2;
    }

    @Override
    public Tactics type(Combat c) {
        if (isVaginal(c, c.getStance().getPartner(c, getSelf()))) {
            return Tactics.fucking;
        } else {
            return Tactics.pleasure;
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        String m = "";
        if (modifier == Result.miss) {
            m = "You try to take " + target.getName() + "'s penis into your mouth, but she manages to pull away.";
        }
        if (target.getArousal().get() < 15) {
            m = "You suck on " + target.nameOrPossessivePronoun()
                            + " flaccid little penis until it grows into an intimidating large erection.";
        } else if (target.getArousal().percent() >= 90) {
            m = target.getName()
                            + "'s girl-cock seems ready to burst, so you suck on it strongly and attack the glans with your tongue fiercely.";
        } else if (modifier == Result.special) {
            m = "You put your skilled tongue to good use tormenting and teasing her unnatural member.";
        } else if (modifier == Result.reverse) {
            m = "With " + target.getName() + " sitting over your face, you have no choice but to try to suck her off.";
        } else {
            m = "You feel a bit odd, faced with " + target.getName()
                            + "'s rigid cock, but as you lick and suck on it, you discover the taste is quite palatable. Besides, "
                            + "making " + target.getName() + " squirm and moan in pleasure is well worth it.";
        }
        if (modifier != Result.miss && getSelf().body.getRandomMouth().isErogenous()) {
            m += "<br/>Unfortunately for you, your sensitive modified mouth pussy sends spasms of pleasure into you too as you mouth fuck "
                            + target.possessiveAdjective() + " cock.";
        }
        return m;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        String m = "";
        if (modifier == Result.miss) {
            m += String.format("%s tries to suck %s cock, but %s %s %s hips back to avoid %s.",
                            getSelf().getName(), target.nameOrPossessivePronoun(), target.pronoun(),
                            target.action("pull"), target.possessiveAdjective(), getSelf().objectPronoun());
        } else if (modifier == Result.special) {
            m += String.format("%s soft lips and talented tongue work over %s dick, drawing out"
                            + " dangerously irresistible pleasure with each touch.", 
                            getSelf().nameOrPossessivePronoun(), target.nameOrPossessivePronoun());
        } else if (modifier == Result.intercourse) {
            m += String.format("%s pussy lips suddenly quiver and %s a long sinuous object wrap around %s cock. "
                            + "%s realize she's controlling her vaginal tongue to blow %s with her pussy! "
                            + "Her lower tongue runs up and down %s shaft causing %s to shudder with arousal.",
                            getSelf().nameOrPossessivePronoun(), target.subjectAction("feel"),
                            target.possessiveAdjective(),
                            Global.capitalizeFirstLetter(target.pronoun()), target.objectPronoun(),
                            target.possessiveAdjective(), target.objectPronoun());
        } else if (modifier == Result.reverse) {
            m += String.format("Faced with %s dick sitting squarely in front of %s face, %s"
                            + " obediently tongues %s cock in defeat.", target.nameOrPossessivePronoun(),
                            getSelf().nameOrPossessivePronoun(), getSelf().pronoun(), target.possessiveAdjective());
        } else if (target.getArousal().get() < 15) {
            m += String.format("%s %s soft penis into %s mouth and sucks on it until it hardens.",
                            getSelf().subjectAction("take"), target.nameOrPossessivePronoun(),
                            getSelf().possessiveAdjective());
        } else if (target.getArousal().percent() >= 90) {
            m += String.format("%s up the precum leaking from %s cock and %s the entire length into %s mouth, sucking relentlessly.",
                            getSelf().subjectAction("lap"), target.nameOrPossessivePronoun(), getSelf().action("take"),
                            getSelf().possessiveAdjective());
        } else {
            int r = Global.random(4);
            if (r == 0) {
                m += String.format("%s %s tongue up the length of %s dick, sending a jolt of pleasure up %s spine. "
                                + "%s slowly wraps %s lips around %s dick and sucks.",
                                getSelf().subjectAction("run"), getSelf().possessiveAdjective(), target.nameOrPossessivePronoun(),
                                target.possessiveAdjective(), Global.capitalizeFirstLetter(getSelf().pronoun()),
                                getSelf().possessiveAdjective(), target.nameOrPossessivePronoun());
            } else if (r == 1) {
                m += String.format("%s on the head of %s cock while %s hand strokes the shaft.",
                                getSelf().subjectAction("suck"), target.nameOrPossessivePronoun(), getSelf().possessiveAdjective());
            } else if (r == 2) {
                m += String.format("%s %s way down to the base of %s cock and gently sucks on %s balls.",
                                getSelf().subjectAction("lick"), getSelf().possessiveAdjective(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective());
            } else {
                m += String.format("%s %s tongue around the glans of %s penis and teases %s urethra.",
                                getSelf().subjectAction("run"), getSelf().possessiveAdjective(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective());
            }
        }

        if (modifier != Result.miss && getSelf().body.getRandomMouth().isErogenous()) {
            m += String.format("<br/>Unfortunately for %s, as %s mouth fucks %s cock %s sensitive"
                            + " modifier mouth pussy sends spasms of pleasure into %s as well.", 
                            getSelf().objectPronoun(), getSelf().subject(), target.nameOrPossessivePronoun(),
                            getSelf().possessiveAdjective(), getSelf().reflexivePronoun());
        }
        return m;
    }

    @Override
    public String describe(Combat c) {
        return "Lick and suck your opponent's dick";
    }

    @Override
    public boolean makesContact(Combat c) {
        return true;
    }
}
