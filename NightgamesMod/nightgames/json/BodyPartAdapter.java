package nightgames.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import nightgames.characters.body.AssPart;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.EarPart;
import nightgames.characters.body.FacePart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.characters.body.MouthPart;
import nightgames.characters.body.PussyPart;
import nightgames.characters.body.StraponPart;
import nightgames.characters.body.TailPart;
import nightgames.characters.body.TentaclePart;
import nightgames.characters.body.WingsPart;

public class BodyPartAdapter implements JsonSerializer<BodyPart>, JsonDeserializer<BodyPart> {
    private interface BodyPartLoader {
        BodyPart newFromJSON(JsonObject js);
    }

    static private Map<String, BodyPartLoader> prototypes;
    static {
        prototypes = new HashMap<>();
        prototypes.put(PussyPart.class.getCanonicalName(), PussyPart::new);
        prototypes.put(BreastsPart.class.getCanonicalName(), BreastsPart::new);
        prototypes.put(WingsPart.class.getCanonicalName(), WingsPart.demonic::load);
        prototypes.put(TailPart.class.getCanonicalName(), TailPart.cat::load);
        prototypes.put(EarPart.class.getCanonicalName(), EarPart.normal::load);
        prototypes.put(StraponPart.class.getCanonicalName(), StraponPart.generic::load);
        prototypes.put(TentaclePart.class.getCanonicalName(), TentaclePart::new);
        prototypes.put(AssPart.class.getCanonicalName(), AssPart::new);
        prototypes.put(MouthPart.class.getCanonicalName(), MouthPart::new);
        prototypes.put(CockPart.class.getCanonicalName(), CockPart::new);
        prototypes.put(GenericBodyPart.class.getCanonicalName(), js -> new GenericBodyPart("", 0, 1, 1, "none", "none").load(js));
        prototypes.put(FacePart.class.getCanonicalName(), FacePart::new);
    }

    @Override
    public BodyPart deserialize(
        JsonElement jsonElement,
        Type type,
        JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        String classType = obj.get("class").getAsString();
        return prototypes.get(classType).newFromJSON(obj);
    }

    @Override
    public JsonElement serialize(
        BodyPart part,
        Type type,
        JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = part.save();
        obj.addProperty("class", part.getClass().getCanonicalName());
        return obj;
    }
}
