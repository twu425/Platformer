package jade;

import org.joml.*;

public class Camera {

    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f position;
    private Vector2f projectionSize = new Vector2f(6.0f, 3.0f); // 6 meters by 3 meters
    private float zoom = 1.0f;

    // Method to update the camera's position
    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    // Sets the screen action box to 1280 x 672 // Actually 1920 * 1080
    public void adjustProjection() {
        projectionMatrix.identity();
        // Creates a game world of 1280 x 672 // Actually 1920 * 1080
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f,100.0f);
        //projectionMatrix.perspective(75, 1, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjection);
    }

    // Returns the viewmatrix
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f,0.0f,-1.0f);
        Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f), cameraFront.add(position.x,position.y, 0.0f), cameraUp);
        this.viewMatrix.invert(inverseView);
        return this.viewMatrix;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    public Matrix4f getInverseView() {
        return inverseView;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void addZoom(float value) {
        this.zoom += value;
    }
}

