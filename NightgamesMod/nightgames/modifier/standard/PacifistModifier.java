package nightgames.modifier.standard;

import nightgames.characters.Player;
import nightgames.global.Global;
import nightgames.grammar.Shorthand;
import nightgames.modifier.BaseModifier;
import nightgames.modifier.skill.BanTacticsModifier;
import nightgames.skills.Tactics;

public class PacifistModifier extends BaseModifier {

    public static final String NAME = "pacifist";

    public PacifistModifier() {
        skills = new BanTacticsModifier(Tactics.damage);
    }

    @Override
    public int bonus() {
        return 100;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String intro() {
        return "Lilly gives you a long, appraising look. <i>\"I'm trying to decide what sort of person you are. You strike me as reasonable, probably not the type "
                        + "to assault someone outside a match. I propose you try being a perfect " + Shorthand.gentlemanOrLady(Global.getPlayer()) + " by refusing to hit "
                        + "anyone during tonight's match too. So no slapping, kicking, anything intended to purely cause pain. If you agree, I'll add $" + bonus()
                        + " to each point. What do you say?\"</i>";
    }

    @Override
    public String acceptance() {
        Player player = Global.getPlayer();
        return String.format("Lilly flashes you a broad grin and slaps you on the back uncomfortably hard. <i>\"Just so everyone's aware,\"</i> she calls out to your opponents, <i>\""
                        + "%s has sworn that %s won't hurt anyone tonight. So no matter how much anyone taunts %s, whips %s, or kicks %s in the crotch, %s can't retaliate in "
                        + "any way.\"</i> As you try to ignore a growing sense of dread, she leans close to your ear and whispers, <i>\"Good luck.\"</i>",
                        player.getTrueName(),
                        player.pronoun(),
                        player.objectPronoun(), player.objectPronoun(), player.objectPronoun(),
                        player.pronoun());
    }

}
