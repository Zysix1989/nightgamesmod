package nightgames.characters.body.mods;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.global.Global;

public class SizeMod extends PartMod {

    //FIXME: Why are cocks not treated the same way as boobs?! - DSM
    private static int COCK_SIZE_TINY = 3;
    private static int COCK_SIZE_MAMMOTH = 12;

    private static int ASS_SIZE_HUGE = 5;

    private static final Map<Integer, String> BREAST_SIZE_DESCRIPTIONS = new HashMap<>();
    private static final Map<Integer, String> BREAST_SIZE_CUPS = new HashMap<>();

    static {
        BREAST_SIZE_CUPS.put(0, "");
        BREAST_SIZE_CUPS.put(1, "A Cup");
        BREAST_SIZE_CUPS.put(2, "B Cup");
        BREAST_SIZE_CUPS.put(3, "C Cup");
        BREAST_SIZE_CUPS.put(4, "D Cup");
        BREAST_SIZE_CUPS.put(5, "DD Cup");
        BREAST_SIZE_CUPS.put(6, "E Cup");
        BREAST_SIZE_CUPS.put(7, "F Cup");
        BREAST_SIZE_CUPS.put(8, "G Cup");
        BREAST_SIZE_CUPS.put(9, "H Cup");
        BREAST_SIZE_CUPS.put(10, "I Cup");
        BREAST_SIZE_CUPS.put(11, "J Cup");
        BREAST_SIZE_DESCRIPTIONS.put(0, "flat");
        BREAST_SIZE_DESCRIPTIONS.put(1, "tiny");
        BREAST_SIZE_DESCRIPTIONS.put(2, "smallish");
        BREAST_SIZE_DESCRIPTIONS.put(3, "modest");
        BREAST_SIZE_DESCRIPTIONS.put(4, "round");
        BREAST_SIZE_DESCRIPTIONS.put(5, "large");
        BREAST_SIZE_DESCRIPTIONS.put(6, "huge");
        BREAST_SIZE_DESCRIPTIONS.put(7, "glorious");
        BREAST_SIZE_DESCRIPTIONS.put(8, "massive");
        BREAST_SIZE_DESCRIPTIONS.put(9, "colossal");
        BREAST_SIZE_DESCRIPTIONS.put(10, "mammoth");
        BREAST_SIZE_DESCRIPTIONS.put(11, "godly");
    }

    @Override
    public void loadData(JsonElement element) {
        this.size = element.getAsInt();
        this.modType = "size:"+this.size;
    }

    public JsonElement saveData() {
        return new JsonPrimitive(size);
    }

    private int size;

    public static SizeMod fromJSON(JsonElement e) {
        var s = new SizeMod();
        s.loadData(e);
        return s;
    }

    private SizeMod() {
        this(0);
    }

    public SizeMod(JsonElement js) {
        this();
        size = js.getAsInt();
    }

    public SizeMod(int size) {
        super("size:" + size, 0, 0, 0, -100000);
        this.size = size;
    }

    public String adjective(GenericBodyPart part) {
        if (part.getType().equals("breasts")) {
            if (Global.random(2) == 0) {
                return BREAST_SIZE_DESCRIPTIONS.get(size);
            } else {
                return BREAST_SIZE_CUPS.get(size);
            }
        }
        return "";
    }

    public static int clampToValidSize(BodyPart part, int size) {
        return Global.clamp(size, getMinimumSize(part.getType()), getMaximumSize(part.getType()));
    }

    @Override
    public String getVariant() {
        return "sizemod";
    }

    public Optional<String> getDescriptionOverride(Character self, BodyPart part) {
        if (part.isType("breasts")) {
            if ((size > 0 || self.useFemalePronouns())) {
                return Optional.empty();
            } else {
                if (self.get(Attribute.Power) > self.getLevel() + 5) {
                    return Optional.of("muscular pecs");
                }
                return Optional.of("flat chest");
            }
        }
        return Optional.empty();
    }

    public int getSize() {
        return size;
    }

    @Override
    public String describeAdjective(String partType) {
        return "some of its volume";
    }

    public static int getMaximumSize(String type) {
        switch (type) {
            case "breasts":
                return 11;
            case "cock":
                return COCK_SIZE_MAMMOTH;
            case "ass":
                return ASS_SIZE_HUGE;
        }
        return 0;
    }

    public static int getMinimumSize(String type) {
        if (type.equals("cock")) {
            return COCK_SIZE_TINY;
        }
        return 0;
    }
}