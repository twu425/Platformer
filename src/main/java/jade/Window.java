package jade;

import fontrenderer.FontBatch;
import fontrenderer.Fonts.CFont;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.*;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

//
public class Window implements Observer {

    // Initialize window variables
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    // Background colors
    public float r, g, b, a;

    // Static as we only need one instance of a window
    private static Window window = null;

    // Set up audio elements
    private long audioContext;
    private long audioDevice;

    private static Scene currentScene;

    private boolean runTimePlay = false;

    // Window parameters
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Platformer";
        EventSystem.addObserver(this);
        // Set background colors
        r = 0.9f;
        b = 0.9f;
        g = 0.9f;
        a = 1;
    }

    /** Changes the scene initializer. */
    public static void changeScene(SceneInitializer sceneInitializer) {
        // Destroy the old scene
        if (currentScene != null) {
            currentScene.destroy();
        }
        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    /** Returns the current window instance. Creates one if there is none. */
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Initialize the game
        init();
        // Start the core loop
        loop();

        // Terminate the audio context (Technically not necessary because the OS should automatically clean this up)
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // These commands shut down the program once the game has stopped looping (technically windows does this already)
        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback (prints any errors to the system)
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        // Sets certain window properties (resizable, etc.) to their default values
        glfwDefaultWindowHints();
        // Makes the window invisible until it has loaded
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        // Makes the window resizable
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        // Makes the window maximized upon launch
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        // glfwWindow is really just a long number which is the memory address to the window
        // The last two parameters are about sharing and monitor
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        // Set Input Callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible once it has loaded
        glfwShowWindow(glfwWindow);

        // Setup/Init audio devices (Doesn't actually have to be here but since OpenAL is similar
        // to OpenGL we put this here
        //############################################################
        // Get the default speakers of the user
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        // Currently, audio attributes is set to 0 since we don't want any for now
        // TODO: See what audio attributes there are
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        // Make sure OpenAL works with the user's audio devices in case their speakers are made of stone
        if (!alcCapabilities.OpenALC10) {
            assert false: "Audio library not supported";
        }
        //############################################################


        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(3840, 2160); // size of screen
        this.pickingTexture = new PickingTexture(3840, 2160); // size of screen
        glViewport(0, 0, 3840, 2160);

        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imguiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInitializer());
    }

    // Core loop
    public void loop() {

        // Capture starting time
        float beginTime = (float)glfwGetTime();
        float endTime;
        // dt = delta time (time elapsed between frames)
        float dt = -1.0f;

        // Get the shaders from the assetpool
        Shader defaultShader = AssetPool.getShader("Assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("Assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {



            // Poll events (update keylisteners)
            glfwPollEvents();


            // Render pass 1 render picking texture
            glDisable(GL_BLEND);

            // For some reason the text blurs without this
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            pickingTexture.enableWriting();

            glViewport(0,0,3840,2160);

            // Set the window to white
            //glClearColor(0.0f,0.0f,0.0f,0.0f);
            // Fill the window with white
            //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


            // Bind custom shader
            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();

            // Render pass 2 render actual game
            DebugDraw.beginFrame();

            // Bind the framebuffer
            this.framebuffer.bind();
            glEnable(GL_BLEND);

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);


            // Don't update game until dt has been initialized
            if (dt >= 0) {
                DebugDraw.draw();
                // Switch back to the default shader
                Renderer.bindShader(defaultShader);
                if (runTimePlay) {
                    currentScene.update(dt);
                    currentScene.render();
                }
                else {

                    currentScene.editorUpdate(dt);
                    currentScene.render();

                }

            }



            // Unbind the framebuffer
            this.framebuffer.unbind();

            this.imguiLayer.update(dt, currentScene);

            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            // Update delta time
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        // Don't save on close
        //currentScene.save();
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static Framebuffer getFramebuffer() { return get().framebuffer; }

    // In the future get monitor size
    public static float getTargetAspectRatio() { return 16.0f  / 9.0f; }

    public static ImGuiLayer getImguiLayer() {
        return get().imguiLayer;
    }

    // Implement the on notifiy method from the Observer interface
    // Checks to see if the event type is GameEngine____Play and then plays/stops
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {
            case GameEngineStartPlay:
                //System.out.println("Playing");
                this.runTimePlay = true;
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                //System.out.println("Stopping");
                this.runTimePlay = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                //System.out.println("Loading");
                // Clear the scene by loading a new one
                Window.changeScene(new LevelEditorSceneInitializer());
            case SaveLevel:
                //System.out.println("Saving");
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
        }
    }
}

