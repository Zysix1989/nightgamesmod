package nightgames.pet;

import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.grammar.Person;
import nightgames.grammar.SingularFeminineThirdPerson;
import nightgames.grammar.SingularMasculineThirdPerson;
import nightgames.match.Action;
import nightgames.match.Encounter;
import nightgames.match.Participant;
import nightgames.match.actions.Move;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.Slimed;
import nightgames.status.Status;
import nightgames.trap.Trap;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PetCharacter extends Character {
    
    public static final PetCharacter DUMMY = new PetCharacter();
    
    private static final Set<SkillTag> PET_UNUSABLE_TAG = new HashSet<>();
    static {
        PET_UNUSABLE_TAG.add(SkillTag.suicidal);
        PET_UNUSABLE_TAG.add(SkillTag.petDisallowed);
        PET_UNUSABLE_TAG.add(SkillTag.counter);
    }
    private String type;
    private String ownerType;
    private Pet self;

    @Override
    public final int getPetLimit() {
        // NO PETS OF PETS ARGH
        return 0;
    }

    private static final List<Trait> INSPIRABLE_TRAITS = Arrays.asList(
                    Trait.analFanatic,
                    Trait.analTraining1,
                    Trait.analTraining2,
                    Trait.analTraining3,
                    Trait.anatomyknowledge,
                    Trait.asshandler,
                    Trait.assmaster,
                    Trait.autonomousAss,
                    Trait.autonomousPussy,
                    Trait.carnalvirtuoso,
                    Trait.defthands,
                    Trait.desensitized,
                    Trait.desensitized2,
                    Trait.dexterous,
                    Trait.dominatrix,
                    Trait.energydrain,
                    Trait.experienced,
                    Trait.experttongue,
                    Trait.fakeout,
                    Trait.freeSpirit,
                    Trait.graceful,
                    Trait.hawkeye,
                    Trait.holecontrol,
                    Trait.insertion,
                    Trait.limbTraining1,
                    Trait.limbTraining2,
                    Trait.limbTraining3,
                    Trait.mojoMaster,
                    Trait.naturalTop,
                    Trait.nimbletoes,
                    Trait.obsequiousAppeal,
                    Trait.oiledass,
                    Trait.polecontrol,
                    Trait.powerfulhips,
                    Trait.pussyhandler,
                    Trait.responsive,
                    Trait.romantic,
                    Trait.RawSexuality,
                    Trait.sadist,
                    Trait.silvertongue,
                    Trait.sexualmomentum,
                    Trait.shameless,
                    Trait.sexTraining1,
                    Trait.sexTraining2,
                    Trait.sexTraining3,
                    Trait.soulsucker,
                    Trait.spiral,
                    Trait.submissive,
                    Trait.sweetlips,
                    Trait.SexualGroove,
                    Trait.temptingtits,
                    Trait.ticklemonster,
                    Trait.toymaster,
                    Trait.tongueTraining1,
                    Trait.tongueTraining2,
                    Trait.tongueTraining3,
                    Trait.tight);
    public PetCharacter(Pet self, String name, String type, Growth growth, int level) {
        super(name, 1);
        this.ownerType = self.owner().getType();
        this.self = self;
        this.type = type;
        this.setGrowth(growth);
        for (int i = 1; i < level; i++) {
            this.level += 1;
            getGrowth().levelUp(this);
        }
        distributePoints(Arrays.asList());
        if (self.owner().has(Trait.inspirational)) {
            for (Trait t : INSPIRABLE_TRAITS) {
                if (self.owner().has(t) && !has(t)) {
                    add(t);
                }
            }
        }
        this.getSkills().clear();
        this.mojo.setMax(100);
        getStamina().renew();
        getArousal().renew();
        getMojo().renew();
    }

    private PetCharacter() {
        super("{{{DUMMY}}}", 1);
    }
    
    public boolean isDummy() {
        return self == null;
    }
    
    public PetCharacter cloneWithOwner(Character owner) throws CloneNotSupportedException {
        PetCharacter clone = (PetCharacter) clone();
        clone.self = getSelf().cloneWithOwner(owner);
        return clone;
    }

    @Override
    public void ding(Combat c) {
        level += 1;
        getGrowth().levelUp(this);
        distributePoints(Arrays.asList());
    }

    @Override
    public String describe(int per, Character observer) {
        return "";
    }

    @Override
    public void intervene3p(Combat c, Character target, Character assist) {}

    @Override
    public void victory3p(Combat c, Character target, Character assist) {}

    @Override
    public boolean resist3p(Combat c, Character target, Character assist) {
        return true;
    }

    @Override
    public void move(Collection<Action> possibleActions, Consumer<Action> callback) {

    }

    @Override
    public void handleEnthrall(Consumer<Action> callback) {

    }

    public boolean act(Combat c, Character target) {
        List<Skill> allowedEnemySkills = new ArrayList<>(getSkills()
                        .stream().filter(skill -> Skill.isUsableOn(c, skill, target) && Collections.disjoint(skill.getTags(c), PET_UNUSABLE_TAG))
                        .collect(Collectors.toList()));
        Skill.filterAllowedSkills(c, allowedEnemySkills, this, target);        

        List<Skill> possibleMasterSkills = new ArrayList<>(getSkills());
        possibleMasterSkills.addAll(Combat.WORSHIP_SKILLS);
        List<Skill> allowedMasterSkills = new ArrayList<>(getSkills()
                        .stream().filter(skill -> Skill.isUsableOn(c, skill, getSelf().owner)
                                        && (skill.getTags(c).contains(SkillTag.helping) || (getSelf().owner.has(Trait.showmanship) && skill.getTags(c).contains(SkillTag.worship)))
                                        && Collections.disjoint(skill.getTags(c), PET_UNUSABLE_TAG))
                        .collect(Collectors.toList()));
        Skill.filterAllowedSkills(c, allowedMasterSkills, this, getSelf().owner);
        WeightedSkill bestEnemySkill = Decider.prioritizePet(this, target, allowedEnemySkills, c);
        WeightedSkill bestMasterSkill = Decider.prioritizePet(this, getSelf().owner, allowedMasterSkills, c);

        // don't let the ratings be negative.
        double masterSkillRating = Math.max(.001, bestMasterSkill.rating);
        double enemySkillRating = Math.max(.001, bestEnemySkill.rating);

        double roll = Global.randomdouble(masterSkillRating + enemySkillRating) - masterSkillRating;
        if (roll >= 0) {
            c.write(this, String.format("<b>%s uses %s against %s</b>\n", getTrueName(), 
                            bestEnemySkill.skill.getLabel(c), target.nameDirectObject()));
            Skill.resolve(bestEnemySkill.skill, c, target);
        } else {
            c.write(this, String.format("<b>%s uses %s against %s</b>\n", 
                            getTrueName(), bestMasterSkill.skill.getLabel(c), target.nameDirectObject()));
            Skill.resolve(bestMasterSkill.skill, c, self.owner());
        }
        return false;
    }

    @Override
    public void add(Combat c, Status status) {
        super.add(c, status);
        if (stunned()) {
            c.write(this, Global.format("With {self:name-possessive} link to the fight weakened, {self:subject-action:disappears|disappears}..", this, this));
            c.removePet(this);
        }
    }

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
    public String challenge(Character other) {
        return "";
    }

    @Override
    public String getPortrait() {
        return "";
    }

    @Override
    public String getType() {
        return type;
    }
    
    @Override
    protected void resolveOrgasm(Combat c, Character opponent, BodyPart selfPart, BodyPart opponentPart, int times, int totalTimes) {
        super.resolveOrgasm(c, opponent, selfPart, opponentPart, times, totalTimes);
        if (getSelf().owner().has(Trait.StickyFinale)) {
            c.write(this, Global.format("The force of {self:name-possessive} orgasm causes {self:direct-object} to shudder and explode in a rain of slime, completely covering {other:name-do} with the sticky substance.", this, opponent));
            opponent.add(c, new Slimed(opponent, getSelf().owner(), Global.random(5, 11)));
        } else {
            c.write(this, Global.format("The force of {self:name-possessive} orgasm destroys {self:possessive} anchor to the fight and {self:pronoun} disappears.", this, opponent));
        }
        c.removePet(this);
    }

    @Override
    public void intrudeInCombat(Set<Encounter.IntrusionOption> intrusionOptions, List<Move> possibleMoves, Consumer<Action> actionCallback, Runnable neitherContinuation) { }

    @Override
    public void showerScene(Participant target, Runnable ambushContinuation, Runnable stealContinuation, Runnable aphrodisiacContinuation, Runnable waitContinuation) {}
    @Override
    public void afterParty() {}
    
    @Override
    public void emote(Emotion emo, int amt) {}

    @Override
    public void promptTrap(Participant target, Trap.Instance trap, Runnable attackContinuation, Runnable waitContinuation) {}

    @Override
    public void counterattack(Character target, Tactics type, Combat c) {}

    @Override
    public Growth getGrowth() {
        return super.getGrowth();
    }

    public boolean isPetOf(Character other) {
        return other != null && !isDummy() && ownerType.equals(other.getType());
    }

    public Pet getSelf() {
        return self;
    }
    
    public double percentHealth() {
        return Math.min(getStamina().percent(), getArousal().percent());
    }

    public boolean isPet() {
        return true;
    }

    @Override
    public void faceOff(Participant opponent, Runnable fightContinuation, Runnable fleeContinuation, Runnable smokeContinuation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void spy(Participant opponent, Runnable ambushContinuation, Runnable waitContinuation) {
        // TODO Auto-generated method stub
    }

    @Override
    public Person getGrammar() {
        if (useFemalePronouns()) {
            return new SingularFeminineThirdPerson(this);
        } else {
            return new SingularMasculineThirdPerson(this);
        }
    }

}