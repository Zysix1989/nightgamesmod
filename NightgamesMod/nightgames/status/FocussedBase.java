package nightgames.status;

import java.util.Optional;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.skills.Skill;

public abstract class FocussedBase extends DurationStatus {

    private final String pro, con;

    public FocussedBase(String name, Character affected, String pro, String con) {
        super(name, affected, 10);
        this.pro = pro;
        this.con = con;
        flag(Stsflag.focussed);
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        return Global.format("{self:SUBJECT-ACTION:are} now fully focussed" + " on %s, to the detriment of %s.",
                        affected, c.getOpponent(affected), pro, con);
    }

    @Override
    public String describe(Character opponent) {
        return Global.format("{self:NAME-POSSESSIVE} focus on %s has not yet waned.", 
                        affected, opponent, pro);
    }
    
    @Override
    public abstract double sensitivity(double x, BodyPart withPart,
                    BodyPart targetPart, Skill skill);
    
    @Override
    public abstract double opponentSensitivity(double x, BodyPart withPart,
                    BodyPart targetPart, Skill skill);
    
    @Override
    public boolean lingering() {
        return true;
    }
    
    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return 0;
    }

    @Override
    public int escape() {
        return 0;
    }

    @Override
    public int gainmojo(int x) {
        return 0;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return 0;
    }

    @Override
    public int value() {
        return 0;
    }
    
}
