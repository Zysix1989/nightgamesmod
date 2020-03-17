package nightgames.combat;

import nightgames.characters.Character;
import nightgames.characters.NPC;

public class ArtificialIntelligence implements Intelligence {
    private final NPC character;

    public ArtificialIntelligence(NPC character) {
        this.character = character;
    }

    @Override
    public boolean act(Combat c, Character target) {
        return character.act(c, target);
    }
}
