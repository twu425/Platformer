package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    // Don't serialize the texID because the texID can mean something
    // different when the game is restarted. Instead, for saving to json
    // the filepath is used since that won't change.
    private String filepath;
    private transient int texID;

    private int width, height;

    // This code purposefully doesn't work
    // Unless the init method is called to fix the texture
    // Since we can't save the texture if it uses a constructor
    public Texture() {
        texID = -1;
        width = -1;
        height = -1;
    }

    /** This method is for use with the Framebuffer only */
    public Texture(int width, int height) {
        // This texture doesn't have a filepath since it's being generated
        this.filepath = "Generated";

        // Generate a texture on GPU and bind it to the texId
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // What to do if the image texture don't match up with their actual size
        // In this case it'll resize them linearly (blur)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Asks the GPU to generate a texture without any data
        // In other words it creates a large 2d array and stores it to the texid
        // This empty texture will be used for the framebuffer
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
    }

    public void init(String filepath) {
        this.filepath = filepath;

        // Generate texture on GPU and bind it to the texId
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Set texture parameters
        // Repeat image in both directions (In other words, if the texture's uv coordinates are greater than it's actual width, wrap it)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, color);

        // When stretching or shrinking the image, stretch or compress linearly
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Creates int-buffers for the texture parameters as a Bytebuffer won't accept integers
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // Flip vertically because that's how c++ works
        stbi_set_flip_vertically_on_load(true);

        // Create the bytebuffer image
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        // If the image loaded successfully
        if (image != null) {

            // Gets the texture dimensions from the intbuffers
            this.width = width.get(0);
            this.height = height.get(0);

            // Simple check to see if the image has alpha (opacity) since there are different functions for RGB and RGBA textures
            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channels '" + channels.get(0) + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }
        // Frees the memory once the texture is created
        stbi_image_free(image);
    }

    // This checks to make sure that when comparing two textures
    // They are actually the same and don't merely have the same address
    // So a custom equals method is created to override the default one
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Texture)) return false;
        Texture oTex = (Texture)o;
        return oTex.getWidth() == this.width &&
                oTex.getHeight() == this.height &&
                oTex.getId() == this.texID &&
                oTex.getFilepath().equals(this.filepath);
    }

    public String getFilepath() {
        return this.filepath;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getId() {
        return texID;
    }
}