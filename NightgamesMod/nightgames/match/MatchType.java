package nightgames.match;

import nightgames.characters.Character;
import nightgames.match.defaults.DefaultPrematch;
import nightgames.match.ftc.FTCMatch;
import nightgames.match.ftc.FTCPrematch;
import nightgames.modifier.BaseModifier;
import nightgames.modifier.standard.FTCModifier;
import nightgames.modifier.standard.NoModifier;

import java.util.Collection;

public enum MatchType {
    NORMAL,
    FTC;

    public Match buildMatch(Collection<Character> combatants, BaseModifier condition) {
        switch (this) {
            case FTC:
                assert condition instanceof FTCModifier;
                if (combatants.size() != 5) {
                    return Match.newMatch(combatants, new NoModifier());
                }
                return FTCMatch.newMatch(combatants, (FTCModifier) condition);
            case NORMAL:
                return Match.newMatch(combatants, condition);
            default:
                throw new Error();
        }
    }
    
    public Prematch buildPrematch() {
        switch (this) {
            case FTC:
                return new FTCPrematch();
            case NORMAL:
                return new DefaultPrematch();
            default:
                throw new Error();
        }
    }
    
    public void runPrematch() {
        buildPrematch().run();
    }
}
