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
    
    public void preTurn() {
        
    }
    
    public void preSkillSelection() {
        
    }
    
    public void postSkillSelection(Skill p1Chosen, Skill p2Chosen) {
        
    }
    
    public void prePetActions() {
        
    }
    
    public void prePetBattle(PetCharacter a, PetCharacter b) {
        
    }
    
    public void postPetBattle(PetCharacter a, PetCharacter b) {
        
    }
    
    public void prePetAction(PetCharacter pet) {
        
    }
    
    public void postPetAction(PetCharacter pet) {
        
    }
    
    public void postPetActions(boolean didAny) {
        
    }
    
    public void preActions() {
        
    }
    
    public void preAction(Character user, Character target, Skill action) {
        
    }
    
    public void onActionTurnedToWorship(Character user, Character target, Skill original, Skill worship) {
        
    }
    
    public void onActionSuccess(Character user, Character target, Skill action) {
        
    }
    
    public void onActionCountered(Character user, Character target, Skill action) {
        
    }
    
    public void onActionFailed(Character user, Character target, Skill action) {
        
    }
    
    public void postAction(Character user, Character target, Skill action) {
        
    }
    
    public void postTurn() {
        
    }
    
    public void preOrgasm(Character self, Character source, BodyPart selfPart, BodyPart sourcePart) {
        
    }
    
    public void postOrgasm(Character self, Character source, BodyPart selfPart, BodyPart sourcePart) {
        
    }
    
    public void onWinded(Character winded) {
        
    }
    
    public void onStanceChange(Position old, Position nw, Character initiator, boolean voluntary) {
        
    }
    
    public void preEnd() {
        
    }
    
    public void postEnd(Optional<Character> winner) {
        
    }

    public void onPetAdded(PetCharacter self) {

    }

    public void onPetRemoved(PetCharacter self) {

    }
    
}
