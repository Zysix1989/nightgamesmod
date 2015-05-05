package skills;

import stance.Cowgirl;
import stance.Missionary;
import combat.Combat;
import combat.Result;

import global.Global;
import characters.Attribute;
import characters.Character;

public class CounterDrain extends CounterBase {
	public CounterDrain(Character self) {
		super("Counter Vortex", self, 6, Global.format("{self:SUBJECT-ACTION:glow|glows} with a purple light.", self, self));
	}

	public float priorityMod(Combat c) {
		return Global.randomfloat() * 3;
	}

	@Override
	public void resolveCounter(Combat c, Character target) {
		if (self.human()) {
			c.write(self, deal(c, 0, Result.normal, target));
		} else {
			c.write(self, receive(c, 0, Result.normal, target));
		}
		if (target.hasDick() && self.hasPussy()) {
			c.setStance(new Cowgirl(self, target));
		} else {
			c.setStance(new Missionary(self, target));
		}
		Drain drain = new Drain(self);
		drain.resolve(c, target, true);
	}

	@Override
	public boolean requirements(Character user) {
		return user.get(Attribute.Dark) > 25;
	}

	@Override
	public boolean usable(Combat c, Character target) {
		return !c.getStance().dom(self) && !c.getStance().dom(target) && self.canAct()
				&& self.pantsless() && target.pantsless()
				&&((self.hasDick() && target.hasPussy()) || (self.hasPussy() && target.hasDick()))
				&&self.canSpend(getMojoCost());
	}

	public int getMojoCost() {
		return 30;
	}

	@Override
	public String describe() {
		return "Counter with Drain";
	}

	@Override
	public Skill copy(Character user) {
		return new CounterDrain(user);
	}

	@Override
	public Tactics type(Combat c) {
		return Tactics.pleasure;
	}

	@Override
	public String deal(Combat c, int damage, Result modifier, Character target) {
		if (modifier == Result.setup) {
			return Global.format("You drop your stance, take a deep breath and close your eyes. A purple glow starts radiating from your core.", self, target);
		} else {
			return Global.format("You suddenly open your eyes as you sense {other:name} approaching. "
						+ "The purple light that surrounds you suddenly flies into {other:direct-object}, "
						+ "eliciting a cry out of her. She collapses like a puppet with her strings cut and falls to the ground. "
						+ "Seeing the opportunity, you smirk and leisurely mount her.", self, target);
		}
	}

	@Override
	public String receive(Combat c, int damage, Result modifier,
			Character target) {
		if (modifier == Result.setup) {
			return Global.format("She drops her stance, takes a deep breath and closes her eyes. You notice a purple glow begin to radiate from her core.", self, target);
		} else {
			return Global.format("{self:SUBJECT} suddenly opens her eyes as you approach. "
						+ "The purple light that was orbiting around her suddenly reverses directions and flies into you. "
						+ "The purple energy seem to paralyze your muscles and you collapse like a puppet with your strings cut."
						+ "You can't help but fall to the ground with a cry. "
						+ "Seeing the opportunity, she smirks and leisurely mounts you.", self, target);
		}
	}
}
