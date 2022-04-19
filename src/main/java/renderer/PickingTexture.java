package renderer;


// This class uses shaders to get the gamobject that is clicked on in the leveleditor scene

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class PickingTexture {
    private int pickingTextureId;
    private int fbo;
    private int depthTexture;

    // The width and height should just be the size of the window / framebuffer
    public PickingTexture(int width, int height) {
        // Check to see if the framebuffer was initialized
        if (!init(width, height)) {
            assert false: "Error: Initialize picking texture";
        }
    }

    public boolean init(int width, int height) {
        // Generate framebuffer
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // Generate a texture id
        pickingTextureId = glGenTextures();

        // Bind the texture id to a texture2d
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);

        // If the texture wraps (the dimensions are disproportionate), repeat it to fit
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // If the texture is shrinked or enlarged, scale it in a pixelated fashion
        // This is important, since it grants the most accurate picking data
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // Upload this texture to the GPU
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                this.pickingTextureId, 0);

        // Create the texture object for the depth buffer
        // Stores the z-index of each pixel
        glEnable(GL_TEXTURE_2D);
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);

        // No renderbuffer since this shouldn't be rendered
        // Disable the reading. Technically this isn't necessary but explicitly saying don't render it will prevent older GPU's from doing it
        // Don't read anything from this framebuffer
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        // Check to see if the framebuffer completed successfully
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Framebuffer is not complete";
            return false;
        }

        // Unbind
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Return true, meaning that the initialization was successful
        return true;

    }

    public void enableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    public void disableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int readPixel(int x, int y) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float pixels[] = new float[3];
        glReadPixels(x,y,1,1, GL_RGB, GL_FLOAT, pixels);

        return (int)(pixels[0])-1;
    }

}
