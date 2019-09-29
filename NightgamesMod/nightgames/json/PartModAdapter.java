package nightgames.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import nightgames.characters.body.mods.PartMod;

public class PartModAdapter implements JsonSerializer<PartMod>, JsonDeserializer<PartMod> {
    @Override public PartMod deserialize(JsonElement jsonElement, Type type,
                    JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            String modClass = jsonElement.getAsJsonObject().get("_type").getAsString();
            PartMod mod;
            mod = (PartMod) Class.forName(modClass).newInstance();
            return mod;
        } catch (ClassNotFoundException | IllegalAccessException | ClassCastException | InstantiationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("could not deserialize %s", jsonElement.toString()));
    }

    @Override
    public JsonElement serialize(PartMod mod, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("_type", mod.getClass().getCanonicalName());
        return object;
    }
}