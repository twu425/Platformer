package components;

import editor.PropertiesWindow;
import jade.MouseListener;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        // Move game object
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();

            }
            else if (yAxisActive && !xAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
                System.out.println(activeGameObject.transform.position.y);
                System.out.println(MouseListener.getWorldY());
            }
        }
        super.editorUpdate(dt);
    }
}
