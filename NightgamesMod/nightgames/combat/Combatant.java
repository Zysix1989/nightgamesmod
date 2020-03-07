package nightgames.combat;

import nightgames.characters.Character;
import nightgames.match.Participant;

class Combatant {
    private Participant participant;

    Combatant(Participant p) {
        participant = p;
    }

    private Combatant(Combatant c) {
            participant = c.participant.copy();
    }

    Character getCharacter() {
        return participant.getCharacter();
    }

    Participant getParticipant() { return participant; }

    Combatant copy() {
        return new Combatant(this);
    }
}
