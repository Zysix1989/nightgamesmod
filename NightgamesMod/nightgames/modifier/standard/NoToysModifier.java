package nightgames.modifier.standard;

import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.modifier.BaseModifier;
import nightgames.modifier.item.BanToysModifier;

import java.util.Map;

public class NoToysModifier extends BaseModifier {

    public static final String NAME = "notoys";

    public NoToysModifier() {
        items = new BanToysModifier();
    }

    @Override
    public int bonus() {
        return 50;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String intro() {
        return "<i>\"I've only got a small bonus available tonight, so I have a simple little handicap for you. Leave your sex toys under the bed tonight. You're better off "
                        + "getting some practice with your fingers, tongue, or whatever other body parts you like sticking into people. Liquids, traps, any consumables are fine, only "
                        + "toys are off limits. The bonus is only $" + bonus()
                        + ", but it shouldn't give you much trouble.\"</i>";
    }

    @Override
    public String acceptance() {
        return "You agree to Lilly's terms and hand over all the sex toys you have on you. She carefully looks over each of the devices, which makes you feel awkward for reasons "
                        + "you can't quite explain. <i>\"Is this really the best you have? You're going to need to up your game soon if you want to be competitive. These don't even match up "
                        + "to the toys I have for personal use.\"</i>";
    }

    @Override
    public boolean isApplicable() {
        Map<Item, Integer> inv = Global.getPlayer().getInventory();
        for (Item item : BanToysModifier.TOYS) {
            if (inv.containsKey(item)) {
                return true;
            }
        }
        return false;
    }
}
