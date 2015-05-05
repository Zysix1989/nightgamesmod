package skills;

import global.Global;
import stance.FaceSitting;
import stance.Stance;
import status.Enthralled;
import status.Shamed;
import characters.Attribute;
import characters.Character;
import characters.Trait;
import characters.body.PussyPart;

import combat.Combat;
import combat.Result;

public class FaceSit extends Skill {

	public FaceSit(Character self) {
		super("Facesit", self);
	}

	@Override
	public boolean requirements(Character user) {
		return user.getLevel()>=10 || user.get(Attribute.Seduction) > 30;
	}

	@Override
	public float priorityMod(Combat c) {
		return (self.has(Trait.lacedjuices) || self.has(Trait.addictivefluids) || self.body.getRandomPussy() == PussyPart.feral) ? 3.0f : 0;
	}

	@Override
	public boolean usable(Combat c, Character target) {
		return self.pantsless()&&self.canAct()&&c.getStance().dom(self)&&c.getStance().reachTop(self)&&
				!c.getStance().penetration(self)&&!c.getStance().penetration(target)&&c.getStance().prone(target)&&!self.has(Trait.shy);
	}

	@Override
	public String describe() {
		return "Shove your crotch into your opponent's face to demonstrate your superiority";
	}

	@Override
	public void resolve(Combat c, Character target) {
		if(self.has(Trait.entrallingjuices)&&Global.random(4)==0 && !target.wary()){
			if(self.human()){
				c.write(self,deal(c,0,Result.special, target));
			}else if(target.human()){
				c.write(self,receive(c,0,Result.special, target));
			}
			target.add(new Enthralled(target,self, 5));
		} else if (self.has(Trait.lacedjuices)) {
			if(self.human()){
				c.write(self,deal(c,0,Result.strong, target));
			}else if(target.human()){
				c.write(self,receive(c,0,Result.strong, target));
			}
		} else {
			if(self.human()){
				c.write(self,deal(c,0,Result.normal, target));
			}else if(target.human()){
				c.write(self,receive(c,0,Result.normal, target));
			}
		}

		int m = 10;
		if (target.has(Trait.silvertongue)) {
			m = m * 3 / 2;
		}
		if (self.hasBalls())
			self.body.pleasure(target, target.body.getRandom("mouth"), self.body.getRandom("balls"), m, c);					
		else {
			self.body.pleasure(target, target.body.getRandom("mouth"), self.body.getRandom("pussy"), m, c);
		}
		double n = (int) Math.round(4+Global.random(4));
		if (!c.getStance().behind(self)) {
			// opponent can see self
			n += 3 * self.body.getCharismaBonus(target);
		}
		if (target.has(Trait.imagination)) {
			n *= 1.5;
		}

		target.tempt(c, self, (int) Math.round(n));
		target.add(new Shamed(target));
		self.buildMojo(c, 50);
		if (c.getStance().enumerate() != Stance.facesitting) {
			c.setStance(new FaceSitting(self, target));
		}
	}

	@Override
	public Skill copy(Character user) {
		return new FaceSit(user);
	}

	@Override
	public Tactics type(Combat c) {
		if (c.getStance().enumerate() != Stance.facesitting) {
			return Tactics.positioning;
		} else {
			return Tactics.pleasure;			
		}
	}

	public String getLabel(Combat c){
		if(self.hasBalls()&&!self.hasPussy()){
			return "Teabag";
		}
		else if (c.getStance().enumerate() != Stance.facesitting){
			return "Facesit";
		} else {
			return "Ride Face";
		}
	}

	@Override
	public String deal(Combat c, int damage, Result modifier, Character target) {
		if(self.hasBalls()){
			if(modifier==Result.special){
				return "You crouch over "+target.nameOrPossessivePronoun()+" face and dunk your balls into her mouth. She can do little except lick them submissively, which does feel " +
						"pretty good. She's so affected by your manliness that her eyes glaze over and she falls under your control. Oh yeah. You're awesome.";
			}
			else if(modifier==Result.strong){
				return "You crouch over "+target.nameOrPossessivePronoun()+" face and dunk your balls into her mouth. She can do little except lick them submissively, which does feel " +
						"pretty good. Your powerful musk is clearly starting to turn her on. Oh yeah. You're awesome.";			}
			else{
				return "You crouch over "+target.nameOrPossessivePronoun()+" face and dunk your balls into her mouth. She can do little except lick them submissively, which does feel " +
						"pretty good. Oh yeah. You're awesome.";
			}
		}
		else{
			if(modifier==Result.special){
				return "You straddle "+target.nameOrPossessivePronoun()+" face and grind your pussy against her mouth, forcing her to eat you out. Your juices take control of her lust and " +
						"turn her into a pussy licking slave. Ooh, that feels good. You better be careful not to get carried away with this.";
			}
			else if(modifier==Result.strong){
				return "You straddle "+target.nameOrPossessivePronoun()+" face and grind your pussy against her mouth, forcing her to eat you out. She flushes and seeks more of your tainted juices. " +
						"Ooh, that feels good. You better be careful not to get carried away with this.";
			}
			else{
				return "You straddle "+target.nameOrPossessivePronoun()+" face and grind your pussy against her mouth, forcing her to eat you out. Ooh, that feels good. You better be careful " +
						"not to get carried away with this.";
			}
		}
	}

	@Override
	public String receive(Combat c, int damage, Result modifier, Character target) {
		if(self.hasBalls()){
			if(modifier==Result.special){
				return self.name()+" straddles your head and dominates you by putting her balls in your mouth. For some reason, your mind seems to cloud over and you're " +
						"desperate to please her. She gives a superior smile as you obediently suck on her nuts.";
			}
			else if(modifier==Result.strong){
				return self.name()+" straddles your head and dominates you by putting her balls in your mouth. Despite the humiliation, her scent is turning you on incredibly. " +
						"She gives a superior smile as you obediently suck on her nuts.";
			}
			else{
				return self.name()+" straddles your head and dominates you by putting her balls in your mouth. She gives a superior smile as you obediently suck on her nuts.";
			}
		} else {
			if(modifier==Result.special){
				return self.name()+" straddles your face and presses her pussy against your mouth. You open your mouth and start to lick her freely offered muff, but she just smiles " +
						"while continuing to queen you. As you swallow her juices, you feel her eyes start to bore into your mind. You can't resist her. You don't even want to.";
			}
			else if(modifier==Result.strong){
				return self.name()+" straddles your face and presses her pussy against your mouth. You open your mouth and start to lick her freely offered muff, but she just smiles " +
						"while continuing to queen you. You feel your body start to heat up as her juices flow into your mouth. She's giving you a mouthful of aphrodisiac straight from " +
						"the source.";
			}
			else{
				return self.name()+" straddles your face and presses her pussy against your mouth. You open your mouth and start to lick her freely offered muff, but she just smiles " +
						"while continuing to queen you. She clearly doesn't mind accepting some pleasure to demonstrate her superiority.";
			}
		}
	}

	@Override
	public boolean makesContact() {
		return true;
	}
}
