package nightgames.match.team;

import java.util.Arrays;
import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.match.Prematch;
import nightgames.match.PrematchEvent;
import nightgames.modifier.standard.NoModifier;

public class TeamPrematch extends Prematch {

    public static final String DID_FIRST_TEAM_MATCH_FLAG = "DidFirstTeamMatch";
    private static final String START_MATCH_CHOICE = "Let's Go!";
    public TeamPrematch() {
        super(new FirstTeamMatchEvent(), new TeamMatchEvent());
    }

    @Override
    public void respond(String response) {
        if (response.equals(START_MATCH_CHOICE)) {
            Global.setUpMatch(type);
            return;
        }
        Global.gui().message("<b>ERROR: Unimplemented option '" + response + "' in TeamPrematch.</b>");
        System.err.println("ERROR: Unimplemented option '" + response + "' in TeamPrematch.</b>");
        Thread.dumpStack();
    }
    
    private static class FirstTeamMatchEvent extends PrematchEvent {


        private FirstTeamMatchEvent() {
            super("You received a text earlier in the day saying you'd need to be at the meeting spot"
                            + " in the Student Union a little earlier than usual, as there would be"
                            + " some announcements. When you arrive, everyone else has already gathered. <i>\""
                            + Global.getPlayer().getTrueName() + ", good, now we can begin. We are going to"
                            + " do things a little differently tonight. Instead of having a rapacious"
                            + " free-for-all, you are going to have to work together this time. I'm going"
                            + " to split you up into teams, and you will be fighting alongside your"
                            + " teammembers the whole night. Each team is going to have a team captain"
                            + " who calls the shots. Try not to get too tyrannical on your teammates, captains."
                            + " A fight is over when the team captain can no longer continue, regardless of"
                            + " the state of the other teammembers. That means you would do well to do"
                            + " whatever you can to keep your captain in good condition! After a fight,"
                            + " the winning team gets three points, or both get one in case of a draw."
                            + " At the end of the night, everyone gets paid according to the amount"
                            + " of points their teams scored. I'll try to balance the teams in terms"
                            + " of skill, but no promises there. Now, any further questions?\"</i>",
                new NoModifier(), Arrays.asList(GUI.sceneOption(START_MATCH_CHOICE)));
        }
        
        @Override
        protected void extraEffects() {
            Global.flag(DID_FIRST_TEAM_MATCH_FLAG);
        }

        @Override
        protected boolean valid() {
            return !Global.checkFlag(DID_FIRST_TEAM_MATCH_FLAG);
        }
        
    }
    
    private static class TeamMatchEvent extends PrematchEvent {

        private TeamMatchEvent() {
            super("Lilly seems to have a special spring in her step as she walks into the Student"
                  + " Union, where the rest of you were already waiting. We're going to do another team"
                  + " match today! You know the drill, line up and I'll give you your team assignment!"
                , new NoModifier(), Arrays.asList(GUI.sceneOption(START_MATCH_CHOICE)));
        }
        
        @Override
        protected void extraEffects() {
            
        }

        @Override
        protected boolean valid() {
            return true;
        }
        
        
    }

}
