package nightgames.actions;

import nightgames.characters.Character;

public enum Movement implements IMovement {
    // All of the movement strings are appended to 'You notice NAME', to produce
    // a line like 'You notice Cassie move to the indoor pool."
    hide(" disappear into a hiding place."),
    trap(" start rigging up something weird, probably a trap."),
    bathe(" start bathing in the nude, not bothered by your presence."),
    scavenge(" begin scrounging through some boxes in the corner."),
    craft(" start mixing various liquids. Whatever it is doesn't look healthy."),
    wait(" loitering nearby"),
    struggle((Character n) -> String.format(" is struggling against %s bondage.", n.possessiveAdjective())),
    resupply(" heads for one of the safe rooms, probably to get a change of clothes."),
    oil((Character n) -> String.format(" rubbing body oil on every inch of %s skin. Wow, you wouldn't mind watching that again.", n.possessiveAdjective())),
    enerydrink(" opening an energy drink and downing the whole thing."),
    beer(" opening a beer and downing the whole thing."),
    recharge(" plugging a battery pack into a nearby charging station."),
    locating((Character n) -> String.format(" is holding someone's underwear in %s hands and breathing deeply. Strange.", n.possessiveAdjective())),
    mana((Character n) -> String.format(" doing something with a large book. When %s's finished, you can see a sort of aura coming from %s.", n.pronoun(), n.possessiveAdjective())),
    retire(" has left the match."),
    ftcTreeAmbush(" climb up a tree."),
    ftcBushAmbush(" dive into some bushes."),
    ftcPassAmbush(" slip into an alcove."),
    disguise(" shimmer and turn into someone else!"),
    masturbate((Character n) -> {
        String mast;
        if (n.hasDick()) {
            mast = String.format(" starts to stroke %s cock ", n.possessiveAdjective());
        } else if (n.hasPussy()) {
            mast = String.format(" starts to stroke %s pussy ", n.possessiveAdjective());
        } else {
            mast = String.format(" starts to finger %s ass ", n.possessiveAdjective());
        }
        return mast + "while trying not to make much noise. It's quite a show.";
    });

    private interface DescriptionProducer {
        String getDescriptionFor(Character doer);
    }
    private DescriptionProducer producer;

    /**
     * @return the Item name
     */
    public String describe(Character doer) {
        return producer.getDescriptionFor(doer);
    }

    Movement(String desc) {
        this.producer = n -> desc;
    }
    
    Movement(DescriptionProducer producer) {
        this.producer = producer;
    }
}
