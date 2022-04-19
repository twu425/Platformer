package animation;


import components.Sprite;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

/** This class is used to store the information of a certain state, used in the class StateMachine.
 * Each state has two key elements: a name (title) and a list of frames (animationFrames). */
public class AnimationState {

    // Key elements
    private String title;
    private List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    // Tracks the time we've been on a single frame
    // Transient since this information is not worthy of saving
    private transient float timeTracker = 0.0f;
    private transient int currentSprite = 0;
    private boolean doesLoop = false;

    // ################# Getters and Setters #######################
    /** Append a frame to the list animationFrames */
    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }

    public List<Frame> getFrames() {
        return animationFrames;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    public boolean getDoesLoop() {
        return doesLoop;
    }
    // ############################################

    /** Reset the texture ID reference */
    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }

    /** Change the current sprite based on time */
    public void update(float dt) {
        // Avoid index out of bounds errors if the entity is trying to change to a sprite that doesn't exist
        if (currentSprite < animationFrames.size()) {
            // Update the time elapsed
            timeTracker -= dt;
            if (timeTracker <= 0) {
                // As long as we aren't on the last frame, or we are looping
                // Or in other words, when we want to move to the next frame
                if (currentSprite != animationFrames.size() - 1 || doesLoop) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                timeTracker = animationFrames.get(currentSprite).frameTime;
            }
        }
    }


    public Sprite getCurrentSprite() {
        if (currentSprite <animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }
        return defaultSprite;
    }
}
