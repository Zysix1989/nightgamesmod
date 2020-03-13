package nightgames.match;

import nightgames.match.actions.Move;
import nightgames.trap.Trap;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface Intelligence {
    void move(Collection<Action> possibleActions,
              Consumer<Action> callback);

    void promptTrap(Participant target, Trap.Instance trap, Runnable attackContinuation, Runnable waitContinuation);

    void faceOff(Participant opponent, Runnable fightContinuation, Runnable fleeContinuation, Runnable smokeContinuation);

    void spy(Participant opponent, Runnable ambushContinuation, Runnable waitContinuation);

    void showerScene(Participant target, Runnable ambushContinuation, Runnable stealContinuation, Runnable aphrodisiacContinuation, Runnable waitContinuation);

    void intrudeInCombat(Set<Encounter.IntrusionOption> intrusionOptions, List<Move> possibleMoves, Consumer<Action> actionCallback, Runnable neitherContinuation);

}
