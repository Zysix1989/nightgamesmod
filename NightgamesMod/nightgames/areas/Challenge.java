package nightgames.areas;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.ArrayList;
import java.util.List;

public class Challenge implements Deployable {
    private Character owner;
    private Character target;
    private GOAL goal;
    public boolean done;

    public Challenge() {
        done = false;
    }

    public GOAL pick() {
        ArrayList<GOAL> available = new ArrayList<>();
        if (!target.breastsAvailable() && !target.crotchAvailable()) {
            available.add(GOAL.clothedwin);
        }
        if (owner.getPure(Attribute.Seduction) >= 9) {
            available.add(GOAL.analwin);
        }
        if (owner.getAffection(target) >= 10) {
            available.add(GOAL.kisswin);
            available.add(GOAL.pendraw);
        }
        if (target.has(Item.Strapon) || target.has(Item.Strapon2) || target.hasDick()) {
            available.add(GOAL.peggedloss);
        }
        available.add(GOAL.pendomwin);
        available.add(GOAL.subwin);
        return available.get(Global.random(available.size()));
    }

    private static final JtwigTemplate KISS_WIN_ANNOUNCE = JtwigTemplate.inlineTemplate(
            "{{ target.subject().properNoun() }} seems pretty head over heels for you, at least to my eyes. " +
                    "I bet you can bring {{ target.subject().pronoun() }} to a climax if you give " +
                    "{{ target.object().pronoun() }} a good kiss. Give it a try.");
    private static final JtwigTemplate CLOTHED_WIN_ANNOUNCE = JtwigTemplate.inlineTemplate(
            "Not everyone relies on brute force to get their opponents off. The masters of seduction often don't " +
                    "bother to even undress their opponents. See if you can make {{ target.object().properNoun() }} " +
                    "cum while {{ target.subject().pronoun() }}'s still got {{ target.possessiveAdjective() }} " +
                    "clothes on.");
    private static final JtwigTemplate PEGGED_LOSS_ANNOUNCE = JtwigTemplate.inlineTemplate(
            "Getting pegged in the ass is a hell of a thing, isn't it? I sympathize... especially since "
            + "{{ target.subject().properNoun() }} seems to have it in for you tonight. If " +
                    "you cum from it, I'll see that you're compensated.");
    private static final JtwigTemplate ANAL_WIN_ANNOUNCE = JtwigTemplate.inlineTemplate(
            "{{ target.subject().properNoun() }} has been acting pretty cocky lately. If you can make her cum " +
                    "while fucking {{ target.object().pronoun() }} in the ass, {{ target.subject().pronoun() }} " +
                    "should learn some humility.");
    private static final JtwigTemplate PEN_DOM_WIN_ANNOUNCE = JtwigTemplate.inlineTemplate(
            "How good are you exactly? If you want to show {{ target.object().properNoun() }} that you're the best, " +
                    "make {{ target.object().pronoun() }} cum while giving {{ target.object().pronoun() }} a good " +
                    "fucking.");
    private static final JtwigTemplate PEN_DRAW_ANNOUNCE = JtwigTemplate.inlineTemplate(
            "Some things are better than winning, like cumming together with your sweetheart. You and " +
                    "{{ target.subject().properNoun() }} seem pretty sweet.");
    private static final JtwigTemplate SUB_WIN_ANNOUNCE = JtwigTemplate.inlineTemplate(
            "Everyone loves an underdog. If you can make {{ target.object().properNoun() }} cum when " +
                    "{{ target.subject().properNoun() }} thinks you're at {{ target.possessiveAdjective() }} mercy, " +
                    "you'll get a sizable bonus.");

    public JtwigTemplate announceTemplate() {
        switch (goal) {
            case kisswin:
                return KISS_WIN_ANNOUNCE;
            case clothedwin:
                return CLOTHED_WIN_ANNOUNCE;
            case peggedloss:
                return PEGGED_LOSS_ANNOUNCE;
            case analwin:
                return ANAL_WIN_ANNOUNCE;
            case pendomwin:
                return PEN_DOM_WIN_ANNOUNCE;
            case pendraw:
                return PEN_DRAW_ANNOUNCE;
            case subwin:
                return SUB_WIN_ANNOUNCE;
            default:
                throw new RuntimeException(String.format("fell off end: %s", goal));
        }
    }

    private enum GOAL {
        kisswin("'Win with a kiss'"),
        clothedwin("'Win while opponent is clothed'"),
        peggedloss("'Lose by being pegged'"),
        analwin("'Win through anal sex'"),
        pendomwin("'Win through dominant sex'"),
        pendraw("'Force a draw through sex'"),
        subwin("'Win from a submissive position'");
        
        private final String name;
        
        GOAL(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }

    public String describe() {
        return goal.getName() + " Challenge vs. " + target.getTrueName();
    }
    
    public void check(Combat state, Character victor) {
        if (!done && (state.getP1Character() == target || state.getP2Character() == target || target == null)) {
            switch (goal) {
                case kisswin:
                    if (victor == owner && state.lastact(owner).toString().equals("Kiss")) {
                        done = true;
                    }
                    break;
                case clothedwin:
                    if (victor == owner && !target.breastsAvailable() && !target.crotchAvailable()) {
                        done = true;
                    }
                    break;
                case peggedloss:
                    if (target == victor && state.state == Result.anal) {
                        done = true;
                    }
                    break;
                case analwin:
                    if (owner == victor && state.state == Result.anal) {
                        done = true;
                    }
                    break;
                case pendomwin:
                    if (target == victor && state.state == Result.intercourse) {
                        done = true;
                    }
                    break;
                case pendraw:
                    if (victor == null && state.state == Result.intercourse) {
                        done = true;
                    }
                    break;
                case subwin:
                    if (victor == owner && state.getStance().sub(owner)) {
                        done = true;
                    }
                    break;
            }
        }
    }

    @Override
    public boolean resolve(Participant active) {
        if (active.getCharacter().state == State.ready) {
            owner = active.getCharacter();
            List<Character> combatants = Global.getMatch().getCombatants();
            target = combatants.get(Global.random(combatants.size() - 1));
            for (int i = 0; i < 10 && target == active.getCharacter(); i++) {
                target = combatants.get(Global.random(combatants.size() - 1));
            }
            if (target == active.getCharacter()) {
                return false;
            }
            goal = pick();
            if (active.getCharacter().human()) {
                var model = JtwigModel.newModel()
                        .with("target", target.getGrammar());
                Global.gui().message("You find a gold envelope sitting conspicously in the middle of the "
                                + Global.getMatch().genericRoomDescription()
                                + ". You open it up and read the note inside.\n'" + announceTemplate().render(model) + "'\n");
            }
            active.getLocation().remove(this);
            active.getCharacter().accept(this);
            return true;
        }
        return false;
    }

    public int reward() {
        switch (goal) {
            case kisswin:
            case clothedwin:
                return 250;
            case peggedloss:
                return 1000;
            case pendomwin:
                return 300;
            default:
                return 500;
        }
    }
}
