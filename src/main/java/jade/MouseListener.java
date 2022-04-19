package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX, worldX, worldY, lastWorldX, lastWorldY;

    // An array that contains (hopefully) all of the mousebuttons status as booleans
    private boolean mouseButtonPressed[] = new boolean[9];

    private boolean isDragging;
    // When this is = 0, the mousebutton is not pressed, when it is > 0, a mouse button is being pressed
    private int mouseButtonDown = 0;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    /** Initializes the mouselistener by assigning mouse variables to 0 */
    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    /** Gets the current Mouselistener instance. If there isn't one, create it*/
    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    /** Updates the mouselistener with the mouse cursor information */
    public static void mousePosCallback(long window, double xpos, double ypos) {
        // If the mouse has been held down, set dragging to true
        if (get().mouseButtonDown > 0) {
            get().isDragging = true;
        }
        // Keep track of the last place the cursor was on the window
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        // Keep track of the last place the cursor was on the game world
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        // Get the new cursor location relative to the window
        get().xPos = xpos;
        get().yPos = ypos;
    }

    /** Updates the mouselistener with the mouse button information */
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {

            get().mouseButtonDown++;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {

            get().mouseButtonDown--;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                // If the mouse button is released, set dragging to false
                get().isDragging = false;
            }
        }
    }

    /** Updates the mouselistener with the mouse scroll information */
    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    // Reset all cursor variables
    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
    }

    /** Input the gameviewportpos so the mouselistener can gather mouse data relative to it */
    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    /** Input the gameviewporsize so the mouselistener can gather mouse data relative to it */
    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }

    // Gets the amount of pixels the cursor has moved in the x direction relative to the game world */
    //public static float getWorldDx() {return (float)(get().lastWorldX - get().worldX);}

    // Gets the amount of pixels the cursor has moved in the y direction relative to the game world */
    //public static float getWorldDy() {return (float)(get().lastWorldY - get().worldY);}

    /** Gets the amount of pixels the cursor has scrolled in the x direction relative to the window */
    public static float getScrollX() {
        return (float)get().scrollX;
    }

    /** Gets the amount of pixels the cursor has scrolled in the y direction relative to the window */
    public static float getScrollY() {
        return (float)get().scrollY;
    }

    /** Gets the dragging status */
    public static boolean isDragging() {
        return get().isDragging;
    }

    /** Gets the status of the mouse button. True if pressed, false if not */
    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    public static float getWorldDx() {
        return (float)(get().lastWorldX - get().worldX);
    }

    public static float getWorldDy() {
        return (float)(get().lastWorldY - get().worldY);
    }


    /** Gets the mouse X coordinates relative to the actual dimensions (size of the window itself)*/
    public static float getX() {
        return (float)get().xPos;
    }

    /** Gets the mouse Y coordinates relative to the actual dimensions (size of window itself)*/
    public static float getY() {
        return (float)get().yPos;
    }

    ///////////////////////////////////////////////////////////////
    /** Get the mouse Y coordinates in pixels */
    public static float getScreenX() {
        return getScreen().x;
    }
    /** Get the mouse X coordinates in pixels */
    public static float getScreenY() {
        return getScreen().y;
    }

    public static Vector2f getScreen() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 3840.0f;
        float currentY = (getY() - get().gameViewportPos.y);
        currentY = (1.0f - (currentY / get().gameViewportSize.y)) * 2160.0f;
        return new Vector2f(currentX, currentY);
    }
    ///////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////
    /** Calculates the new mouse x position in the viewport in normalized device coordinates. */
    public static float getWorldX() {
        return getWorld().x;
    }

    /** Calculates the new mouse y position in the viewport in normalized device coordinates. */
    public static float getWorldY() {
        return getWorld().y;
    }

    /** Calculates the new mouse x and y positions in the viewport in normalized device coordinates. */
    public static Vector2f getWorld() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (2.0f * (currentX / get().gameViewportSize.x)) - 1.0f;
        float currentY = (getY() - get().gameViewportPos.y);
        currentY = (2.0f * (1.0f - (currentY / get().gameViewportSize.y))) - 1.0f;
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Camera camera = Window.getScene().camera();

        // Create new, copied variables for view and projection to avoid accidental modifications
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());

        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }
    ///////////////////////////////////////////////////////////////
}