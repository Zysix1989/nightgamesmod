package nightgames.combat;

import nightgames.pet.PetCharacter;

public class Assistant {
    private PetCharacter character;

    Assistant(PetCharacter c) {
        this.character = c;
    }

    Assistant(Assistant a) {
        this.character = a.character;
    }

    public Assistant copy() {
        return new Assistant(this);
    }

    public PetCharacter getCharacter() {
        return this.character;
    }


}
