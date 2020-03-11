package nightgames.match;

import nightgames.actions.Action;
import nightgames.actions.Move;
import nightgames.actions.Resupply;
import nightgames.areas.Area;
import nightgames.areas.Challenge;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Stsflag;

import java.util.*;
import java.util.stream.Collectors;

public class Participant {

    // Below, the participant 'p' is the one who holds the state
    public interface State {
        boolean allowsNormalActions();
        void move(Participant p);
        boolean isDetectable();
        Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other);
        Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other);
        int spotCheckDifficultyModifier(Participant p);
        default void sendAssessmentMessage(Participant p, Character observer) {
            if (p.getCharacter().mostlyNude()) {
                observer.message("She is completely naked.");
            } else {
                observer.message("She is dressed and ready to fight.");
            }
        }
    }

    protected Character character;
    private int score = 0;
    public State state = new Action.Ready();
    public Set<Participant> invalidAttackers = new HashSet<>();
    public List<Challenge> challenges = new ArrayList<>();

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
        this.state = p.state;
        this.invalidAttackers = new HashSet<>(p.invalidAttackers);
    }

    public Participant copy() {
        return new Participant(this);
    }

    public Character getCharacter() {
        return character;
    }

    int getScore() {
        return score;
    }

    public void incrementScore(int amt, String reason) {
        score += amt;
        getLocation()
                .getOccupants()
                .forEach(p -> p.getCharacter().message(Match.scoreString(getCharacter(), amt, reason)));
    }

    public void place(Area loc) {
        character.location.set(loc);
        loc.place(this);
        if (loc.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    public boolean canStartCombat(Participant p2) {
        return !p2.invalidAttackers.contains(this) && !(p2.state instanceof Resupply.State);
    }

    public interface ActionCallback {
        Action.Aftermath execute(Action a);
    }

    public void move() {
        character.displayStateMessage(character.location.get().getTrap(this));

        var possibleActions = new ArrayList<Action>();
        possibleActions.addAll(character.location.get().possibleActions(this));
        possibleActions.addAll(character.getItemActions());
        possibleActions.addAll(Global.getMatch().getAvailableActions());
        possibleActions.removeIf(a -> !a.usable(this));
        if (state instanceof Combat.State) {
            state.move(this);
        } else if (this.character.is(Stsflag.enthralled)) {
            character.handleEnthrall(act -> act.execute(this));
            return;
        } else {
            state.move(this);
        }
        if (state.allowsNormalActions()) {
            if (character.location.get().encounter(this)) {
                character.move(possibleActions, act -> act.execute(this));
            }
        }
    }

    public void flee() {
        var options = character.location.get().possibleActions(this);
        var destinations = options.stream()
                .filter(action -> action instanceof Move)
                .map(action -> (Move) action)
                .map(Move::getDestination)
                .collect(Collectors.toList());
        var destination = destinations.get(Global.random(destinations.size()));
        travel(destination, "You dash away and escape into the <b>" + destination.name + ".</b>");
    }

    public void endOfMatchRound() {
        character.getTraits().forEach(trait -> {
            if (trait.status != null) {
                nightgames.status.Status newStatus = trait.status.instance(character, null);
                if (!character.has(newStatus)) {
                    character.addNonCombat(new nightgames.match.Status(newStatus));
                }
            }
        });
        character.endOfMatchRound();
    }

    public Area getLocation() { return character.location(); }

    public void travel(Area dest) {
        state = new Action.Ready();
        character.location.get().exit(this.character);
        character.location.set(dest);
        dest.enter(this.character);
        if (dest.name.isEmpty()) {
            throw new RuntimeException("empty location");
        }
    }

    public void travel(Area dest, String message) {
        travel(dest);
        character.notifyTravel(dest, message);
    }

    public void timePasses() {}

    public void interveneInCombat(Set<Encounter.IntrusionOption> intrusionOptions, Runnable noneContinuation) {
        character.intrudeInCombat(intrusionOptions,
                character.location.get().possibleActions(this).stream()
                        .filter(act -> act instanceof Move)
                        .map(act -> (Move) act)
                        .collect(Collectors.toList()), act -> act.execute(this), noneContinuation
        );
    }

    public void invalidateAttacker(Participant victor) {
        invalidAttackers.add(victor);
    }

    void finishMatch() {
        character.finishMatch();
        invalidAttackers.clear();
    }

    public int pointsForVictory(Participant loser) {
        return loser.pointsGivenToVictor();
    }

    protected int pointsGivenToVictor() {
        return 1;
    }

    public void accept(Challenge c) {
        challenges.add(c);
    }

    public void evalChallenges(Combat c, Character victor) {
        for (Challenge chal : challenges) {
            chal.check(c, victor);
        }
    }
}
