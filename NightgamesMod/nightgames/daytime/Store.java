package nightgames.daytime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.Clothing;

public abstract class Store extends Activity {
    protected HashMap<Item, Integer> stock;
    protected HashMap<Clothing, Integer> clothingstock;
    protected boolean acted;

    public Store(String name, Character player) {
        super(name, player);
        stock = new HashMap<Item, Integer>();
        clothingstock = new HashMap<Clothing, Integer>();
        acted = false;
    }

    @Override
    public abstract boolean known();

    @Override
    public abstract void visit(String choice);

    public void add(Item item) {
        stock.put(item, item.getPrice());
    }

    public void add(Clothing item) {
        clothingstock.put(item, item.getPrice());
    }

    public HashMap<Clothing, Integer> clothing() {
        return clothingstock;
    }

    protected Set<Clothing> getClothes() {
        return clothingstock.keySet().stream()
            .filter(clothing -> !player.has(clothing))
            .collect(Collectors.toSet());
    }

    protected Set<Item> getItems() {
        return stock.keySet();
    }

    protected Set<Loot> getGoods() {
        HashSet<Loot> purchasableLoot = new HashSet<>(getClothes());
        purchasableLoot.addAll(getItems());
        return purchasableLoot;
    }

    protected boolean checkSale(String name) {
        for (Item i : stock.keySet()) {
            if (name.equals(i.getName())) {
                buy(i);
                return true;
            }
        }
        for (Clothing i : clothingstock.keySet()) {
            if (name.equals(i.getName())) {
                buy(i);
                return true;
            }
        }
        return false;
    }

    public void buy(Item item) {
        int price = stock.getOrDefault(item, item.getPrice());
        if (player.money >= price) {
            player.modMoney(-price);
            player.gain(item);
            acted = true;
            Global.gui().refresh();
        } else {
            Global.gui().message("You don't have enough money to purchase that.");
        }
    }

    public void buy(Clothing item) {
        int price = clothingstock.getOrDefault(item, item.getPrice());
        if (player.money >= price) {
            player.modMoney(-price);
            player.gain(item);
            acted = true;
            Global.gui().refresh();
        } else {
            Global.gui().message("You don't have enough money to purchase that.");
        }

    }
}
