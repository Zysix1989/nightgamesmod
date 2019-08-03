package nightgames.match.defaults;

import java.util.ArrayList;
import java.util.List;
import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.gui.GUI;
import nightgames.match.Postmatch;

public class DefaultPostmatch extends Postmatch {

    private boolean normal;

    public DefaultPostmatch(List<Character> combatants) {
        super(combatants);
        normal = true;
    }

    @Override
    public void respond(String response) {
        if (response.startsWith("Next")) {
            normal();
        }
    }

    private void events() {
        String message = "";
        List<CommandPanelOption> choice = new ArrayList<>();
        if (Global.checkFlag(Flag.metLilly) && !Global.checkFlag(Flag.challengeAccepted) && Global.random(10) >= 7) {
            message = message
                            + "When you gather after the match to collect your reward money, you notice Jewel is holding a crumpled up piece of paper and ask about it. <i>\"This? I found it lying on the ground during the match. It seems to be a worthless piece of trash, but I didn't want to litter.\"</i> Jewel's face is expressionless, but there's a bitter edge to her words that makes you curious. You uncrumple the note and read it.<br/><br/>'Jewel always acts like the dominant, always-on-top tomboy, but I bet she loves to be held down and fucked hard.'<br/><br/><i>\"I was considering finding whoever wrote the note and tying his penis in a knot,\"</i> Jewel says, still impassive. <i>\"But I decided to just throw it out instead.\"</i> It's nice that she's learning to control her temper, but you're a little more concerned with the note. It mentions Jewel by name and seems to be alluding to the Games. You doubt one of the other girls wrote it. You should probably show it to Lilly.<br/><br/><i>\"Oh for fuck's sake..\"</i> Lilly sighs, exasperated. <i>\"I thought we'd seen the last of these. I don't know who writes them, but they showed up last year too. I'll have to do a second sweep of the grounds each night to make sure they're all picked up by morning. They have competitors' names on them, so we absolutely cannot let a normal student find one.\"</i> She toys with a pigtail idly while looking annoyed. <i>\"For what it's worth, they do seem to pay well if you do what the note says that night. Do with them what you will.\"</i><br/>";

            Global.flag(Flag.challengeAccepted);
            choice.add(GUI.sceneOption("Next"));
        }
        if (!message.equals("")) {
            Global.gui().prompt(message, choice);
        }
    }

    private void normal() {
        Character closest = null;
        int maxaffection = 0;
        for (Character rival : combatants) {
            if (rival.getAffection(player) > maxaffection) {
                closest = rival;
                maxaffection = rival.getAffection(player);
            }
        }
        //TODO: Fix Angel always being picked for the afterparty event. Given how often she can win, it's not a surprise she ends up with highest affection? - DSM
        if (maxaffection >= 15 && closest != null) {
            closest.afterParty();
        } else {
            Global.gui().message("You walk back to your dorm and get yourself cleaned up.");
        }
    }

    @Override
    protected void runInternal() {
        for (Character self : combatants) {
            for (Character other : combatants) {
                if (self != other && self.getAffection(other) >= 1 && self.getAttraction(other) >= 20) {
                    self.gainAttraction(other, -20);
                    self.gainAffection(other, 2);
                }
            }
        }

        events();
        if (normal) {
            normal();
        }
    }
}
