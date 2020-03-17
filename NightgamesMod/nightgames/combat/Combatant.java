package nightgames.combat;

import nightgames.characters.Character;
import nightgames.match.Participant;

public class Combatant {
    private Participant participant;
    private final Intelligence intelligence;

    Combatant(Participant p) {
        participant = p;
        participant.getCharacter().orgasms = 0;
        intelligence = participant.makeCombatIntelligence();
    }

    private Combatant(Combatant c) {
            participant = c.participant.copy();
            intelligence = participant.makeCombatIntelligence();
    }

    public Character getCharacter() {
        return participant.getCharacter();
    }

    Participant getParticipant() { return participant; }

    Combatant copy() {
        return new Combatant(this);
    }

    boolean act(Combat c, Combatant target) {
        return intelligence.act(c, target.getCharacter());
    }
}
