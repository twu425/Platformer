package components;

import fontrenderer.FontBatch;
import fontrenderer.Fonts.CFont;
import jade.Window;
import renderer.Shader;
import scenes.Scene;
import util.AssetPool;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer extends Component {

    private CFont font;
    FontBatch fontBatch = new FontBatch();
    Shader fontShader;

    @Override
    public void start() {
        System.out.println("FONTRENDERER");
        font = new CFont("C:/Windows/Fonts/Arial.ttf", 64);
        fontShader = AssetPool.getShader("Assets/shaders/fontShader.glsl");
        fontShader.compile();
        fontBatch.shader = fontShader;
        fontBatch.font = font;
        fontBatch.initBatch();
    }



    @Override
    public void editorUpdate(float dt) {
        //glEnable(GL_BLEND);


        //fontBatch.addText("Hello world!", 200, 200, 50f, 0xFF00AB0);
        fontBatch.addText("Hello World!", 100, 300, 1.1f, 0xAA01BB);
        fontBatch.flushBatch();

    }
    @Override
    public void update(float dt) {
        //fontBatch.addText("Hello world!", 200, 200, 50f, 0xFF00AB0);
        //fontBatch.addText("My name is Gabe!", 100, 300, 1.1f, 0xAA01BB);
        //fontBatch.flushBatch();
    }






}
