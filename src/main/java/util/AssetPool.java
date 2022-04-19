package util;

import jade.Sound;
import renderer.Shader;
import renderer.Texture;
import components.Spritesheet;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// The aim of this class is to avoid lag spikes from Java attempting to clear memory
// It does this by making it so that texture and shaders objects will have their pointers
// Duplicated instead of creating a new one texture / shader

public class AssetPool {

    // Private since we don't want to access the hashmaps directly
    // The hashmaps contain data of the filepath (the string), and the resource it corresponds to
    // the key is the filepath. When a resource is requested, it is not actually obtained from the file
    // but from the assetpool
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();

    /** Gets a shader from the assetpool using the filepath. */
    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        // If the there is already a shader with the same file path
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            // Return that shader
            return AssetPool.shaders.get(file.getAbsolutePath());
        }
        // Else the shader does not already exist in the hashmap
        else {
            // Create a new shader and add it to the hashmap
            Shader shader = new Shader(resourceName);
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    /** Gets a Texture from the assetpool using the filepath */
    public static Texture getTexture(String resourceName) {
        // This works the same way as the shaders
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    /** Adds a Spritesheet using the filepath and the spritesheet object */
    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        // If the assetpool does not already contain the spritesheet
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            // Add it to the assetpool
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    /** Gets a Spritesheet using the filepath and the spritesheet object */
    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File(resourceName);
        // If the spritesheet is not already in the assetpool
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            // Throw an error
            assert false: "Error: Tried to access spritesheet " + resourceName + " and it has not been added to asset pool.";
        }
        // Returns the spritesheet from the assetpool. If it can't find the key, return null (which'll probably never happen since in that case it'll throw and error before it return null).
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }

    /** For Imgui */
    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }

    public static Sound getSound(String resourceName) {
        File file = new File(resourceName);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Sound file not added " + resourceName;
        }
        return null;
    }

    public static Sound addSound(String resourceName, boolean loops) {
        File file = new File(resourceName);
        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            AssetPool.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }
}