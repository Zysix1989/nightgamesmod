package nightgames.match;

import nightgames.characters.Character;
import nightgames.combat.Combat;

public interface Dialog {
    void intrudeInCombat(Combat c, Character target, Character assist);
    void assistedByIntruder(Combat c, Character target, Character assist);
}
