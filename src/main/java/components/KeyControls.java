package components;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/** A class like MouseControls which is (currently) used to process key inputs */
public class KeyControls extends Component{

    @Override
    public void editorUpdate(float dt) {
        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        // Create a list of active (selected) game objects, so they can be all duplicated / deleted at once
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();

        // Duplicate Object (singular)
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)
                && KeyListener.keyBeginPress(GLFW_KEY_D)) {
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObj);
            // Offset it a little, so it doesn't go on top of the old gameobject
            newObj.transform.position.add(0.1f, 0.1f);
            propertiesWindow.setActiveGameObject(newObj);
        }
        // Duplicate Objects (All selected)
        else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL)
                && KeyListener.keyBeginPress(GLFW_KEY_D)
                && activeGameObjects.size() > 1) {
            // Create a copy of activeGameObjects
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
            }
        }
        // Delete all selected objects
        else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
    }
}
