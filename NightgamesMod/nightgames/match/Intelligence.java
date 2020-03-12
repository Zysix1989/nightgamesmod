package nightgames.match;

import nightgames.trap.Trap;

import java.util.Collection;
import java.util.function.Consumer;

public interface Intelligence {
    void move(Collection<Action> possibleActions,
              Consumer<Action> callback);

    void promptTrap(Participant target, Trap.Instance trap, Runnable attackContinuation, Runnable waitContinuation);

}
