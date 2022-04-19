package renderer;

import jade.GameObject;
import components.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            //Checks if batch has room and if all sprites are on the same zIndex
            if (batch.hasRoom() && batch.zIndex() == sprite.gameObject.transform.zIndex) { // Gets the zindex directly from the transform class
                Texture tex = sprite.getTexture();
                if (tex!= null && batch.hasTexture(tex) || batch.hasTextureRoom() || tex == null) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }


        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.transform.zIndex);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            // Sort batches everytime a new batch is added
            Collections.sort(batches);
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render() {
        currentShader.use();
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }

    /** Removes the gameobject from the rendering system **/
    public void destroyGameObject(GameObject go) {
        // If the gameobject being destroyed doesn't actually have a spriterenderer don't do anything
        if (go.getComponent(SpriteRenderer.class) == null) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
    }
}
