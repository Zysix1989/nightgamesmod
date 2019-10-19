package nightgames.characters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.gui.commandpanel.CommandPanelOption;
import nightgames.match.Encounter;
import nightgames.skills.Tactics;
import nightgames.trap.Trap;

public class DummyCharacter extends Character {

    public DummyCharacter(String name, String type, int level, BodyPart... parts) {
        super(name, level);
        this.type = type;
        Arrays.stream(parts).forEach(part -> body.add(part));
    }

    private String type;

    @Override
    public void ding(Combat c) {}

    @Override
    public void faceOff(Character opponent, Encounter enc) {}

    @Override
    public void spy(Character opponent, Encounter enc) {}

    @Override
    public String describe(int per, Character observer) {
        return "";
    }

    @Override
    public void victory(Combat c, Result flag) {}

    @Override
    public void defeat(Combat c, Result flag) {}

    @Override
    public void intervene3p(Combat c, Character target, Character assist) {}

    @Override
    public void victory3p(Combat c, Character target, Character assist) {}

    @Override
    public boolean resist3p(Combat c, Character target, Character assist) {
        return false;
    }

    @Override
    public boolean act(Combat c) {
        return false;
    }

    @Override
    public void move() {}

    @Override
    public void draw(Combat c, Result flag) {}

    @Override
    public boolean human() {
        return false;
    }

    @Override
    public String bbLiner(Combat c, Character target) {
        return "";
    }

    @Override
    public String nakedLiner(Combat c, Character target) {
        return "";
    }

    @Override
    public String stunLiner(Combat c, Character target) {
        return "";
    }

    @Override
    public String taunt(Combat c, Character target) {
        return "";
    }

    @Override
    public List<CommandPanelOption> intervene(Encounter fight, Character p1, Character p2) {
        return new ArrayList<>();
    }

    @Override
    public void showerScene(Character target, Encounter encounter) {}

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void afterParty() {}

    @Override
    public void emote(Emotion emo, int amt) {}

    @Override
    public String challenge(Character other) {
        return "";
    }

    @Override
    public void promptTrap(Encounter fight, Character target, Trap trap) {
    }

    @Override
    public void counterattack(Character target, Tactics type, Combat c) {}

    @Override
    public String getPortrait() {
        return "";
    }

    @Override public Growth getGrowth() {
        return new Growth();
    }

}
