package nightgames.combat;

import nightgames.characters.Character;

class Combatant {
    private Character character;

    Combatant(Character c) {
        character = c;
    }

    Character getCharacter() {
        return character;
    }
}
