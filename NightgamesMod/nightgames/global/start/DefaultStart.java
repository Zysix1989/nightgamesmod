package nightgames.global.start;

import nightgames.global.Global;
import nightgames.match.MatchType;
import nightgames.modifier.standard.NoModifier;

public class DefaultStart implements GameStarter {

    @Override
    public void startGame(MatchType type) {
        Global.setUpMatch(type, new NoModifier());
    }

}
