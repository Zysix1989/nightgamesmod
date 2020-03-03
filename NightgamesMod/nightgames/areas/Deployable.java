package nightgames.areas;

import nightgames.match.Participant;

public interface Deployable {
    public boolean resolve(Participant active);
    
    default int priority() {return 5;}
}
