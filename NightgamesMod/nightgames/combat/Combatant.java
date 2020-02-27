package nightgames.combat;

import nightgames.characters.Character;

class Combatant {
    private Character character;

    Combatant(Character c) {
        character = c;
    }

    private Combatant(Combatant c) {
        try {
            character = c.character.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    Character getCharacter() {
        return character;
    }

    Combatant copy() {
        return new Combatant(this);
    }
}
