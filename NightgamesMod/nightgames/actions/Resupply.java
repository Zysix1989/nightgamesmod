package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.MatchType;
import nightgames.match.Participant;
import nightgames.match.ftc.FTCMatch;
import nightgames.modifier.standard.NudistModifier;

import java.util.Set;
import java.util.stream.Collectors;

public class Resupply extends Action {
    private static final long serialVersionUID = -3349606637987124335L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " heads for one of the safe rooms, probably to get a change of clothes.";
        }
    }

    private final boolean permissioned;
    private final Set<Character> validCharacters;

    public Resupply() {
        super("Resupply");
        permissioned = false;
        validCharacters = Set.of();
    }

    public Resupply(Set<Participant> validParticipants) {
        super("Resupply");
        permissioned = true;
        validCharacters = validParticipants.stream().map(Participant::getCharacter).collect(Collectors.toSet());
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound() && (!permissioned || validCharacters.contains(user));
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        if (Global.getMatch().getType() == MatchType.FTC) {
            FTCMatch match = (FTCMatch) Global.getMatch();
            if (user.getCharacter().human()) {
                Global.gui().message("You get a change of clothes from the chest placed here.");
            }
            if (user.getCharacter().has(Item.Flag) && !match.isPrey(user.getCharacter())) {
                match.turnInFlag(user.getCharacter());
            } else if (match.canCollectFlag(user.getCharacter())) {
                match.grabFlag();
            }
        } else {
            if (user.getCharacter().human()) {
                if (Global.getMatch().getCondition().name().equals(NudistModifier.NAME)) {
                    Global.gui().message(
                                    "You check in so that you're eligible to fight again, but you still don't get any clothes.");
                } else {
                    Global.gui().message("You pick up a change of clothes and prepare to get back in the fray.");
                }
            }
        }
        user.getCharacter().state = State.resupplying;
        return new Aftermath();
    }

}
