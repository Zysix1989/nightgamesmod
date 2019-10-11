package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.characters.body.Body;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.EngulfedFuck.Pairing;
import nightgames.skills.damage.DamageType;
import nightgames.stance.BreastSmothering;
import nightgames.stance.FlyingCarry;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;
import nightgames.status.Charmed;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.BreastsPart;
import nightgames.status.WingWrapped;

public class BreastSmother extends Skill {
    public BreastSmother(Character self) {
        super("BreastSmother", self);
        addTag(SkillTag.usesBreasts);
        addTag(SkillTag.breastfeed);
        addTag(SkillTag.perfectAccuracy);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.human() && user.hasBreasts();
    }

    @Override
    public float priorityMod(Combat c) {
        if (c.getStance().havingSex(c)) {
            return 1; 
        } else {
            return 3;
        }
    }

    static int MIN_REQUIRED_BREAST_SIZE = 4;
    
    @Override
    public boolean usable(Combat c, Character target) {
        return true;
    }

    @Override
    public String describe(Combat c) {
        return "Shove your opponent's face between your tits to crush her resistance.";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        boolean special = c.getStance().en != Stance.breastsmothering && !c.getStance().havingSex(c);        
        writeOutput(c, special ? Result.special : Result.normal, target);

        double n = getSelf().body.getLargestBreasts().mod(Attribute.Seduction);

        if (target.has(Trait.temptingtits)) {
            n += Global.random(5, 10);
        }
        if (target.has(Trait.beguilingbreasts)) {
            n *= 1.5;
            target.add(c, new Charmed(target));
        }
        if (target.has(Trait.imagination)) {
            n *= 1.5;
        }

        target.temptWithSkill(c, getSelf(), getSelf().body.getRandomBreasts(), (int) Math.round(n / 2), this);
        target.weaken(c, (int) getSelf().modifyDamage(DamageType.physical, target, Global.random(5, 15)));

        target.loseWillpower(c, Math.min(5, target.getWillpower().max() * 10 / 100 ));     

        if (special) {
            c.setStance(new BreastSmothering(getSelf(), target), getSelf(), true);
            getSelf().emote(Emotion.dominant, 20);
        } else {
            getSelf().emote(Emotion.dominant, 10);
        }
        if (Global.random(100) < 15 + 2 * getSelf().get(Attribute.Fetish)) {
            target.add(c, new BodyFetish(target, getSelf(), BreastsPart.TYPE, 10));
        }

        Pairing pair = Pairing.findPairing(getSelf(), target);
        double base = 10.0 + Math.min(20, Global.random(getSelf().get(Attribute.Slime) / 3 + getSelf().get(Attribute.Seduction) / 5));
        int selfDmg = (int) ((base * pair.modPleasure(true)) / (getSelf().has(Trait.experienced) ? 2.0 : 3.0));
        int targetDmg = (int) (base * pair.modPleasure(false));
        switch (pair) {
            case ASEX_MALE:
                c.write(getSelf(),
                                Global.format("{self:SUBJECT-ACTION:wrap|wraps} {other:name-possessive}"
                                                + " {other:body-part:cock} in a clump of slime molded after {self:possessive} ass"
                                                + " and {self:action:pump|pumps} it furiously.", getSelf(), target));
                target.body.pleasure(getSelf(), getSelf().body.getRandomAss(), target.body.getRandomCock(), targetDmg,
                                c, this);
                getSelf().body.pleasure(target, target.body.getRandomCock(), getSelf().body.getRandomAss(), selfDmg, c, this);
                break;
            case FEMALE_HERM:
                c.write(getSelf(),
                                Global.format("{self:SUBJECT-ACTION:impale|impales} {self:reflective} on"
                                                + " {other:name-possessive} {other:body-part:cock} and {self:action:bounce|bounces} wildly,"
                                                + " filling {other:direct-object} with pleasure. At the same time, {self:pronoun} "
                                                + "{self:action:twiddle|twiddles} {other:possessive} clit with {self:possessive} fingers.",
                                getSelf(), target));
                target.body.pleasure(getSelf(), getSelf().body.getRandomPussy(), target.body.getRandomCock(),
                                targetDmg / 2, c, this);
                target.body.pleasure(getSelf(), getSelf().body.getRandom(Body.HANDS), target.body.getRandomPussy(),
                                targetDmg / 2, c, this);
                getSelf().body.pleasure(target, target.body.getRandomCock(), getSelf().body.getRandomPussy(), selfDmg,
                                c, this);
                break;
            case FEMALE_MALE:
                c.write(getSelf(),
                                Global.format("{self:SUBJECT-ACTION:impale|impales} {self:reflective} on"
                                                + " {other:name-possessive} {other:body-part:cock} and {self:action:bounce|bounces} wildly,"
                                                + " filling {other:direct-object} with pleasure.", getSelf(), target));
                target.body.pleasure(getSelf(), getSelf().body.getRandomPussy(), target.body.getRandomCock(), targetDmg,
                                c, this);
                getSelf().body.pleasure(target, target.body.getRandomCock(), getSelf().body.getRandomPussy(), selfDmg,
                                c, this);
                break;
            case HERM_ASEX:
            case MALE_ASEX:
                c.write(getSelf(),
                                Global.format("Despite not having much to work with, {self:subject} still"
                                                + " {self:action:manage|manages} to make {other:subject} squeal by pounding {other:name-possessive}"
                                                + " {other:body-part:ass} with {self:possessive} {self:body-part:cock}.",
                                getSelf(), target));
                target.body.pleasure(getSelf(), getSelf().body.getRandomCock(), target.body.getRandomAss(), targetDmg,
                                c, this);
                getSelf().body.pleasure(target, target.body.getRandomAss(), getSelf().body.getRandomCock(), selfDmg, c, this);
                break;
            case HERM_FEMALE:
                c.write(getSelf(),
                                Global.format("{self:SUBJECT-ACTION:pound|pounds} {other:name-possessive} "
                                                + "{other:body-part:pussy} with vigor, at the same time fingering {other:possessive}"
                                                + " ass.", getSelf(), target));
                target.body.pleasure(getSelf(), getSelf().body.getRandomCock(), target.body.getRandomPussy(),
                                targetDmg / 2, c, this);
                target.body.pleasure(getSelf(), getSelf().body.getRandom(Body.HANDS), target.body.getRandomAss(),
                                targetDmg / 2, c, this);
                getSelf().body.pleasure(target, target.body.getRandomPussy(), getSelf().body.getRandomCock(), selfDmg,
                                c, this);
                break;
            case HERM_HERM:
                c.write(getSelf(),
                                Global.format("It takes some clever maneuvering, but {self:SUBJECT-ACTION:manage|manages}"
                                                + " to line {other:name-do} and {self:reflective} up perfectly. When"
                                                + " {self:pronoun} {self:action:strike|strikes}, {other:possessive} {other:body-part:cock} ends up"
                                                + " in {self:possessive} {self:body-part:pussy}, and {self:body-part:cock} in {other:possessive}"
                                                + " {other:body-part:pussy}. With every twitch, both of you are filled with unimaginable pleasure,"
                                                + " so when {self:pronoun} {self:action:start|starts} fucking in earnest the sensations are"
                                                + " almost enough to cause you both to pass out.", getSelf(), target));
                target.body.pleasure(getSelf(), getSelf().body.getRandomCock(), target.body.getRandomPussy(),
                                targetDmg / 2, c, this);
                target.body.pleasure(getSelf(), getSelf().body.getRandomPussy(), target.body.getRandomCock(),
                                targetDmg / 2, c, this);
                getSelf().body.pleasure(target, target.body.getRandomPussy(), getSelf().body.getRandomCock(),
                                selfDmg / 2, c, this);
                getSelf().body.pleasure(target, target.body.getRandomCock(), getSelf().body.getRandomPussy(),
                                selfDmg / 2, c, this);
                break;
            case HERM_MALE:
                c.write(getSelf(),
                                Global.format("{self:SUBJECT-ACTION:lower|lowers} {self:possessive}"
                                                + " {self:body-part:pussy} down on {other:name-possessive} {other:body-part:cock}."
                                                + " While slowly fucking {other:direct-object}, {self:pronoun} {self:action:move|moves}"
                                                + " {self:possessive} {self:body-part:cock} to the entrance of {other:possessive} ass."
                                                + " Before {other:pronoun} {other:action:have|has} a chance to react, {self:pronoun}"
                                                + " {self:action:shove|shoves} it up there in one thrust, fucking {other:direct-object}"
                                                + " from both sides.", getSelf(), target));
                target.body.pleasure(getSelf(), getSelf().body.getRandomCock(), target.body.getRandomAss(),
                                targetDmg / 2, c, this);
                target.body.pleasure(getSelf(), getSelf().body.getRandomPussy(), target.body.getRandomCock(),
                                targetDmg / 2, c, this);
                getSelf().body.pleasure(target, target.body.getRandomPussy(), getSelf().body.getRandomCock(),
                                selfDmg / 2, c, this);
                getSelf().body.pleasure(target, target.body.getRandomCock(), getSelf().body.getRandomAss(), selfDmg / 2,
                                c, this);
                break;
            case MALE_FEMALE:
            case MALE_MALE:
            case MALE_HERM:
                BodyPart bpart = pair == Pairing.MALE_MALE ? target.body.getRandomAss() : target.body.getRandomPussy();
                int realTargetDmg = targetDmg;
                String msg = "{self:SUBJECT-ACTION:place|places} {self:possessive}"
                                + " {self:body-part:cock} at {other:name-possessive} " + bpart.describe(target)
                                + " and thrust in all the way"
                                + " in a single movement of {self:possessive} hips. {self:PRONOUN} "
                                + "{self:action:proceed|proceeds} to piston in and out at a furious pace.";
                if (pair == Pairing.MALE_HERM) {
                    msg += " At the same time, {self:pronoun} {self:action:use:uses} both of {self:possessive}"
                                    + " hands to pump {other:possessive} {other:body-part:cock}.";
                }
                c.write(getSelf(), Global.format(msg, getSelf(), target));
                if (pair == Pairing.MALE_HERM) {
                    target.body.pleasure(getSelf(), getSelf().body.getRandom(Body.HANDS), target.body.getRandomCock(),
                                    targetDmg / 2, c, this);
                    realTargetDmg /= 2;
                }
                target.body.pleasure(getSelf(), getSelf().body.getRandomCock(), bpart, realTargetDmg, c, this);
                getSelf().body.pleasure(target, bpart, getSelf().body.getRandomCock(), selfDmg, c, this);
                break;
        }



        target.add(c, new WingWrapped(target, getSelf()));

        getSelf().emote(Emotion.dominant, 50);
        getSelf().emote(Emotion.horny, 30);
        target.emote(Emotion.desperate, 50);
        target.emote(Emotion.nervous, 75);
        int m = 5 + Global.random(5);
        int otherm = m;
        if (getSelf().has(Trait.insertion)) {
            otherm += Math.min(getSelf().get(Attribute.Seduction) / 4, 40);
        }
        c.setStance(new FlyingCarry(getSelf(), target), getSelf(), getSelf().canMakeOwnDecision());

        return true;
    }

    @Override
    public int getMojoBuilt(Combat c) {
        return 0;
    }

    @Override
    public Skill copy(Character user) {
        return new BreastSmother(user);
    }

    @Override
    public Tactics type(Combat c) {
        if (c.getStance().enumerate() != Stance.breastsmothering) {
            return Tactics.positioning;
        } else {
            return Tactics.pleasure;
        }
    }

    @Override
    public String getLabel(Combat c) {
        return "BreastSmother";
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        StringBuilder b = new StringBuilder();
        
        if (modifier == Result.special) {
            b.append( "You quickly wrap up " + target.getName() + "'s head in your arms and press your "
                            + getSelf().body.getRandomBreasts().fullDescribe(getSelf()) + " into " + target.nameOrPossessivePronoun() + " face. ");
        }
        else {
            b.append( "You rock " + target.getName() + "'s head between your "
                            + getSelf().body.getRandomBreasts().fullDescribe(getSelf()) + " trying to force " + target.objectPronoun() + " to gasp. ");
        }
        
        if (getSelf().has(Trait.temptingtits)) {
            b.append(Global.capitalizeFirstLetter(target.pronoun()) + " can't help but groan in pleasure from having " + target.possessiveAdjective() + " face stuck between your perfect tits");                          
            if (getSelf().has(Trait.beguilingbreasts)) {
                b.append(", and you smile as " + target.pronoun() + " snuggles deeper into your cleavage");
            } 
            b.append(".");
            
        } else{
            b.append(" " + target.getName() + " muffles something in confusion into your breasts before " + target.pronoun() + " begins to panic as " + target.pronoun() + " realizes " + target.pronoun() + " cannot breathe!");            
        }   
        return b.toString();
}

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        StringBuilder b = new StringBuilder();
        if (modifier == Result.special) {
            b.append( getSelf().subject()+ " quickly wraps up your head between " + getSelf().possessiveAdjective() + " "
                            + getSelf().body.getRandomBreasts().fullDescribe(getSelf()) + ", filling your vision instantly with them. ");
        } else {
            b.append( getSelf().subject()+ " rocks your head between " + getSelf().possessiveAdjective() + " "
                            + getSelf().body.getRandomBreasts().fullDescribe(getSelf()) + " trying to force you to gasp for air. ");
        }
        
        if (getSelf().has(Trait.temptingtits)) {
            b.append("You can't help but groan in pleasure from having your face stuck between ");
            b.append(getSelf().possessiveAdjective());
            b.append(" perfect tits as they take your breath away");             
            if (getSelf().has(Trait.beguilingbreasts)) {
                b.append(", and due to their beguiling nature you can't help but want to stay there as long as possible");
            }
            b.append(".");
        } else {
            b.append(" You let out a few panicked sounds muffled by the breasts now covering your face as you realize you cannot breathe!");
        }

        return b.toString();
    }

    @Override
    public boolean makesContact(Combat c) {
        return true;
    }
}
