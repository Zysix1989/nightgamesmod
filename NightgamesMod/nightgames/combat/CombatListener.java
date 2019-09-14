package nightgames.combat;

import java.util.Optional;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.pet.PetCharacter;
import nightgames.skills.Skill;
import nightgames.stance.Position;

public abstract class CombatListener {

    protected final Combat c;
    
    public CombatListener(Combat c) {
        this.c = c;
    }
    
    public void preStart() {
        
    }
    
    public void prePetBattle(PetCharacter a, PetCharacter b) {
        
    }

    public void postTurn() {
        
    }
    
    public void postEnd(Optional<Character> winner) {
        
    }
    
}
