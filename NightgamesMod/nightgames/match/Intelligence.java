package nightgames.match;

import java.util.Collection;
import java.util.function.Consumer;

public interface Intelligence {
    void move(Collection<Action> possibleActions,
              Consumer<Action> callback);
}
