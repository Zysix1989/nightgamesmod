package nightgames.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Detected;
import nightgames.status.Horny;

import java.util.stream.Collectors;

public class Locate extends Action {
    private static final long serialVersionUID = 1L;
    private static final int MINIMUM_SCRYING_REQUIREMENT = 5;

    private static class Aftermath extends Action.Aftermath {
        private Aftermath() {}

        @Override
        public String describe(Character c) {
            return String.format(" is holding someone's underwear in %s hands and breathing deeply. Strange.", c.possessiveAdjective());
        }
    }

    public Locate() {
        super("Locate");
    }

    @Override
    public boolean usable(Participant self) {
        boolean hasUnderwear = false;
        for (Item i : self.getCharacter().getInventory().keySet()) {
            // i hate myself for having to add this null check... why is inventory even public...
            if (i != null && i.toString().contains("Trophy")) {
                hasUnderwear = true;
            }
        }
        return self.getCharacter().has(Trait.locator) && hasUnderwear && !self.getCharacter().bound();
    }

    @Override
    public Action.Aftermath execute(Participant self) {
        startEvent(self.getCharacter());
        return new Aftermath();
    }

    public final void startEvent(Character self) {
        var msg = "Thinking back to your 'games' with Reyka, you take out a totem to begin a scrying ritual: ";
        self.chooseLocateTarget(this,
                Global.getMatch().getParticipants().stream()
                        .filter(p -> self.getAffection(p.getCharacter()) >= MINIMUM_SCRYING_REQUIREMENT)
                        .map(Participant::getCharacter)
                        .collect(Collectors.toList()),
                msg);
    }

    public final void eventBody(Character self, Character target) {
        var gui = Global.gui();
        Area area = target.location();
        gui.clearText();
        if (area != null) {
            gui.message("Drawing on the dark energies inside the talisman, you attempt to scry for "
                    + target.nameOrPossessivePronoun() + " location. In your mind, an image of the <b><i>"
                    + area.name
                    + "</i></b> appears. It falls apart as quickly as it came to be, but you know where "
                    + target.getTrueName()
                    + " currently is. Your small talisman is already burning up in those creepy "
                    + "purple flames, the smoke flowing from your nose straight to your crotch and setting another fire there.");
            target.addNonCombat(new Status(new Detected(target, 10)));
        } else {
            gui.message("Drawing on the dark energies inside the talisman, you attempt to scry for "
                    + target.nameOrPossessivePronoun() + " location. "
                    + "However, you draw a blank. Your small talisman is already burning up in those creepy "
                    + "purple flames, the smoke flowing from your nose straight to your crotch and setting another fire there.");
        }
        self.addNonCombat(new Status(new Horny(self, self.getArousal().max() / 10, 10, "Scrying Ritual")));
        self.leaveAction(this);
    }

    public final void endEvent() {
        Global.gui().clearText();
        Global.getMatch().resume();
    }

}
