package components;

import renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {

    private Texture texture;
    private List<Sprite> sprites;

    // Creates a spritesheet with the texture and parameters of each individual sprite
    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {

        this.sprites = new ArrayList<>();
        this.texture = texture;

        // Initializes to a the left bottom (I assume) of the first sprite in the sprite sheet
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;

        // Loop through for each sprite in the spritesheet
        for (int i=0; i < numSprites; i++) {

            // Calculates the coordinates of the sprite relative to the spritesheet
            float topY = (currentY + spriteHeight) / (float)texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float)texture.getWidth();
            float leftX = currentX / (float)texture.getWidth();
            float bottomY = currentY / (float)texture.getHeight();

            // Creates new texture coordinates
            Vector2f[] texCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };

            // Creates a new sprite
            Sprite sprite = new Sprite();

            sprite.setTexture(this.texture);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            this.sprites.add(sprite);

            // Moves to the next sprite
            currentX += spriteWidth + spacing;
            // If it's the last sprite in the row, move down to the next one
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    // Returns the sprite at the given index
    public Sprite getSprite(int index) {
        return this.sprites.get(index);
    }

    // Returns the current sprite size
    public int size() {
        return sprites.size();
    }
}