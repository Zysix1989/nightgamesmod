package nightgames.actions;

import nightgames.characters.Character;
import nightgames.match.Participant;
import nightgames.match.defaults.DefaultEncounter;

import java.util.Optional;

public class Bathe extends Action {

    /**
     * 
     */
    private static final long serialVersionUID = 4565550545479306251L;
    private static class Aftermath extends Action.Aftermath {
        private Aftermath() { }

        @Override
        public String describe(Character c) {
            return " start bathing in the nude, not bothered by your presence.";
        }
    }

    public static class ShowerState implements Participant.PState {
        private boolean clothesStolen = false;
        private String message;

        public ShowerState(String message) {
            this.message = message;
        }

        @Override
        public boolean allowsNormalActions() {
            return false;
        }

        @Override
        public void move(Participant p) {
            p.getCharacter().bathe();
            p.getCharacter().message(message);
            if (clothesStolen) {
                p.getCharacter().message("Your clothes aren't where you left them. Someone must have come by and taken them.");
            }
            p.state = new Ready();
        }

        @Override
        public boolean isDetectable() {
            return true;
        }

        @Override
        public Optional<Runnable> eligibleCombatReplacement(DefaultEncounter encounter, Participant p, Participant other) {
            if (!clothesStolen) {
                return Optional.of(() -> encounter.showerScene(other, p));
            }
            return Optional.empty();
        }

        @Override
        public Optional<Runnable> ineligibleCombatReplacement(Participant p, Participant other) {
            return Optional.empty();
        }

        @Override
        public int spotCheckDifficultyModifier(Participant p) {
            throw new UnsupportedOperationException(String.format("spot check for %s should have already been replaced",
                    p.getCharacter().getTrueName()));
        }

        @Override
        public void sendAssessmentMessage(Participant p, Character observer) {
            observer.message("She is completely naked.");
        }

        public void stealClothes() {
            assert !clothesStolen;
            clothesStolen = true;
        }
    }


    private final String startMessage;
    private final String endMessage;

    public Bathe(String startMessage, String endMessage) {
        super("Clean Up");
        this.startMessage = startMessage;
        this.endMessage = endMessage;
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        user.getCharacter().message(startMessage);
        String message = endMessage;
        user.state = new ShowerState(message);
        user.waitRounds(1);
        return new Aftermath();
    }

    public static final String SHOWER_START_MESSAGE = "It's a bit dangerous, but a shower sounds especially inviting right now.";
    public static final String SHOWER_END_MESSAGE = "You let the hot water wash away your exhaustion and soon you're back to peak condition.";
    public static final String POOL_START_MESSAGE = "There's a jacuzzi in the pool area and you decide to risk a quick soak.";
    public static final String POOL_END_MESSAGE = "The hot water soothes and relaxes your muscles. You feel a bit exposed, skinny-dipping in such an open area. You decide it's time to get moving.";

}
