package nightgames.actions;

import nightgames.characters.Character;
import nightgames.match.Participant;

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
        user.state = new Participant.ShowerState(endMessage);
        user.waitRounds(1);
        return new Aftermath();
    }

    public static final String SHOWER_START_MESSAGE = "It's a bit dangerous, but a shower sounds especially inviting right now.";
    public static final String SHOWER_END_MESSAGE = "You let the hot water wash away your exhaustion and soon you're back to peak condition.";
    public static final String POOL_START_MESSAGE = "There's a jacuzzi in the pool area and you decide to risk a quick soak.";
    public static final String POOL_END_MESSAGE = "The hot water soothes and relaxes your muscles. You feel a bit exposed, skinny-dipping in such an open area. You decide it's time to get moving.";

}
