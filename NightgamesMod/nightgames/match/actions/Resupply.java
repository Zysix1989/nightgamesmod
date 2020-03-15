package nightgames.match.actions;

import edu.emory.mathcs.backport.java.util.Collections;
import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.MatchType;
import nightgames.match.Participant;
import nightgames.match.ftc.FTCMatch;
import nightgames.modifier.standard.NudistModifier;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Resupply extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath(Participant usedAction) {
            super(usedAction);
        }

        @Override
        public String describe(Character c) {
            return " heads for one of the safe rooms, probably to get a change of clothes.";
        }
    }

    public final class Instance extends Action.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            if (Global.getMatch().getType() == MatchType.FTC) {
                FTCMatch match = (FTCMatch) Global.getMatch();
                user.getCharacter().message("You get a change of clothes from the chest placed here.");
                if (user.getCharacter().has(Item.Flag) && !match.isPrey(user.getCharacter())) {
                    match.turnInFlag(user);
                } else if (match.canCollectFlag(user.getCharacter())) {
                    match.grabFlag();
                }
            } else {
                if (Global.getMatch().getCondition().name().equals(NudistModifier.NAME)) {
                    user.getCharacter().message(
                            "You check in so that you're eligible to fight again, but you still don't get any clothes.");
                } else {
                    user.getCharacter().message("You pick up a change of clothes and prepare to get back in the fray.");
                }
            }
            user.state = new State();
            messageOthersInLocation(new Aftermath(user).describe());
        }
    }

    public class State implements Participant.State {

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.invalidAttackers.clear();
            p.getCharacter().change();
            p.state = new Ready();
            p.getCharacter().getWillpower().renew();
            if (p.getLocation().getOccupants().size() > 1) {
                var escapeRoute = escapeRoutes.stream().filter(EscapeRoute::usable).findFirst();
                escapeRoute.or(() -> {
                    var shuffledRoutes = new ArrayList<>(escapeRoutes);
                    Collections.shuffle(shuffledRoutes);
                    return shuffledRoutes.stream().findFirst();
                }).ifPresent(route -> route.use(p));
            }
        }

        @Override
        public boolean isDetectable() {
            return true;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(Encounter encounter, Participant p, Participant other) {
            throw new UnsupportedOperationException(String.format("%s can't be attacked while resupplying",
                    p.getCharacter().getTrueName()));
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("%s can't be attacked while resupplying",
                    p.getCharacter().getTrueName()));
        }

    }

    public static final class EscapeRoute {
        private final Area destination;
        private final String message;

        public EscapeRoute(Area destination, String message) {
            this.destination = destination;
            this.message = message;
        }

        public boolean usable() {
            return destination.getOccupants().isEmpty();
        }

        public void use(Participant p) {
            p.travel(destination, message);
        }
    }

    private final boolean permissioned;
    private final Set<Character> validCharacters;
    private final Set<EscapeRoute> escapeRoutes;

    private Resupply(Set<Participant> validParticipants, Set<EscapeRoute> escapeRoutes) {
        super("Resupply");
        permissioned = !validParticipants.isEmpty();
        validCharacters = validParticipants.stream().map(Participant::getCharacter).collect(Collectors.toSet());
        this.escapeRoutes = escapeRoutes;
    }

    public static Resupply withEscapeRoutes(Set<EscapeRoute> escapeRoutes) {
        return new Resupply(Set.of(), escapeRoutes);
    }

    public static Resupply limitToCharacters(Set<Participant> validParticipants) {
        return new Resupply(validParticipants, Set.of());
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound() && (!permissioned || validCharacters.contains(user.getCharacter()));
    }

    @Override
    public Instance newInstance(Participant user, Area location) {
        return new Instance(user, location);
    }

}
