package nightgames.combat;

import nightgames.characters.Character;
import nightgames.pet.PetCharacter;

public class Assistant {
    private PetCharacter character;
    private Character master;

    Assistant(PetCharacter c, Character master) {
        this.character = c;
        this.master = master;
    }

    Assistant(Assistant a) {
        try {
            this.character = a.character.cloneWithOwner(a.master);
            this.master = a.master;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Assistant copy() {
        return new Assistant(this);
    }

    public PetCharacter getCharacter() {
        return this.character;
    }


}
