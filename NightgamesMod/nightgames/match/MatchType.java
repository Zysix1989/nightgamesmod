package nightgames.match;

import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.match.defaults.DefaultPrematch;
import nightgames.match.ftc.FTCMatch;
import nightgames.match.ftc.FTCPrematch;
import nightgames.modifier.Modifier;
import nightgames.modifier.standard.FTCModifier;
import nightgames.modifier.standard.NoModifier;

import java.util.Collection;

public enum MatchType {
    NORMAL,
    FTC;

    public Match buildMatch(Collection<Character> combatants, Modifier condition) {
        switch (this) {
            case FTC:
                assert condition.name().equals(FTCModifier.NAME);
                if (combatants.size() != 5) {
                    return Match.newMatch(combatants, new NoModifier());
                }
                return FTCMatch.newMatch(combatants, ((FTCModifier) condition).getPrey());
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
    
    //exists entirely to make debugging with MatchModifierPicker.java work. runs a default event
    //with the given modifier while ignoring any requirements
    public void runWith(Modifier m) {
        Global.unflag(Flag.victory);
        DefaultPrematch selection = new DefaultPrematch();
        selection.type = m;
        Global.current = selection;
        selection.respond("Do");
    }

}
