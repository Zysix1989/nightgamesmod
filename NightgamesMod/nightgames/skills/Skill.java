package nightgames.skills;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.Staleness;
import nightgames.status.FiredUp;
import nightgames.status.Status;
import nightgames.status.Stsflag;

public abstract class Skill {
    /**
     *
     */
    private String name;
    private Character self;
    private int cooldown;
    private Set<SkillTag> tags;
    public String choice;
    private Staleness staleness;

    public Skill(String name, Character self) {
        this(name, self, 0);
    }
    public Skill(String name, Character self, int cooldown) {
        this(name, self, cooldown, Staleness.build().withDecay(.1).withFloor(.5).withRecovery(.05));
    }

    public Skill(String name, Character self, int cooldown, Staleness staleness) {
        this.name = name;
        setSelf(self);
        this.cooldown = cooldown;
        this.staleness = staleness;
        choice = "";
        tags = new HashSet<>();
    }

    public final boolean requirements(Combat c, Character target) {
        return requirements(c, getSelf(), target);
    }

    public abstract boolean requirements(Combat c, Character user, Character target);

    public static void filterAllowedSkills(Combat c, Collection<Skill> skills, Character user) {
        filterAllowedSkills(c, skills, user, null);
    }
    
    
    public static void filterAllowedSkills(Combat c, Collection<Skill> skills, Character user, Character target) {
        boolean restrictedByStance = false;
        boolean restrictedByStatus = false;
   
        // If there's a set of skills we're restricted to because of stance, filter down to those
        Optional<Collection<Skill>> possibleGivenStance = c.getStance().allowedSkills(c, user);
        if (possibleGivenStance.isPresent()) {
            Set<Skill> stanceSkills = new HashSet<Skill>(possibleGivenStance.get());
            skills.retainAll(stanceSkills);
            restrictedByStance = true;
        }
        
        // From the list of possible actions, possibly restricted by stance, we check Status
        // effect to see if anything is forcing or preventing remaining skill choices.
        Set<Skill> forcedGivenStatus = new HashSet<Skill>();
        Set<Skill> statusBlacklist = new HashSet<Skill>();
        for (Status st : user.status) {
            for (Skill sk : st.skillWhitelist(c)) {
                forcedGivenStatus.add(sk);
            }
            for (Skill sk: st.skillBlacklist(c)) {
                statusBlacklist.add(sk);
            }
        }
        
        // We don't take the blacklist into account when deciding whether skills were restricted
        // by status because those it doesn't remove still have to be checked.
        if (!forcedGivenStatus.isEmpty()) {
            skills.retainAll(forcedGivenStatus);
            restrictedByStatus = true;
        }
       
        // Both Stance and Status lists take precedence over the general checking we're about
        // to do, so if we've used either then we're done.
        if (restrictedByStance || restrictedByStatus) {
            return;
        }
        
        // Remove using generic requirements & tags.
        Set<Skill> filtered = new HashSet<Skill>();
        for (Skill sk : skills) {
            if (sk.getTags(c).contains(SkillTag.mean) && user.has(Trait.softheart)) {
                filtered.add(sk);
                continue;
            }
            if (!sk.requirements(c, target != null? target : sk.getDefaultTarget(c))) {
                filtered.add(sk);
            }
        }
        skills.removeAll(filtered);
    }

    public static boolean isUsable(Combat c, Skill s) {
        return isUsableOn(c, s, null);
    }

    public static boolean isUsableOn(Combat c, Skill s, Character target) {
        if (target == null) {
            target = s.getDefaultTarget(c);
        }
        boolean charmRestricted = (s.getSelf().is(Stsflag.charmed))
                        && s.type(c) != Tactics.fucking && s.type(c) != Tactics.pleasure && s.type(c) != Tactics.misc;
        boolean allureRestricted =
                        target.is(Stsflag.alluring) && (s.type(c) == Tactics.damage || s.type(c) == Tactics.debuff);
        boolean modifierRestricted = !Global.getMatch().getCondition().getSkillModifier().allowedSkill(c,s);
        boolean usable = s.usable(c, target) && s.getSelf().canSpend(s.getMojoCost(c)) && !charmRestricted
                        && !allureRestricted && !modifierRestricted;
        return usable;
    }

    public int getMojoBuilt(Combat c) {
        return 0;
    }

    public int getMojoCost(Combat c) {
        return 0;
    }

    public abstract boolean usable(Combat c, Character target);

    public abstract String describe(Combat c);

    public abstract boolean resolve(Combat c, Character target);

    public abstract Skill copy(Character user);

    public abstract Tactics type(Combat c);

    public abstract String deal(Combat c, int damage, Result modifier, Character target);

    public abstract String receive(Combat c, int damage, Result modifier, Character target);

    public boolean isReverseFuck(Character target) {
        return target.hasDick() && getSelf().hasPussy();
    }

    public float priorityMod(Combat c) {
        return 0.0f;
    }

    public int accuracy(Combat c, Character target) {
        return 200;
    }

    public Staleness getStaleness() {
        return this.staleness;
    }

    public int speed() {
        return 5;
    }

    public String getLabel(Combat c) {
        return getName(c);
    }

    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Character user() {
        return getSelf();
    }

    public void setSelf(Character self) {
        this.self = self;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        return toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return ("Skill:" + toString()).hashCode();
    }

    public String getName(Combat c) {
        return toString();
    }

    public boolean makesContact() {
        return false;
    }

    public static boolean resolve(Skill skill, Combat c, Character target) {
        skill.user().addCooldown(skill);
        // save the mojo built of the skill before resolving it (or the status
        // may change)
        int generated = skill.getMojoBuilt(c);

        // Horrendously ugly, I know.
        // But you were the one who removed getWithOrganType...
        if (skill.user().has(Trait.temptress)) {
            FiredUp status = (FiredUp) skill.user().status.stream().filter(s -> s instanceof FiredUp).findAny()
                            .orElse(null);
            if (status != null) {
                if (status.getPart().equals("hands") && skill.getClass() != TemptressHandjob.class
                                || status.getPart().equals("mouth") && skill.getClass() != TemptressBlowjob.class
                                || status.getPart().equals("pussy") && skill.getClass() != TemptressRide.class) {
                    skill.user().removeStatus(Stsflag.firedup);
                }
            }
        }

        boolean success = skill.resolve(c, target);
        skill.user().spendMojo(c, skill.getMojoCost(c));
        if (success) {
            skill.user().buildMojo(c, generated);
        } else if (target.has(Trait.tease) && Global.random(4) == 0) {
            c.write(target, Global.format("Dancing just past {other:name-possessive} reach gives {self:name-do} a minor high.", target, skill.getSelf()));
            target.buildMojo(c, 20);
        }
        if (success && c.getCombatantData(skill.getSelf()) != null) {
            c.getCombatantData(skill.getSelf()).decreaseMoveModifier(c, skill);
        }
        if (c.getCombatantData(skill.user()) != null) { 
            c.getCombatantData(skill.user()).setLastUsedSkillName(skill.getName());
        }
        return success;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Collection<String> subChoices(Combat c) {
        return Collections.emptySet();
    }

    public Character getSelf() {
        return self;
    }
    
    protected void printBlinded(Combat c) {
        c.write(getSelf(), "<i>You're sure something is happening, but you can't figure out what it is.</i>");
    }
    
    public Stage getStage() {
        return Stage.REGULAR;
    }

    public Character getDefaultTarget(Combat c) {
        return c.getOpponent(getSelf());
    }

    public final double multiplierForStage(Character target) {
        return getStage().multiplierFor(target);
    }
    
    protected void writeOutput(Combat c, Result result, Character target) {
        writeOutput(c, 0, result, target);
    }
    
    protected void writeOutput(Combat c, int mag, Result result, Character target) {
        if (getSelf().human()) {
            c.write(getSelf(), deal(c, mag, result, target));
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(getSelf(), receive(c, mag, result, target));
        }
    }

    protected void addTag(SkillTag tag) {
        tags.add(tag);
    }

    protected void removeTag(SkillTag tag) {
        tags.remove(tag);
    }
    public final Set<SkillTag> getTags(Combat c) {
        return getTags(c, c.getOpponent(self));
    }

    public Set<SkillTag> getTags(Combat c, Character target) {
        return Collections.unmodifiableSet(tags);
    }
}
