package nightgames.match.actions;

import edu.emory.mathcs.backport.java.util.Collections;
import nightgames.areas.Area;
import nightgames.global.Global;
import nightgames.match.Participant;
import nightgames.modifier.standard.NudistModifier;

import java.util.ArrayList;
import java.util.Set;

public class ResupplyNormal extends Resupply {

    public final class Instance extends Resupply.Instance {

        private Instance(Participant user, Area location) {
            super(user, location);
        }

        @Override
        public void execute() {
            if (Global.getMatch().getCondition().name().equals(NudistModifier.NAME)) {
                user.getCharacter().message(
                        "You check in so that you're eligible to fight again, but you still don't get any clothes.");
            } else {
                user.getCharacter().message("You pick up a change of clothes and prepare to get back in the fray.");
            }
            super.execute();
        }
    }

    public final class State extends Resupply.State {
        @Override
        public void move(Participant p) {
            super.move(p);
            if (p.getLocation().getOccupants().size() > 1) {
                var escapeRoute = escapeRoutes.stream().filter(EscapeRoute::usable).findFirst();
                escapeRoute.or(() -> {
                    var shuffledRoutes = new ArrayList<>(escapeRoutes);
                    Collections.shuffle(shuffledRoutes);
                    return shuffledRoutes.stream().findFirst();
                }).ifPresent(route -> route.use(p));
            }
        }
    }

    private final Set<EscapeRoute> escapeRoutes;

    public ResupplyNormal(Set<EscapeRoute> escapeRoutes) {
        this.escapeRoutes = escapeRoutes;
    }
}
