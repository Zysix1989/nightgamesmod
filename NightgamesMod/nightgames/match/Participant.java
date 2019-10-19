package nightgames.match;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import nightgames.characters.Character;

public class Participant {
    private Character character;
    private int score = 0;

    // Participants this participant has defeated recently.  They are not valid targets until they
    // resupply.
    private Set<Participant> invalidTargets = new HashSet<>();

    Participant(Character c) {
        this.character = c;
    }

    public Character getCharacter() {
        return character;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int i) {
        score += i;
    }

    public void defeated(Participant p) {
        assert !invalidTargets.contains(p);
        invalidTargets.add(p);
        incrementScore(1);
    }

    public void allowTarget(Participant p) {
        invalidTargets.remove(p);
    }
}
