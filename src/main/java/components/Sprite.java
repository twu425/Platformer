package components;

import renderer.Texture;
import org.joml.Vector2f;

// A sprite is pretty much just a texture on a plane
public class Sprite {

    private float width, height;
    private Texture texture = null;

    private Vector2f[] texCoords = {
                    // Default texture coordinates
                    new Vector2f(1,1),
                    new Vector2f(1,0),
                    new Vector2f(0,0),
                    new Vector2f(0,1)
        };

    // Old Constructors, not used because gson can't read them

    //public Sprite(Texture texture) {
    //    this.texture = texture;
    //    Vector2f[] texCoords = {
    //            new Vector2f(1,1),
    //            new Vector2f(1,0),
    //            new Vector2f(0,0),
    //            new Vector2f(0,1)
    //    };
    //    this.texCoords = texCoords;
    //}

    //public Sprite(Texture texture, Vector2f[] texCoords) {
    //    this.texture = texture;
    //    this.texCoords = texCoords;
    //}

    // Getter and Setters for Texture and Texture Coordinates

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    // Getters and Setters for sprite height and width

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    // Getter for texture Id from this sprite
    public int getTexId() {
        // If there is no texture return -1
        if (texture == null){
            return -1;
        }
        else {
            return texture.getId();
        }
    }
}
