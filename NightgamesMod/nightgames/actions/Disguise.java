package nightgames.actions;

import nightgames.characters.Character;
import nightgames.characters.NPC;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Disguised;
import nightgames.status.Stsflag;

import java.util.stream.Collectors;

public class Disguise extends Action {
    private static final long serialVersionUID = 2089054062272510717L;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {
        }

        @Override
        public String describe(Character c) {
            return Movement.disguise.describe(c);
        }
    }

    public Disguise() {
        super("Disguise");
    }

    @Override
    public boolean usable(Participant user) {
        return !user.getCharacter().bound()
                && user.getCharacter().has(Trait.Imposter)
                && !user.getCharacter().is(Stsflag.disguised)
                && getRandomNPC(user.getCharacter()) != null;
    }

    private NPC getRandomNPC(Character user) {
        return (NPC) Global.pickRandom(Global.getParticipants()
                        .stream().filter(other -> !other.human()
                                        && user != other
                                        && !other.has(Trait.cursed)
                                        && !Global.checkCharacterDisabledFlag(other))
                        .collect(Collectors.toList())).orElse(null);
    }

    @Override
    public Action.Aftermath execute(Participant user) {
        NPC target = getRandomNPC(user.getCharacter());
        if (target != null) {
            user.getCharacter().addNonCombat(new Status(new Disguised(user.getCharacter(), target)));
            user.getCharacter().body.mimic(target.body);
            user.getCharacter().getTraits().forEach(t -> user.getCharacter().removeTemporaryTrait(t, 1000));
            target.getTraits().forEach(t -> user.getCharacter().addTemporaryTrait(t, 1000));
            user.getCharacter().completelyNudify(null);
            target.outfitPlan.forEach(user.getCharacter().outfit::equip);
        }
        return new Aftermath();
    }

}
