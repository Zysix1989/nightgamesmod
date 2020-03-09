package nightgames.combat;

import nightgames.characters.Character;
import nightgames.match.Participant;

public class Combatant {
    private Participant participant;

    Combatant(Participant p) {
        participant = p;
        participant.getCharacter().orgasms = 0;
    }

    private Combatant(Combatant c) {
            participant = c.participant.copy();
    }

    public Character getCharacter() {
        return participant.getCharacter();
    }

    Participant getParticipant() { return participant; }

    Combatant copy() {
        return new Combatant(this);
    }
}
