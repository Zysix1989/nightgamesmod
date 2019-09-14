package nightgames.pet.arms.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.pet.arms.Arm;

public class Idle extends ArmSkill {
    public Idle() {
        super("Idle", 0);
    }

    @Override
    public boolean resolve(Combat c, Arm arm, Character owner, Character target) {
        return true;
    }
}
