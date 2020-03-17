package nightgames.combat;

import nightgames.characters.Character;

public interface Intelligence {
    /**
     * @param c combat to act in
     * @param target
     * @return true if combat should be paused.
     */
    boolean act(Combat c, Character target);
}
