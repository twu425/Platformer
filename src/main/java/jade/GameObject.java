package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import imgui.ImGui;
import org.lwjgl.system.CallbackI;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;

    public String name;
    private List<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;

        // Generates a new id that's the last id number +1
        this.uid = ID_COUNTER++;
    }


    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())){
                try {
                    return componentClass.cast(c);
                }
                catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting Components";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i=0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void imgui() {
        // Add each component into the imgui window and give it the name
        for (Component c: components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
            c.imgui();
        }
    }

    public void addComponent(Component c) {
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for(int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    // Editor update to make things work differently in the level editor scene
    public void editorUpdate(float dt) {
        for(int i = 0; i < components.size(); i++) {
            components.get(i).editorUpdate(dt);
        }
    }

    public void start() {
        for(int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    /** Destroy This GameObject **/
    public void destroy() {
        this.isDead = true;
        // Doesn't actually destroy the component, just here
        // So that in case some special component stuff needs to happen that code can go in the destroy method
        for (int i=0; i < components.size(); i++) {
            components.get(i).destroy();
        }
    }

    public GameObject copy() {
        // Serialize
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);

        obj.generateUid();
        for (Component c : obj.getAllComponents()) {
            c.generateId();
        }

        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
        }
        return obj;
    }

    public boolean isDead() {
        return this.isDead;
    }

    // ZIndex is no longer stored as part of the gameobject but instead the transformation component
    //public int getzIndex() {return this.zIndex;}

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }


    public int getUid() {
        return this.uid;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    /** Prevents gameobjects from being serialized */
    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean doSerialization() {
        return this.doSerialization;
    }

    public void generateUid() {
        this.uid = ID_COUNTER++;
    }
}
