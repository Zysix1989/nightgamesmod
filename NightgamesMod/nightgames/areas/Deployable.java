package nightgames.areas;

import nightgames.match.Participant;

public interface Deployable {
    boolean resolve(Participant active);
}
