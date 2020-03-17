package nightgames.combat;

import nightgames.characters.Character;

public interface Intelligence {
    boolean act(Combat c, Character target);
}
