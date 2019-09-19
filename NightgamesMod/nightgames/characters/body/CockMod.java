package nightgames.characters.body;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.mods.BlessedCockMod;
import nightgames.characters.body.mods.CyberneticMod;
import nightgames.characters.body.mods.DemonicMod;
import nightgames.characters.body.mods.FeralMod;
import nightgames.characters.body.mods.FieryMod;
import nightgames.characters.body.mods.IncubusCockMod;
import nightgames.characters.body.mods.PartMod;
import nightgames.characters.body.mods.PrimalCockMod;
import nightgames.characters.body.mods.RunicCockMod;
import nightgames.characters.body.mods.SlimyCockMod;
import nightgames.combat.Combat;
import nightgames.global.Global;
import nightgames.pet.PetCharacter;
import nightgames.skills.damage.DamageType;
import nightgames.status.Abuff;
import nightgames.status.CockBound;
import nightgames.status.DivineCharge;
import nightgames.status.Drained;
import nightgames.status.Enthralled;
import nightgames.status.FluidAddiction;
import nightgames.status.Horny;
import nightgames.status.Hypersensitive;
import nightgames.status.Pheromones;
import nightgames.status.Stsflag;
import nightgames.status.Winded;

public class CockMod extends PartMod {
    public static final CockMod error = new CockMod("error", 1.0, 1.0, 1.0);
    public static final CockMod slimy = new SlimyCockMod("slimy", .5, 1.5, .7);
    public static final CockMod runic= new RunicCockMod("runic", 2.0, 1.0, 1.0);
    public static final CockMod blessed = new BlessedCockMod("blessed", 1.0, 1.0, .75);
    public static final CockMod incubus= new IncubusCockMod("incubus", 1.25, 1.3, .9);
    public static final CockMod primal = new PrimalCockMod("primal", 1.0, 1.4, 1.2);
    public static final CockMod bionic = new CockMod("bionic", .8, 1.3, .5);
    public static final CockMod enlightened = new CockMod("enlightened", 1.0, 1.2, .8);
    public static final List<CockMod> ALL_MODS = Arrays.asList(slimy, runic, blessed, incubus, primal, bionic, enlightened);

    protected CockMod(String name, double hotness, double pleasure, double sensitivity) {
        super(name, hotness, pleasure, sensitivity, 0);
    }

    @Override
    public void loadData(JsonElement element) {
        Optional<CockMod> other = getFromType(element.getAsString());
        other.ifPresent(otherMod -> {
            this.modType = otherMod.modType;
            this.pleasure = otherMod.pleasure;
            this.hotness = otherMod.hotness;
            this.sensitivity = otherMod.sensitivity;
        });
    }

    public JsonElement saveData() {
        return new JsonPrimitive(getModType());
    }

    @Override
    public double applyBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        double bonus = super.applyBonuses(c, self, opponent, part, target, damage);
        if (this.equals(bionic)) {
            String message = "";
            if (Global.random(5) == 0 && target.getType().equals("pussy")) {
                message += String.format(
                                "%s %s out inside %s %s, pressing the metallic head of %s %s tightly against %s cervix. "
                                                + "Then, a thin tube extends from %s uthera and into %s womb, pumping in a powerful aphrodisiac that soon has %s sensitive and"
                                                + " gasping for more.",
                                self.subject(), self.human() ? "bottom" : "bottoms", opponent.nameOrPossessivePronoun(),
                                target.describe(opponent), self.possessiveAdjective(), part.describe(self),
                                opponent.possessiveAdjective(), self.possessiveAdjective(), opponent.possessiveAdjective(),
                                opponent.directObject());
                opponent.add(c, new Hypersensitive(opponent));
                // Instantly addict
                opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
                opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
                opponent.add(c, new FluidAddiction(opponent, self, 1, 2));
                bonus -= 3; // Didn't actually move around too much
            } else if (target.moddedPartCountsAs(opponent, FieryMod.INSTANCE)) {
                message += String.format(
                                "Sensing the flesh around it, %s %s starts spinning rapidly, vastly increasing the friction against the walls of %s %s.",
                                self.nameOrPossessivePronoun(), part.describe(self), opponent.nameOrPossessivePronoun(),
                                target.describe(opponent));
                bonus += 5;
                if (Global.random(5) == 0) {
                    message += String.format(
                                    " The intense sensations cause %s to forget to breathe for a moment, leaving %s literally breathless.",
                                    opponent.subject(), opponent.directObject());
                    opponent.add(c, new Winded(opponent, 1));
                }
            }
            c.write(self, message);
        } else if (this.equals(enlightened)) {
            String message = "";
            if (target.moddedPartCountsAs(opponent, DemonicMod.INSTANCE)) {
                message = String.format(
                                "Almost instinctively, %s %s entire being into %s %s. While this would normally be a good thing,"
                                                + " whilst fucking a succubus it is very, very bad indeed.",
                                self.subjectAction("focus", "focuses"), self.possessiveAdjective(),
                                self.possessiveAdjective(), part.describe(self));
                c.write(self, message);
                // Actual bad effects are dealt with in PussyPart
            } else {
                message = String.format(
                                "Drawing upon %s extensive training, %s %s will into %s %s, enhancing %s own abilities",
                                self.possessiveAdjective(), self.subjectAction("concentrate", "concentrates"),
                                self.possessiveAdjective(), self.possessiveAdjective(), part.describe(self),
                                self.possessiveAdjective());
                c.write(self, message);
                for (int i = 0; i < Math.max(2, (self.get(Attribute.Ki) + 5) / 10); i++) { // +5
                                                                                           // for
                                                                                           // rounding:
                                                                                           // 24->29->20,
                                                                                           // 25->30->30
                    Attribute attr = new Attribute[] {Attribute.Power, Attribute.Cunning, Attribute.Seduction}[Global
                                    .random(3)];
                    self.add(c, new Abuff(self, attr, 1, 10));
                }
                self.buildMojo(c, 5);
                self.restoreWillpower(c, 1);
            }
        }
        return bonus;
    }

    public Optional<String> getFluids() {
        if (this.equals(bionic)) {
            return Optional.of("artificial lubricant");
        }
        return Optional.empty();
    }

    public double applyReceiveBonuses(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, double damage) {
        return 0;
    }

    @Override
    public void onOrgasmWith(Combat c, Character self, Character opponent, BodyPart part, BodyPart target, boolean selfCame) {
    }

    @Override
    public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan, BodyPart part) {
    }

    @Override
    public void onStartPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
    }

    public void onEndPenetration(Combat c, Character self, Character opponent, BodyPart part, BodyPart target) {
    }

    public static Optional<CockMod> getFromType(String type) {
        return ALL_MODS.stream().filter(mod -> mod.getModType().equals(type)).findAny();
    }

    @Override
    public String describeAdjective(String partType) {
        if (this.equals(bionic)) {
            return "bionic implants";
        } else if (this.equals(enlightened)) {
            return "imposing presence";
        }
        return "weirdness (ERROR)";
    }
}