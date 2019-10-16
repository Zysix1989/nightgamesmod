package nightgames.pet.arms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.pet.PetCharacter;
import nightgames.pet.arms.skills.ArmSkill;
import nightgames.pet.arms.skills.DoubleGrab;
import nightgames.pet.arms.skills.Idle;
import nightgames.pet.arms.skills.MultiArmMove;

public class ArmManager {
    private static final List<MultiArmMove> MULTI_MOVES = Arrays.asList(new DoubleGrab());

    private List<Arm> arms;

    public ArmManager() {
        arms = new ArrayList<>();
    }

    public ArmManager instance() {
        ArmManager newManager = new ArmManager();
        arms.forEach(arm -> newManager.arms.add(arm.instance()));
        return newManager;
    }

    public void addArm(Arm arm) {
        arms.add(arm);
    }

    public List<Arm> getActiveArms() {
        return new ArrayList<>(arms);
    }

    private String describeArms(List<? extends Arm> arms) {
        return arms.stream()
            .collect(Collectors.groupingBy(Arm::getType))
            .entrySet().stream()
            .map(e -> {
                var innerBuilder = new StringBuilder();
                int amt = e.getValue().size();
                innerBuilder.append(amt == 1 ? "a" : amt);
                innerBuilder.append(" ");
                innerBuilder.append(e.getKey().getName());
                if (amt > 1) {
                    innerBuilder.append("s");
                }
                return innerBuilder;
            })
            .collect(Collectors.joining(" and ", "", ""));
    }

    public String describe(Character owner) {
        List<RoboArm> roboArms = arms.stream().filter(arm -> arm instanceof RoboArm).map(arm -> (RoboArm)arm).collect(Collectors.toList());
        List<TentacleArm> tentacleArms = arms.stream().filter(arm -> arm instanceof TentacleArm).map(arm -> (TentacleArm)arm).collect(Collectors.toList());
        String msg = "";
        if (!roboArms.isEmpty()) {
            msg += "<b>You can see " + describeArms(roboArms) + " strapped behind "
                            + owner.possessiveAdjective() + " back.</b><br/>";
        }
        if (!tentacleArms.isEmpty()) {
            msg += "You can see " + tentacleArms.size() + " tentacles attached to " + owner.possessiveAdjective() + " back.<br/>";
            msg += tentacleArms.stream().map(Arm::describe).collect(Collectors.joining("<br/>"));
            msg += "<br/>";
        }
        return msg;
    }

    private List<Arm> handleMultiArmMoves(Combat c, Character owner, Character target) {
        List<Arm> remaining = arms;
        Collections.shuffle(MULTI_MOVES);
        for (MultiArmMove mam : MULTI_MOVES) {
            if (mam.shouldExecute()) {
                Optional<List<Arm>> used = mam.getInvolvedArms(c, owner, target, remaining);
                if (used.isPresent()) {
                    remaining.removeAll(used.get());
                    mam.execute(c, owner, target, used.get());
                }
            }
        }
        return remaining;
    }

    private void doArmAction(Arm arm, Combat c, Character owner, Character target) {
        if (arm.attackOdds(c, owner, target) > Global.random(100)) {
            Optional<ArmSkill> skill = Global.pickRandom(arm.getSkills(c, owner, target)
                                                  .stream()
                                                  .filter(s -> s.usable(c, arm, owner, target))
                                                  .toArray(ArmSkill[]::new));
            if (skill.isPresent()) {
                c.write(PetCharacter.DUMMY, String.format("<b>%s %s uses %s</b>", owner.nameOrPossessivePronoun(),
                                arm.getName(), skill.get().getName()));
                skill.get().resolve(c, arm, owner, target);
                return;
            }
        }
        new Idle().resolve(c, arm, owner, target);
    }

    public void act(Combat c, Character owner, Character target) {
        if (arms.isEmpty()) {
            return;
        }
        List<Arm> available = handleMultiArmMoves(c, owner, target);
        available.forEach(a -> doArmAction(a, c, owner, target));
    }
}