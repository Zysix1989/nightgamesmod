package nightgames.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Trait;
import nightgames.characters.body.BodyPart;
import nightgames.global.Global;
import nightgames.status.Disguised;
import nightgames.status.Stsflag;

public class Disguise extends Action {
    private static final long serialVersionUID = 2089054062272510717L;

    public Disguise() {
        super("Disguise");
    }

    @Override
    public boolean usable(Character user) {
        return !user.bound() && user.has(Trait.Imposter) && !user.is(Stsflag.disguised) && getRandomNPC(user) != null;
    }

    private NPC getRandomNPC(Character user) {
        NPC target = (NPC) Global.pickRandom(Global.getParticipants()
                        .stream().filter(other -> !other.human() 
                                        && user != other 
                                        && !other.has(Trait.cursed)
                                        && !Global.checkCharacterDisabledFlag(other))
                        .collect(Collectors.toList())).orElse(null);
        return target;
    }

    @Override
    public IMovement execute(Character user) {
        NPC target = getRandomNPC(user);
        if (target != null) {
            user.addNonCombat(new Disguised(user, target));
            user.body.clearReplacements();
            Collection<BodyPart> currentParts = new ArrayList<>(user.body.getCurrentParts());
            currentParts.forEach(part -> user.body.temporaryRemovePart(part, 1000));
            target.body.getCurrentParts().forEach(part -> user.body.temporaryAddPart(part, 1000));
            user.getTraits().forEach(t -> user.removeTemporaryTrait(t, 1000));
            target.getTraits().forEach(t -> user.addTemporaryTrait(t, 1000));
            user.completelyNudify(null);
            target.outfitPlan.forEach(user.outfit::equip);
        }
        return Movement.disguise;
    }

    @Override
    public IMovement consider() {
        return Movement.disguise;
    }

}
