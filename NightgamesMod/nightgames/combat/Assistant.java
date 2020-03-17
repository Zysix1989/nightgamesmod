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

    public Character getMaster() {
        return this.master;
    }

    public double getFitness() {
        return  (10 + character.getSelf().power()) * ((100 + character.percentHealth()) / 200.0) / 2;
    }

    public void vanquish(Combat c, Assistant other) {
        character.getSelf().vanquish(c, other.getCharacter().getSelf());
    }


}
