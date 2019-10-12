package nightgames.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Trait;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.CockPart.Size;
import nightgames.characters.body.DemonicTailMod;
import nightgames.characters.body.EarsPart;
import nightgames.characters.body.mods.PointedEarsMod;
import nightgames.characters.body.PussyPart;
import nightgames.characters.body.TailPart;
import nightgames.characters.body.WingsPart;
import nightgames.characters.body.mods.catcher.DemonicMod;
import nightgames.characters.body.mods.TentacledMod;
import nightgames.characters.body.mods.pitcher.IncubusCockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.requirements.Requirement;
import nightgames.requirements.RequirementShortcuts;
import nightgames.status.Abuff;
import nightgames.status.Alluring;
import nightgames.status.Distorted;
import nightgames.status.Energized;
import nightgames.status.FluidAddiction;
import nightgames.status.Frenzied;
import nightgames.status.Horny;
import nightgames.status.InducedEuphoria;
import nightgames.status.Oiled;
import nightgames.status.Shamed;
import nightgames.status.Shield;
import nightgames.status.Stunned;
import nightgames.status.Trance;

public enum Item implements Loot {

    // Trap components
    Tripwire("Trip Wire", 10, "A strong wire used to trigger traps", "a "),
    Spring("Spring", 20, "A component for traps", "a "),
    Rope("Rope", 15, "A component for traps", "a "),
    Phone("Phone", 30, "A cheap disposable phone with a programable alarm", "a "),
    Sprayer("Sprayer", 30, "Necessary for making traps that use liquids", "a "),

    // Consumables
    ZipTie("Heavy Zip Tie", 5, "A thick heavy tie suitable for binding someone's hands", "a "),
    Handcuffs("Handcuffs", 200, "Strong steel restraints, hard to escape from", ""),
    Lubricant("Lubricant", 20, "Helps you pleasure your opponent, but makes her hard to hang on to",
        "some "),
    EnergyDrink("Energy Drink", 20, "It'll either kill you or restore your stamina", "an "),
    Aphrodisiac("Aphrodisiac", 40, "Can be thrown like a 'horny bomb'", "an "),
    Beer("Beer", 10, "Tastes like horsepiss, but it'll numb your senses", "a can of "),
    Sedative("Sedative", 25, "Tires out your opponent, but can also make her numb", "a "),
    DisSol("Dissolving Solution", 30, "Destroys clothes, but completely non-toxic", "a "),
    SPotion("Sensitivity Potion", 25,
        "Who knows whats in this stuff, but it makes any skin it touches tingle", "a "),
    EmptyBottle("Empty Bottle", 100, "A small flask that can be used to collect liquids.", "an "),

    semen("Semen", 100, "A small bottle filled with cum. Kinda gross", "a bottle of ",
        Arrays.asList((ItemEffect) new ConditionalEffect(
                new GroupEffect(Arrays.asList(
                    (ItemEffect) new BuffEffect("drink", "throw",
                        new Abuff(Global.noneCharacter(), Attribute.Dark, 2,
                            15)),
                    new BuffEffect("drink", "throw",
                        new Abuff(Global.noneCharacter(),
                            Attribute.Seduction, 2, 15)),
                    new BuffEffect("drink", "throw", new Alluring(Global.noneCharacter(), 5)),
                    new ResourceEffect("heal", 30), new ResourceEffect("build", 30),
                    new ResourceEffect("arouse", 10))), new ConditionalEffect.EffectCondition() {
                @Override
                public boolean operation(Combat c, Character user, Character opponent,
                    Item item) {
                    return user.has(Trait.succubus);
                }
            }),
            new ConditionalEffect(
                new GroupEffect(Arrays.asList(
                    (ItemEffect) new BuffEffect("drink", "throw",
                        new Shamed(Global.noneCharacter())),
                    new ResourceEffect("arouse", 10))),
                new ConditionalEffect.EffectCondition() {
                    @Override
                    public boolean operation(Combat c, Character user,
                        Character opponent, Item item) {
                        return !user.has(Trait.succubus);
                    }
                })),
        RequirementShortcuts.none(),
        15),
    HolyWater("\"Holy Water\"", 100, "A small flask filled with \"Holy Water\"", "a bottle of ",
        getHolyWaterEffects(),
        RequirementShortcuts.none(),
        15),
    nectar("Nectar", 100, "A glob of amber nectar", "a glob of ",
        Arrays.asList((ItemEffect) new GroupEffect(Arrays.asList(
            (ItemEffect) new ResourceEffect("heal", 100), new ResourceEffect("build", 50),
            new ResourceEffect("arouse", 10)))),
        RequirementShortcuts.none(),
        15),
    ExtremeAphrodisiac("Extreme Aphrodisiac", 100, "A succubus's pussy juices", "a bottle of ",
        Arrays.asList((ItemEffect) new GroupEffect(Arrays.asList(
            (ItemEffect) new BuffEffect("drink", "throw",
                new Trance(Global.noneCharacter(), 5)),
            new BuffEffect("drink", "throw", new InducedEuphoria(Global.noneCharacter())),
            new ResourceEffect("arouse", 50)))),
        RequirementShortcuts.none(),
        5),
    RawAether("Raw Aether", 100, "Raw Aether collected from an enchanted pussy", "a bottle of ",
        Arrays.asList((ItemEffect) new GroupEffect(Arrays.asList(
            (ItemEffect) new BuffEffect("drink", "throw",
                new Energized(Global.noneCharacter(), 25)),
            new ResourceEffect("build", 50)))),
        RequirementShortcuts.none(),
        25),
    LubricatingOils("Lubricating Oils", 100,
        "Artificial lubricant collected from a cybernetic pussy", "a bottle of ",
        Arrays.asList((ItemEffect) new GroupEffect(Arrays.asList(
            (ItemEffect) new BuffEffect("drink", "throw", new Oiled(Global.noneCharacter())),
            new BuffEffect("drink", "throw",
                new Horny(Global.noneCharacter(), 15, 10, "Aphrodisiac Oils"))))),
        RequirementShortcuts.none(),
        25),
    FeralMusk("Feral Musk", 100, "Musk collected from a feral pussy", "a bottle of ",
        Arrays
            .asList((ItemEffect) new GroupEffect(Arrays.asList((ItemEffect) new BuffEffect("drink",
                "throw", new Frenzied(Global.noneCharacter(), 7))))),
        RequirementShortcuts.none(),
        7),
    BioGel("Bio Gel", 100, "Goo collected from a slime-girl", "a bottle of ",
        Arrays.asList((ItemEffect) new GroupEffect(Arrays.asList(
            (ItemEffect) new BuffEffect("drink",
                "throw", new FluidAddiction(Global.noneCharacter(),
                Global.noneCharacter(), 2, 10)),
            new ResourceEffect("wprestore", 500)))),
        RequirementShortcuts.none(),
        10),
    MoltenDrippings("Molten Drippings", 100, "Excitement from a ki-filled pussy", "a bottle of ",
        Arrays.asList((ItemEffect) new GroupEffect(Arrays.asList(
            (ItemEffect) new ResourceEffect("pain", 500),
            (ItemEffect) new BuffEffect("drink",
                "throw", new Stunned(Global.noneCharacter(), 25, false)),
            (ItemEffect) new LevelUpEffect(1)
        ))),
        RequirementShortcuts.none(),
        10),
    TinyDraft("Tiny Draft", 100, "Temporarily shrink a penis", "a ",
        Collections.singleton((ItemEffect) new BodyDowngradeEffect("drink", "throw",
            new CockPart(Size.Average))),
        (c, self, target) -> self.body.getCockAbove(Size.min()) != null,
        15),
    PriapusDraft("Priapus Draft", 150, "Temporarily grow a penis", "a ",
        Collections.singleton((ItemEffect) new BodyGrowthPlusEffect("drink", "throw",
            new CockPart(Size.Average))),
        (c, self, target) -> !self.hasDick()
            || self.body.getCockBelow(Size.max()) != null,
        15),
    BustDraft("Bust Draft", 80, "Temporarily grow breasts", "a ", Collections.singleton(
        (ItemEffect) new BodyGrowthPlusEffect("drink", "throw",
            new BreastsPart(BreastsPart.Size.CCup))),
        (c, self, target) -> self.body.getBreastsBelow(BreastsPart.Size.max()) != null,
        15),
    FemDraft("Fem Draft", 150, "Temporarily grow a pussy", "a ", Arrays.asList(
        (ItemEffect) new BodyGrowthPlusEffect("drink", "throw",
            new BreastsPart(BreastsPart.Size.CCup)),
        (ItemEffect) new BodyReplaceEffect("drink", "throw", new PussyPart())),
        (c, self, target) -> !self.hasPussy(),
        15),
    Lactaid("Lactaid", 100, "Temporarily start lactating", "",
        Arrays.asList((ItemEffect) new AddTraitEffect("drink", "throw", Trait.lactating)),
        RequirementShortcuts.rev(RequirementShortcuts.noTrait(Trait.lactating)),
        15),
    SuccubusDraft("Succubus Draft", 600, "Temporarily turn into a succubus", "a ",
        Arrays.asList((ItemEffect) new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Dark, 10, 15)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Seduction, 5, 15)),
            new AddTraitEffect("drink", "throw", Trait.addictivefluids),
            new AddTraitEffect("drink", "throw", Trait.succubus),
            new PartModEffect("drink", "throw", PussyPart.TYPE, new DemonicMod(), 15),
            new PartModEffect("drink", "throw", CockPart.TYPE, new IncubusCockMod(), 15),
            new PartModEffect("drink", "throw", EarsPart.TYPE, new PointedEarsMod(), 15),
            new BodyReplaceEffect("drink", "throw", WingsPart.demonic),
            new BodyReplaceEffect("drink", "throw", new TailPart()),
            new PartModEffect("drink", "throw", TailPart.TYPE, new DemonicTailMod(), 15),
            new BodyGrowthPlusEffect("drink", "throw",
                new BreastsPart(BreastsPart.Size.DDCup))),
        RequirementShortcuts.rev(RequirementShortcuts.noTrait(Trait.succubus)),
        15),
    IncubusDraft("Incubus Draft", 600, "Temporarily turn into a incubus", "a ",
        Arrays.asList((ItemEffect) new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Dark, 10, 15)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Seduction, 5, 15)),
            new AddTraitEffect("drink", "throw", Trait.addictivefluids),
            new AddTraitEffect("drink", "throw", Trait.incubus),
            new PartModEffect("drink", "throw", CockPart.TYPE, new DemonicMod(), 15),
            //TODO: Grow a cock if you don't already have one.
            new PartModEffect("drink", "throw", EarsPart.TYPE, new PointedEarsMod(), 15),
            new BodyReplaceEffect("drink", "throw", WingsPart.demonic),
            new BodyReplaceEffect("drink", "throw", new TailPart()),
            new PartModEffect("drink", "throw", TailPart.TYPE, new DemonicTailMod(), 15)
            //new BodyModEffect("drink", "throw", BreastsPart.dd, BodyModEffect.Effect.growplus)
        ),
        RequirementShortcuts.rev(RequirementShortcuts.noTrait(Trait.succubus)),
        15),
    OmnibusDraft("Omnibus Draft", 1300, "Temporarily turn into an demonic omnibus", "a ",
        Arrays.asList((ItemEffect) new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Dark, 10, 15)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Seduction, 5, 15)),
            new AddTraitEffect("drink", "throw", Trait.addictivefluids),
            new AddTraitEffect("drink", "throw", Trait.incubus),
            new PartModEffect("drink", "throw", CockPart.TYPE, new DemonicMod(), 15),
            //TODO: Grow a cock if you don't already have one.
            new PartModEffect("drink", "throw", EarsPart.TYPE, new PointedEarsMod(), 15),
            new BodyReplaceEffect("drink", "throw", WingsPart.demonic),
            new BodyReplaceEffect("drink", "throw", new TailPart()),
            new PartModEffect("drink", "throw", TailPart.TYPE, new DemonicTailMod(), 15),
            new BodyGrowthPlusEffect("drink", "throw",
                new BreastsPart(BreastsPart.Size.DDCup))),
        RequirementShortcuts.rev(RequirementShortcuts.noTrait(Trait.succubus)),
        15),
    TentacleTonic("Tentacle Tonic", 600, "Temporarily grow tentacles", "a ",
        Arrays.asList((ItemEffect) new GrowTentaclesEffect("drink", "throw", 15),
            new MaybeEffect(new GrowTentaclesEffect("drink", "throw", 15), .5),
            new MaybeEffect(new GrowTentaclesEffect("drink", "throw", 15), .25),
            new MaybeEffect(new GrowTentaclesEffect("drink", "throw", 15), .1),
            new MaybeEffect(new PartModEffect("drink", "throw", PussyPart.TYPE, new TentacledMod(), 15),
                .3)),
        RequirementShortcuts.none(),
        15),
    JuggernautJuice("Juggernaut Juice", 350, "Makes you nigh invulnerable.", "a ",
        Arrays.asList((ItemEffect) new BuffEffect("drink", "throw",
                new Shield(Global.noneCharacter(), .5, 10)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Power, 5, 10)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Cunning, -5, 10)),
            new RemoveTraitEffect("drink", "throw", Trait.achilles)),
        RequirementShortcuts.rev(RequirementShortcuts.attribute(Attribute.Cunning, 5)),
        10),
    BewitchingDraught("Bewitching Draught", 350, "Makes you inhumanly alluring.", "a ",
        Arrays.asList((ItemEffect) new BuffEffect("drink", "throw",
                new Alluring(Global.noneCharacter(), 10)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Seduction, 5, 10)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Power, -5, 10)),
            new AddTraitEffect("drink", "throw", Trait.RawSexuality),
            new AddTraitEffect("drink", "throw", Trait.augmentedPheromones)),
        RequirementShortcuts.rev(RequirementShortcuts.attribute(Attribute.Power, 5)),
        10),
    TinkersMix("TinkersMix", 250, "Not sure if it's a good idea to drink this...", "a ",
        Arrays.asList((ItemEffect) new BuffEffect("drink", "throw",
                new Distorted(Global.noneCharacter(), 10)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Cunning, 5, 10)),
            new BuffEffect("drink", "throw",
                new Abuff(Global.noneCharacter(), Attribute.Seduction, -5, 10)),
            new AddTraitEffect("drink", "throw", Trait.lacedjuices),
            new AddTraitEffect("drink", "throw", Trait.aikidoNovice)),
        RequirementShortcuts.rev(RequirementShortcuts.attribute(Attribute.Seduction, 5)),
        10),
    MinorScroll("Minor Summoning Scroll", 50,
        "Less potent than usual, can be used in combat if you know magic", "a "),
    Talisman("Dark Talisman", 500, "An innocent-looking carving imbued with dark magic", "a "),
    Needle("Drugged Needle", 10, "A thin needle coated in a mixture of aphrodisiacs and sedatives",
        "a "),
    SmokeBomb("Smoke Bomb", 20, "For those quick getaways", "a "),
    RemoteControl("Remote Control", 999, "Not in the traditional sense.", "a "),

    // Equipment
    Dildo("Dildo", 250, "Big rubber cock: not a weapon", "a "),
    Crop("Riding Crop", 200, "Delivers a painful sting to instill discipline", "a "),
    Onahole("Onahole", 300,
        "An artificial vagina, but you can probably find some real ones pretty easily", "an "),
    Tickler("Tickler", 300, "Tickles and pleasures your opponent's sensitive areas", "a "),
    Strapon("Strap-on Dildo", 600, "Penis envy much?", "a "),
    Dildo2("Sonic Dildo", 2000, "Apparently vibrates at the ideal frequency to produce pleasure",
        "a "),
    Dildo3("Auto-Dildo", 2000,
        "In addition to vibrating, enlarges when inserted to make it stay in place", "a "),
    Crop2("Hunting Crop", 1500, "Equipped with the fearsome Treasure Hunter attachment", "a "),
    Onahole2("Wet Onahole", 3000, "As hot and wet as the real thing", "an "),
    Tickler2("Enhanced Tickler", 3000, "Coated with a substance that can increase sensitivity",
        "an "),
    Strapon2("Flex-O-Peg", 2500, "A more flexible and versatile strapon with a built in vibrator",
        "the patented "),
    ShockGlove("Shock Glove", 800, "Delivers a safe, but painful electric shock", "a "),
    Aersolizer("Aerosolizer", 500, "Turns a liquid into an unavoidable cloud of mist", "an "),
    Battery("Battery", 0, "Available energy to power electronic equipment", "a "),
    MedicalSupplies("Medical Supplies", 0,
        "Basic medical supplies that contains a syringe, some bandages, and a pair of rubber gloves among others",
        ""),
    Blindfold("Blindfold", 50, "A blindfold one might use to sleep.", "a "),

    // Unused
    Clothing("Set of Clothes", 0, "A trophy of your victory", "a "),
    Ward("Dark Ward", 100, "", "a "),
    Capacitor("Capacitor", 30, "", "a "),

    // Trophies
    CassieTrophy("Cassie's Panties", 0, "Cute and simple panties", ""),
    MaraTrophy("Mara's Underwear", 0, "She wears boys underwear?", ""),
    AngelTrophy("Angel's Thong", 0, "There's barely anything here", ""),
    JewelTrophy("Jewel's Panties", 0, "Surprisingly lacy", ""),
    ReykaTrophy("Reyka's Clit Ring", 0, "What else can you take from someone who goes commando?",
        ""),
    PlayerTrophy("Your Boxers", 0, "How did you end up with these?", ""),
    EveTrophy("Eve's 'Panties'", 0, "Crotchless and of no practical use", ""),
    KatTrophy("Kat's Panties", 0, "Cute pink panties", ""),
    AiriTrophy("A piece of hardened gel", 0, "Not sure what else to take", ""),
    YuiTrophy("Yui's Panties", 0, "", ""),
    MayaTrophy("Maya's Panties", 0, "Lacy, stylish, and coveted by all", ""),
    RoseaTrophy("Rosea's Vine Thong", 0, "", "A sexy thong made of leaves and vines."),
    SamanthaTrophy("Samantha's Lacy Thong", 0,
        "A lacy red thong, translucent in all but the most delicate areas.", ""),
    MiscTrophy("Someone's underwear", 0, "", ""),

    // Ingredients
    FaeScroll("Summoning Scroll", 150, "", "a "),
    Totem("Fetish Totem", 150, "A small penis shaped totem that can summon tentacles", "a "),

    // Misc
    Flag("Flag", 0, "A small red ribbon. Worth points.", "The ");

    /**
     * The Item's display name.
     */
    private String desc;
    private String name;
    private String prefix;
    private int price;
    private List<ItemEffect> effect;
    int duration;
    private Requirement req;

    /**
     * @return the Item name
     */
    public String getDesc() {
        return desc;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String pre() {
        return prefix;
    }

    @Override
    public void pickup(Character owner) {
        owner.gain(this);
    }

    /**Constructor for any given Item.
     * @param name
     * The name of the Item
     * @param price 
     * The price of the item in the shop.
     * @param desc
     * A short text description of the item.
     * @param prefix
     * describes the item in a singular fashion; Ex: "A Bottle of"
     * */
    private Item(String name, int price, String desc, String prefix) {
        this(name, price, desc, prefix, Collections.singleton(new ItemEffect()), RequirementShortcuts.none(), 0);
    }

    /**Constructor for any given Item.
     * @param name
     * The name of the Item
     * @param price 
     * The price of the item in the shop.
     * @param desc
     * A short text description of the item.
     * @param prefix
     * describes the item in a singular fashion; Ex: "A Bottle of"
     * @param effect
     * A collection of ItemEffects. 
     * @param req
     * A requirement for this item
     * @param duration
     * The duration of the item's effects.
     * */
    private Item(String name, int price, String desc, String prefix, Collection<ItemEffect> effect, Requirement req, int duration) {
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.prefix = prefix;
        this.effect = new ArrayList<ItemEffect>(effect);
        this.duration = duration;
        this.req = req;
    }

    public List<ItemEffect> getEffects() {
        return effect;
    }

    @Override public String getID() {
        return name();
    }

    public ItemAmount amount(int amount) {
        return new ItemAmount(this, amount);
    }

    public boolean usable(Combat c, Character self, Character target) {
        return req.meets(c, self, target);
    }
    
    // I'll take 'Things I Never Expected to Code' for 200, Alex. 
    private static Collection<ItemEffect> getSemenEffects() {
        return Arrays.asList((ItemEffect) new ConditionalEffect(
                        new GroupEffect(Arrays.asList(
                                        (ItemEffect) new BuffEffect("drink", "throw",
                                                        new Abuff(Global.noneCharacter(), Attribute.Dark, 2,
                                                                        15)),
                                        new BuffEffect("drink", "throw",
                                                        new Abuff(Global.noneCharacter(),
                                                                        Attribute.Seduction, 2, 15)),
                        new BuffEffect("drink", "throw", new Alluring(Global.noneCharacter(), 5)),
                        new ResourceEffect("heal", 30), new ResourceEffect("build", 30),
                        new ResourceEffect("arouse", 10))), new ConditionalEffect.EffectCondition() {
                            @Override
                            public boolean operation(Combat c, Character user, Character opponent,
                                            Item item) {
                                return user.has(Trait.succubus);
                            }
                        }),
                        new ConditionalEffect(
                                        new GroupEffect(Arrays.asList(
                                                        (ItemEffect) new BuffEffect("drink", "throw",
                                                                        new Shamed(Global.noneCharacter())),
                                                        new ResourceEffect("arouse", 10))),
                                        new ConditionalEffect.EffectCondition() {
                                            @Override
                                            public boolean operation(Combat c, Character user,
                                                            Character opponent, Item item) {
                                                return !user.has(Trait.succubus);
                                            }
                                        }));
    }
    
    private static Collection<ItemEffect> getHolyWaterEffects() {
        return Arrays.asList((ItemEffect) new ConditionalEffect(
                        new GroupEffect(Arrays.asList(
                                        (ItemEffect) new BuffEffect("drink", "throw",
                                                        new Abuff(Global.noneCharacter(),
                                                                        Attribute.Divinity, 2, 15)),
                                        new BuffEffect("drink", "throw",
                                                        new Alluring(Global.noneCharacter(), 5)),
                        new ResourceEffect("heal", 100), new ResourceEffect("build", 30),
                        new ResourceEffect("arouse", 10))), new ConditionalEffect.EffectCondition() {
                            @Override
                            public boolean operation(Combat c, Character user, Character opponent,
                                            Item item) {
                                return !user.isDemonic();
                            }
                        }),
                        new ConditionalEffect(
                                        new GroupEffect(Arrays.asList(
                                                        (ItemEffect) new TextEffect("drink", "throw",
                                                                        "The \"holy water\" splashes onto {self:name-possessive} demonic body, eliciting a shriek from the demon."),
                                                        new BuffEffect("drink", "throw",
                                                                        new Abuff(Global.noneCharacter(),
                                                                                        Attribute.Dark, -10,
                                                                                        15)),
                                                        new ResourceEffect("pain", 100))),
                                        new ConditionalEffect.EffectCondition() {
                                            @Override
                                            public boolean operation(Combat c, Character user,
                                                            Character opponent, Item item) {
                                                return user.isDemonic();
                                            }
                                        }));
        
    }
}
