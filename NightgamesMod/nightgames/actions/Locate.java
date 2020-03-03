package nightgames.actions;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Global;
import nightgames.gui.GUI;
import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.Status;
import nightgames.status.Detected;
import nightgames.status.Horny;

import java.util.stream.Collectors;

public class Locate extends Action {
    private static final long serialVersionUID = 1L;
    private static final int MINIMUM_SCRYING_REQUIREMENT = 5;

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
    public IMovement execute(Participant self) {
        GUI gui = Global.gui();
        gui.clearText();
        gui.validate();
        if (self.getCharacter().human()) {
            gui.message("Thinking back to your 'games' with Reyka, you take out a totem to begin a scrying ritual: ");
        }
        startEvent(self.getCharacter());
        return Movement.locating;
    }

    public final void startEvent(Character self) {
        self.chooseLocateTarget(this,
                Global.getMatch().getParticipants().stream()
                        .filter(p -> self.getAffection(p.getCharacter()) >= MINIMUM_SCRYING_REQUIREMENT)
                        .map(Participant::getCharacter)
                        .collect(Collectors.toList()));
    }

    public final void eventBody(Character self, String choice) {
        var gui = Global.gui();
        Character target = Global.getMatch().getParticipants().stream()
                .filter(p -> p.getCharacter().getTrueName().equals(choice))
                .findAny()
                .orElseThrow()
                .getCharacter();
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

    @Override
    public IMovement consider() {
        return Movement.locating;
    }

    @Override
    public boolean freeAction() {
        return true;
    }
}
