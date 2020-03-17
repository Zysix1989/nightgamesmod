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
        this.character = a.character;
        this.master = a.master;
    }

    public Assistant copy() {
        return new Assistant(this);
    }

    public PetCharacter getCharacter() {
        return this.character;
    }


}
