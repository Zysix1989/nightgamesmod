package nightgames.match;

import nightgames.characters.Player;

public class HumanIntelligence implements Intelligence {
    private Player character;

    public HumanIntelligence(Player character) {
        this.character = character;
    }
}
