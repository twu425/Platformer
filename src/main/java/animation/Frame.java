package animation;

import components.Sprite;

/** A frame only has two things: a sprite and a time for convenient storage. */
public class Frame {

    public Sprite sprite;
    public float frameTime;

    public Frame() {}

    public Frame(Sprite sprite, float time) {
        this.sprite = sprite;
        this.frameTime = time;
    }
}
