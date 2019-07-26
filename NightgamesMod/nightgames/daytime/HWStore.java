package nightgames.daytime;

import java.util.ArrayList;
import java.util.Map;

import java.util.Set;
import java.util.stream.Collectors;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.items.Loot;

public class HWStore extends Store {
    public HWStore(Character player) {
        super("Hardware Store", player);
        add(Item.Tripwire);
        add(Item.Rope);
        add(Item.Spring);
        add(Item.Sprayer);
        add(Item.EmptyBottle);
    }

    @Override
    public boolean known() {
        return Global.checkFlag(Flag.basicStores);
    }

    @Override
    public void visit(String choice) {
        Global.gui().clearText();
        Global.gui().clearCommand();
        if (choice.equals("Start")) {
            acted = false;
        }
        if (choice.equals("Leave")) {
            done(acted);
            return;
        }
        checkSale(choice);
        if (player.human()) {
            Global.gui().message(
                            "Nothing at the hardware store is designed for the sort of activities you have in mind, but there are components you could use to make some "
                                            + "effective traps.");
            Map<Item, Integer> MyInventory = this.player.getInventory();
            for (Item i : getItems()) {
                if (MyInventory.get(i) == null || MyInventory.get(i) == 0) {
                    Global.gui().message(i.getName() + ": $" + i.getPrice());
                } else {
                    Global.gui().message(
                                    i.getName() + ": $" + i.getPrice() + " (you have: " + MyInventory.get(i) + ")");
                }
            }
            Global.gui().message("You have : $" + player.money + " to spend.");
            Set<Loot> purchasableItems = getGoods();
            ArrayList<String> choices = new ArrayList<>();
            choices.add("Leave");
            player.chooseShopOption(this, purchasableItems, choices);
        }
    }

    @Override
    protected Set<Item> getItems() {
        return stock.keySet().stream()
            .filter(i -> i != Item.EmptyBottle || player.getRank() > 0)
            .collect(Collectors.toSet());
    }

    @Override
    public void shop(Character npc, int budget) {
        int remaining = budget;
        int bored = 0;
        while (remaining > 10 && bored < 10) {
            for (Item i : stock.keySet()) {
                boolean emptyBottleCheck = npc.has(Trait.madscientist) || i != Item.EmptyBottle;
                if (remaining > i.getPrice() && !npc.has(i, 20) && emptyBottleCheck) {
                    npc.gain(i);
                    npc.money -= i.getPrice();
                    remaining -= i.getPrice();
                } else {
                    bored++;
                }
            }
        }
    }
}
