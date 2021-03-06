package nightgames.status.addiction;

import com.google.gson.JsonObject;
import java.util.Optional;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.status.Masochistic;
import nightgames.status.Status;

public class Dominance extends Addiction {

    public Dominance(Character affected, Character cause, float magnitude) {
        super(affected, "Dominance", cause, magnitude);
    }

    public Dominance(Character affected, Character cause) {
        this(affected, cause, .01f);
    }

    public static boolean mojoIsBlocked(Character affected, Combat c) {
        if (c == null)
            return false;
        Character player = affected;
        Character opp = c.getOpponentCharacter(player);
        if (!affected.checkAddiction(AddictionType.DOMINANCE, opp))
            return false;
        int sev = player.getAddictionSeverity(AddictionType.DOMINANCE)
                        .ordinal();


        return sev >= 5;
    }

    @Override
    protected Optional<Status> withdrawalEffects() {
            double mod = Math.min(1.0, 1.0 / (double) getSeverity().ordinal() + .4);
            affected.getWillpower().reduceCapacity(mod);
        return Optional.of(new Masochistic(affected));
    }

    @Override
    protected Optional<Status> addictionEffects() {
        return Optional.of(this);
    }

    @Override
    public void endNight() {
        super.endNight();

        affected.getWillpower().resetCapacity();
    }

    @Override
    protected String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                return "Held down by " + cause.getName() + ", you feel completely powerless to resist.";
            case LOW:
                return "You feel strangely weak in " + cause.getName() + "'s powerful hold.";
            case MED:
                return "Something about the way " + cause.getName() + " is holding on to you is causing your strength to seep away.";
            case NONE:
            default:
                return "";
        }
    }

    @Override
    protected String describeDecrease() {
        switch (getSeverity()) {
            case LOW:
                return "More and more of your strength is returning since escaping from " + cause.getName() + ". ";
            case MED:
                return "You find some of the strange weakness caused by " + cause.getName() + "'s powerful hold"
                                + " fleeing your bones. ";
            case NONE:
                return "You have completely recovered from " + cause.getName() + "'s hold. ";
            case HIGH:
            default:
                return "";
        }
    }

    @Override
    protected String describeWithdrawal() {
        return "Your body longs for the exquisite pain and submission " + cause.getName() + " can bring you,"
                        + " reducing your stamina and causing masochisitic tendencies.";
    }

    @Override
    protected String describeCombatIncrease() {
        return "Being hurt so well just makes you want to submit even more.";
    }

    @Override
    protected String describeCombatDecrease() {
        return "Some of the submissiveness clears from your mind, allowing you to focus" + " more on the fight.";
    }

    @Override
    public String informantsOverview() {
        return "<i>\"Is that all? With all the weird shit going on around here, you're worried about a submissive"
                        + " streak? Well, sure, I can see how it would be a problem. Being held down does not"
                        + " help your chances in a fight, and if you actually enjoy it you are not at all"
                        + " likely to win. Basically, if " + cause.pronoun() + " gets you down and tied up or something, you're going"
                        + " to lose, because you subconciously don't actually want to win.\"</i> That does sound"
                        + " pretty bad... Any upsides? <i>\"Well, I suppose that being on the receiving end of such"
                        + " a powerful dominance, the stuff other people do won't make as much of an impression."
                        + " Personally, I wouldn't go for it, but if you like getting hurt and humiliated, go right"
                        + " ahead.\"";
    }

    @Override
    public String describeMorning() {
        return "";
    }

    @Override
    public AddictionType getType() {
        return AddictionType.DOMINANCE;
    }

    @Override
    public String initialMessage(Combat c, Optional<Status> replacement) {
        if (inWithdrawal) {
            return cause.getName() + " is looking meaner than ever after you neglected to visit today. Equal"
                            + " parts of fear and desire well up inside of you at the thought of what "
                            + cause.pronoun() + " might do to you.";
        }
        return "You are conflicted at the sight of " + cause.getName() + ". One part of you still remembers"
                        + " the pain and humiliation " + cause.pronoun() + " can cause and"
                        + " is terrified because of it, the other part is getting excited"
                        + " for the very same reason.";
    }

    @Override
    public String describe(Character opponent) {
        return "";
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
    public Status instance(Character newAffected, Character newOther) {
        return new Dominance((Character) newAffected, newOther, magnitude);
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return new Dominance(Global.noneCharacter(), Global.getCharacterByType(obj.get("cause")
                                                          .getAsString()),
                        (float) obj.get("magnitude")
                                   .getAsInt());
    }

    @Override
    protected void applyEffects(Character self) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void removeEffects(Character self) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void cleanseAddiction(Character self) {
        // TODO Auto-generated method stub
        
    }
}
