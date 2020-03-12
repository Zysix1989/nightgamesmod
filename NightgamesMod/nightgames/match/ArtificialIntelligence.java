package nightgames.match;

import nightgames.characters.NPC;

public class ArtificialIntelligence implements Intelligence {
    private NPC character;

    public ArtificialIntelligence(NPC character) {
        this.character = character;
    }
}
