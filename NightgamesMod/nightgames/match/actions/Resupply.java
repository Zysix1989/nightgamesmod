package nightgames.match.actions;

import nightgames.areas.AreaIdentity;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.MatchType;
import nightgames.match.Participant;
import nightgames.match.ftc.FTCMatch;
import nightgames.modifier.standard.NudistModifier;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Resupply extends Action {

    private static final class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return " heads for one of the safe rooms, probably to get a change of clothes.";
        }
    }

    public static class State implements Participant.State {

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
                if (p.getLocation().id() == AreaIdentity.dorm) {
                    if (Global.getMatch().gps("Quad").orElseThrow().getOccupants().isEmpty()) {
                        p.travel(Global.getMatch().gps("Quad").orElseThrow(),
                                "You hear your opponents searching around the "
                                        + "dorm, so once you finish changing, you hop out the window and "
                                        + "head to the quad.");
                    } else {
                        p.travel(Global.getMatch().gps("Laundry").orElseThrow(),
                                "You hear your opponents searching around "
                                        + "the dorm, so once you finish changing, you quietly move "
                                        + "downstairs to the laundry room.");
                    }
                }
                if (p.getLocation().id() == AreaIdentity.union) {
                    if (Global.getMatch().gps("Quad").orElseThrow().getOccupants().isEmpty()) {
                        p.travel(Global.getMatch().gps("Quad").orElseThrow(),
                                "You don't want to be ambushed leaving the "
                                        + "student union, so once you finish changing, you hop out the "
                                        + "window and head to the quad.");
                    } else {
                        p.travel(Global.getMatch().gps("Pool").orElseThrow(),
                                "You don't want to be ambushed leaving "
                                        + "the student union, so once you finish changing, you sneak out "
                                        + "the back door and head to the pool.");
                    }
                }
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
        return !user.getCharacter().bound() && (!permissioned || validCharacters.contains(user.getCharacter()));
    }

    @Override
    public Action.Aftermath execute(Participant user) {
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
        return new Aftermath();
    }
}
