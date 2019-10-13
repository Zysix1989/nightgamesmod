package nightgames.status;

import java.util.Optional;

import com.google.gson.JsonObject;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;

public class EnemyButtslutTrainingStatus extends Status {

    public EnemyButtslutTrainingStatus(Character affected) {
        super("EnemyTrainingStatus", affected);
        flag(Stsflag.buttsluttraining);
        trainingLevel = 0;
        sensitivityLevel = 0;
    }

    private double trainingLevel;
    private double sensitivityLevel;
    
    public void tick(Combat c) {
        //if(c.getStance().anallyPenetrated(c, affected)) trainingLevel += 0.06;
        //trainingLevel -= 0.01;
    }
    
    public void reactivate() {
        flag(Stsflag.buttslutificationReady);
    }

    public void reduce(String which) {
        switch (which) {
            case "Angel":
                trainingLevel -= 1;break;
            case "Mara":
                sensitivityLevel -= 1;break;
            default:
                throw new RuntimeException("Invalid argument to buttslut training level reduction type");
        }
        if (trainingLevel <= 0 && sensitivityLevel <= 0) {affected.removelist.add(this);}
    }

    public String initialMessage(Combat c, boolean replaced) {
        return Global.format("Your enemies have started to train you to be a buttslut", affected, c.getOpponent(affected));
    }

    @Override
    public String describe(Combat c) {
        return Global.format("You are being trained to be a buttslut", affected, c.getOpponent(affected));
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public int regen(Combat c) {
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

    @Override
    public boolean lingering() {
        return true;
    }
    
    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new EnemyButtslutTrainingStatus(newAffected);
    }

    @Override
    public JsonObject saveToJson() {
        return null;
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return null;
    }


    public static EnemyButtslutTrainingStatus getThisTypeForPlayer() {
        if(!Global.getPlayer().hasStatus(Stsflag.buttsluttraining)) {return new EnemyButtslutTrainingStatus(null);}
        return ((EnemyButtslutTrainingStatus)Global.getPlayer().getStatus(Stsflag.buttsluttraining));
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
