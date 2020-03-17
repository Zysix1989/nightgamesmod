package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.Player;

public class HumanIntelligence implements Intelligence {
    private final Player character;

    public HumanIntelligence(Player character) {
        this.character = character;
    }

    @Override
    public boolean act(Combat c, Character target) {
        return character.act(c, target);
    }
}
