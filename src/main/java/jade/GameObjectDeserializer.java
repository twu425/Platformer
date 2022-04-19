package jade;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Create new JsonObject
        JsonObject jsonObject = json.getAsJsonObject();
        // Gets
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        // Zindexing and transformations are no longer serialized as part of the gameobject
        //Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
        //int zIndex = context.deserialize(jsonObject.get("zIndex"), int.class);

        GameObject go = new GameObject(name);
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        // Set the transformation last
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}
