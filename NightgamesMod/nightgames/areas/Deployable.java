package nightgames.areas;

import nightgames.characters.Character;
import nightgames.match.Participant;

public interface Deployable {
    public boolean resolve(Participant active);

    public Character owner();
    
    default int priority() {return 5;}
}
