package nightgames.areas;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.match.Participant;

public class NinjaStash implements Deployable {

    private Character owner;

    public NinjaStash(Character owner){
        this.owner = owner;
    }
    @Override
    public boolean resolve(Participant active) {
        if(owner==active.getCharacter()&&active.getCharacter().human()){
            Global.gui().message("You have a carefully hidden stash of emergency supplies here. You can replace your clothes and collect the items if you need to.");
        }
        return false;
    }

    public String toString(){
        return "Ninja Stash";
    }

    @Override
    public Character owner() {
        return owner;
    }
    
    @Override
    public int priority() {
        return 0;
    }

}
