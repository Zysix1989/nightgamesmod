package nightgames.areas;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;

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
        ArrayList<GOAL> available = new ArrayList<GOAL>();
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

    public String message() {
        switch (goal) {
            case kisswin:
                return target.getTrueName()
                                + " seems pretty head over heels for you, at least to my eyes. I bet she'll climax if you give her a good kiss. Give it a try.";
            case clothedwin:
                return "Not everyone relies on brute force to get their opponents off. The masters of seduction often don't bother to even undress their opponents. See "
                                + "if you can make " + target.getTrueName() + " cum while she's still got her clothes on.";
            case bathambush:
                return "";
            case peggedloss:
                return "Getting pegged in the ass is a hell of a thing, isn't it. I sympathize... especially since "
                                + target.getTrueName() + " seems to have it in for you tonight. If it "
                                + "happens, I'll see that you're compensated.";
            case analwin:
                return target.getTrueName()
                                + " has been acting pretty cocky lately. If you can make her cum while fucking her in the ass, she should learn some humility.";
            case pendomwin:
                return "How good are you exactly? If you want to show " + target.getTrueName()
                                + " that you're the best, make her cum while giving her a good fucking.";
            case pendraw:
                return "Some things are better than winning, like cumming together with your sweetheart. You and "
                                + target.getTrueName() + " seem pretty sweet.";
            case subwin:
                return "Everyone loves an underdog. If you can win a fight with " + target.getTrueName()
                                + " when she thinks you're at her mercy, you'll get a sizeable bonus.";
            default:
                return "";
        }
    }

    private enum GOAL {
        kisswin("'Win with a kiss'"),
        clothedwin("'Win while opponent is clothed'"),
        bathambush("'Ambush opponent while bathing'"),
        peggedloss("'Lose by being pegged'"),
        analwin("'Win through anal sex'"),
        pendomwin("'Win through dominant sex'"),
        pendraw("'Force a draw through sex'"),
        subwin("'Win from a submissive position'");
        
        private final String name;
        
        private GOAL(String name) {
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
                case bathambush:
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
                Global.gui().message("You find a gold envelope sitting conspicously in the middle of the "
                                + Global.getMatch().genericRoomDescription()
                                + ". You open it up and read the note inside.\n'" + message() + "'\n");
            }
            active.getLocation().remove(this);
            active.getCharacter().accept(this);
            return true;
        }
        return false;
    }

    @Override
    public Character owner() {
        return null;
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
