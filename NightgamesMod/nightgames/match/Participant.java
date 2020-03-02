package nightgames.match;

import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.global.Global;
import nightgames.status.Stsflag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Participant {
    private Character character;
    private int score = 0;

    // Participants this participant has defeated recently.  They are not valid targets until they
    // resupply.
    private Set<Participant> invalidTargets = new HashSet<>();

    public Participant(Character c) {
        this.character = c;
    }

    Participant(Participant p) {
        try {
            this.character = this.getCharacter().clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        this.score = p.score;
        this.invalidTargets = p.invalidTargets;
    }

    public Character getCharacter() {
        return character;
    }

    int getScore() {
        return score;
    }

    void incrementScore(int i) {
        score += i;
    }

    void defeated(Participant p) {
        assert !invalidTargets.contains(p);
        invalidTargets.add(p);
        incrementScore(1);
    }

    public Participant copy() {
        return new Participant(this);
    }

    void allowTarget(Participant p) {
        invalidTargets.remove(p);
    }

    public void place(Area loc) {
        character.location = loc;
        loc.place(this);
        if (loc.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    public boolean canStartCombat(Participant p2) {
        return character.eligible(p2.character);
    }

    public void move() {
        character.displayStateMessage();
        List<Action> possibleActions = new ArrayList<>();
        possibleActions.addAll(character.location.possibleActions(this));
        possibleActions.addAll(character.getItemActions());
        possibleActions.addAll(Global.getMatch().getAvailableActions());
        possibleActions.removeIf(a -> !a.usable(this));
        if (character.state == State.combat) {
            if (!character.location.fight.battle()) {
                Global.getMatch().resume();
            }
            return;
        } else if (character.busy > 0) {
            character.busy--;
            return;
        } else if (this.character.is(Stsflag.enthralled)) {
            character.handleEnthrall();
            return;
        } else if (character.state == State.shower || character.state == State.lostclothes) {
            character.bathe();
            return;
        } else if (character.state == State.crafting) {
            character.craft();
            return;
        } else if (character.state == State.searching) {
            character.search();
            return;
        } else if (character.state == State.resupplying) {
            character.resupply();
            return;
        } else if (character.state == State.webbed) {
            character.state = State.ready;
            return;
        } else if (character.state == State.masturbating) {
            character.masturbate();
            return;
        }
        character.move(possibleActions, character.location.encounter(character));
    }

    public void flee(Area area) {
        var options = character.location.possibleActions(this);
        var destinations = options.stream()
                .filter(action -> action instanceof Move)
                .map(action -> (Move) action)
                .map(Move::getDestination)
                .collect(Collectors.toList());
        var destination = destinations.get(Global.random(destinations.size()));
        character.notifyFlight(destination);
        character.travel(destination);
        area.endEncounter();
    }
}
