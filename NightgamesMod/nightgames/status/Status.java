package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.match.Action;
import nightgames.match.Participant;
import nightgames.requirements.Requirement;
import nightgames.skills.Skill;

import java.util.*;
import java.util.function.Predicate;

public abstract class Status implements Cloneable {
    public String name;
    public transient Character affected;
    protected transient Set<Stsflag> flags;
    protected transient List<Requirement> requirements;

    public Status(String name, Character affected) {
        this.name = name;
        this.affected = affected;
        requirements = new ArrayList<>();
        flags = EnumSet.noneOf(Stsflag.class);
    }

    @Override
    public String toString() {
        return name;
    }

    /** The set of skills that this status effect restricts Characters to using. The union of
     * these sets is taken if a Character is afflicted by multiple Statuses using this feature,
     * so it's still possible that the Character will use a skill outside of this set.
     */
    public Collection<Skill> skillWhitelist(Combat c) {
        return Collections.emptySet();
    }
    
    /** The set of skills that this Status prevents a Character from using. This takes
     * precedence over the whitelist. The sets of blackslists from all Statuses are also
     * unioned.
     */
    public Set<Skill> skillBlacklist(Combat c) {
        return Collections.emptySet();
    }

    public boolean meetsRequirements(Combat c, Character self, Character other) {
        return requirements.stream().allMatch((req) -> req.meets(c, self, other));
    }

    public abstract String initialMessage(Combat c, Optional<Status> replacement);

    public abstract String describe(Character opponent);

    public abstract int mod(Attribute a);

    public abstract int regen(Combat c);

    // Increases damage received by the returned value
    public abstract int damage(Combat c, int x);

    // Increases pleasure received by the returned value
    public abstract double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x);

    public abstract int weakened(Combat c, int x);

    public abstract int tempted(Combat c, int x);

    public double sensitivity(double x, Skill skill) {
        return 0;
    }
    
    public double opponentSensitivity(double x, Skill skill) {
        return 0;
    }

    public abstract int evade();

    public abstract int escape();

    public abstract int gainmojo(int x);

    public abstract int spendmojo(int x);

    public abstract int counter();

    public abstract int value();
    
    public int drained(Combat c, int x) {
        return 0;
    }

    public float fitnessModifier() {
        return 0;
    }

    public boolean lingering() {
        return false;
    }

    public void flag(Stsflag status) {
        flags.add(status);
    }
    
    public void unflag(Stsflag status) {
        flags.remove(status);
    }

    public Set<Stsflag> flags() {
        return flags;
    }

    public Status withFlagRemoved(Stsflag flag) {
        flags.remove(flag);
        return this;
    }
    
    public boolean overrides(Status s) {
        return s.getClass() == this.getClass();
    }

    public void replace(Status newStatus) {}

    public boolean mindgames() {
        return flags().contains(Stsflag.mindgames);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract Status instance(Character newAffected, Character newOther);

    public String getVariant() {
        return toString();
    }

    public void struggle(Character character) {}

    public void onRemove(Combat c, Character other) {}

    public void onApply(Combat c, Character other) {}

    public abstract JsonObject saveToJson();

    public abstract Status loadFromJson(JsonObject obj);

    public void tick(Combat c) {}
    
    public boolean isAddiction() { return false; }

    public Predicate<Action> makeAllowedActionsPredicate(Participant bearer) {
        return action -> true;
    }

    public void afterMatchRound() {}
}
