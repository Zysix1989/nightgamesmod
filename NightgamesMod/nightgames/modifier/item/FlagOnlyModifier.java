package nightgames.modifier.item;

import nightgames.items.Item;
import nightgames.match.Participant;
import nightgames.match.ftc.Prey;

public class FlagOnlyModifier extends ItemModifier {
    private static final String name = "flag-only";

    @Override
    public boolean itemIsBanned(Participant p, Item i) {
        return p instanceof Prey && i != Item.Flag;
    }

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }
}
