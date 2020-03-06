package nightgames.match.ftc;

import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.match.Prematch;
import nightgames.match.PrematchEvent;
import nightgames.modifier.standard.FTCModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class FTCPrematch extends Prematch {

    private Character prey;

    public FTCPrematch() {
        super(List.of(new FTCFirstIntroEvent(), new FTCIntroEvent()));
    }

    @Override
    public void respond(String response) {
        if (response.equals("Start the Match")) {
            FTCModifier mod = new FTCModifier(prey);
            type = mod;
            Global.flag(Flag.didFTC);
            Global.setUpFTCMatch(mod);
        } else {
            String message = "";
            if (response.equals("Volunteer")) {
                prey = Global.getPlayer();
                if (!Global.checkFlag(Flag.didFTC)) {
                    message += "\"That's the spirit! Oh, did I mention the Prey has to be naked"
                                    + " for the duration of the match and can't use any items?\" Lilly grins mischievously as she"
                                    + " reveals this small detail, but it's too late to back down now. Everyone"
                                    + " climbs in the van, Lilly at the wheel, and you set off. While Lilly"
                                    + " assigns all the girls their bases, they are enthusiastically pulling"
                                    + " off your clothes, leaving you naked before the van exits the parking lot."
                                    + " You can't help but feel slightly uncomfortable, despite the fact that"
                                    + " you have been sexfighting these girls for a while now. After about 20"
                                    + " minutes, the van pulls to a stop, and everyone goes to their designated"
                                    + " starting positions. Yours is in a clearing right in the center of the "
                                    + "forest. There is a crate filled with ribbons here, so you pick one up"
                                    + " and bind it around your left arm. The sharp sound of a whistle signals"
                                    + " the start of the match, so you had best get going quickly.";
                } else {
                    message += "You volunteer for the position of Prey, and strip down accordingly."
                                    + " After a brief ride, you arrive at the forest and make your way to"
                                    + " the center. You take a Flag from the box and await the starting" + " signal.";
                }
            } else {
                prey = Global.getParticipants().stream().filter(Predicate.not(Character::human)).findAny().orElseThrow();
                if (!Global.checkFlag(Flag.didFTC)) {
                    message += "\"No one? Really? Fine, then I'll pick someone. Let's see... " + prey.getTrueName()
                                    + "! You have the honors tonight. Oh and just so"
                                    + " you know, the Prey competes naked and without items. Get to it!\" "
                                    + prey.getTrueName() + " briefly seems nervous, but then shrugs and ditches all of "
                                    + prey.possessiveAdjective()
                                    + " clothing and gets in the van. The rest of you pile in as well,"
                                    + " and a short ride later you arrive at the edge of the forest."
                                    + " Lilly gives everyone their starting positions, and you make"
                                    + " your way to yours, ready for the match to begin.";
                } else {
                    message += "\"No one? Really? Fine, then I'll pick someone. Let's see..." + prey.getTrueName()
                                    + "! You have the honors tonight.\" Everyone gets into the van, and " + prey.getTrueName()
                                    + " quickly strips naked. Once at the forest, you all "
                                    + "get to your bases and await Lilly's signal.";
                }
            }
            List<CommandPanelOption> choices = new ArrayList<>();
            choices.add(GUI.sceneOption("Start the Match"));
            Global.gui().prompt(message, choices);
        }
    }
    
    private static class FTCFirstIntroEvent extends PrematchEvent {

        FTCFirstIntroEvent() {
            super("When you get to the student union, you find it deserted save for"
                            + " a note telling you to go to the parking lot instead. Once you get"
                            + " there, the others, including Lilly, are already waiting next to a van. "
                            + "\"Nice of you to join us, " + Global.getPlayer().getTrueName() + ". I've been working for a while"
                            + " on devising an alternative match format, and tonight the lot"
                            + " of you get to be my guniea pigs.\" That sounds patently"
                            + " uncomfortable, but you listen on anyway. \"The idea is this: I drop"
                            + " you off in some nearby woods, and then let four of you hunt the fifth."
                            + " One of you will be assigned as the 'Prey', while the others will be"
                            + " 'Hunters'. At the start of the match, the Prey will get a ribbon, which"
                            + " we will call the Flag. The Prey's goal is to keep the Flag. If a Hunter"
                            + " encounters the Prey, they will fight under the same rules that apply"
                            + " on campus. If the Hunter wins, they take the Flag. Their goal then"
                            + " becomes taking the flag safely back to their base. Hunters"
                            + " may attack other Hunters at will. If a Hunter turns in"
                            + " the Flag, a new one will appear in the center of the forest, where the"
                            + " Prey can pick it up. For the sake of fairness, the Prey cannot be attacked"
                            + " for 15 minutes after picking up the Flag. Scoring is as follows: Hunters"
                            + " get one point for beating the Prey, two points for beating a Hunter,"
                            + " and five points for delivering a Flag to their base. The Prey gets one"
                            + " point for every 15 minutes they hold on to the Flag, plus three points for every"
                            + " fight they win. Everyone will get $100 per point at the end of the night."
                            + " So, anyone want to volunteer to be our first Prey?\"", null,
                Arrays.asList(GUI.sceneOption("Volunteer"), GUI.sceneOption("Keep Silent")));
        }
        
        @Override
        protected void extraEffects() {
            
        }

        @Override
        protected boolean valid() {
            return !Global.checkFlag(Flag.didFTC);
        }
        
    }
    
    private static class FTCIntroEvent extends PrematchEvent {

        FTCIntroEvent() {
            super("You find a note in the student union saying that tonight's match will"
                            + " take place in the forest again. When you get to the van, Lilly asks the"
                            + " assembled competitors who wants to be the Prey tonight.\"", null,
                Arrays.asList(GUI.sceneOption("Volunteer"), GUI.sceneOption("Keep Silent")));
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
