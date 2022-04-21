package editor;

import components.NonPickable;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import org.lwjgl.system.CallbackI;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    // Active gameobject is just the object the properties window is inspecting (aka the object selected)
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;
    // Create a list of gameobjects since the user can select multiple of them
    private List<GameObject> activeGameObjects;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
    }

    public void imgui() {
        // Only show a property window if only one gameobject is selected and it is not null
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {

            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (activeGameObject.getComponent(RigidBody2D.class) == null) {
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null && activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    /** Returns the active gameobject only if only one is active. Else, return null.*/
    public GameObject getActiveGameObject() {
        if (this.activeGameObjects.size() == 1) {
            return this.activeGameObjects.get(0);
        } else {
            return null;
        }
    }

    /** Returns the list of activeGameObjects */
    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    /** Clear the list activeGameObjects (aka deselect all) */
    public void clearSelected() {
        this.activeGameObjects.clear();
    }

    /** If the gameobject sent is valid, clear the list activeGameObjects,
     *  then set the input gameobject to active. (AKA deselect all, then select input GameObject) */
    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }

    /** Add gameobject to activeGameobjects (add it to the selection0 */
    public void addActiveGameObject(GameObject go) {
        this.activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}