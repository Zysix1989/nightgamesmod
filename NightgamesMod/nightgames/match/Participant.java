package nightgames.match;

import nightgames.characters.Character;

class Participant {
    private Character character;

    Participant(Character c) {
        this.character = c;
    }

    public Character getCharacter() {
        return character;
    }
}
