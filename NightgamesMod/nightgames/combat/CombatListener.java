package nightgames.combat;

import nightgames.pet.PetCharacter;

public abstract class CombatListener {

    protected final Combat c;
    
    public CombatListener(Combat c) {
        this.c = c;
    }

    public void prePetBattle(PetCharacter a, PetCharacter b) {
        
    }
}
