package jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static KeyListener instance;

    // Creates an array of 350 booleans for the 350 keys. (I'm not actually sure how many keys a keyboard has)
    private boolean keyPressed[] = new boolean[350];
    private boolean keyBeginPressed[] = new boolean[350];

    private KeyListener() {

    }

    // Getter method for the KeyListener instance (object). If there isn't one, it creates a new one and returns it.
    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    // Sets the keys as true or false depending on if they are pressed or not
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
            get().keyBeginPressed[key] = true;
        }
        else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
            get().keyBeginPressed[key] = false;
        }

    }

    // Method that finds whether a key is pressed
    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }

    // Method that finds whether a key is pressed, but only when it is first pressed and not the entire time it is held
    public static boolean keyBeginPress(int keyCode) {
        boolean result = get().keyBeginPressed[keyCode];
        if (result) {
            get().keyBeginPressed[keyCode] = false;
        }
        return result;
    }
}
