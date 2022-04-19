package editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.MouseListener;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

import static imgui.ImGui.getScrollX;

public class GameViewWindow {

    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;

    public void imgui() {
        // Set the flags for what the viewport should have
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.MenuBar);

        // Add the play / stop button
        ImGui.beginMenuBar();

        //  Create the menu item. Enable playing, but prevent the user from pressing the play button again while enabled
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        // Same thing but for stop playing
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }

        ImGui.endMenuBar();

        // The next thing (the framebuffer texture) is going to be drawn at this position
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        // Viewport size
        ImVec2 windowSize = getLargestSizeForViewport();
        // Viewport center point
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        // Get cursor pos in absolute coordinates
        //ImVec2 topLeft = new ImVec2();
        //ImGui.getCursorPos(topLeft);
        //ImGui.getCursorScreenPos(topLeft);
        // Counteract any scrolling
        //topLeft.x -= ImGui.getScrollX();
        //topLeft.y -= ImGui.getScrollY();
        leftX = windowPos.x+10;
        bottomY = windowPos.y+10;
        rightX = windowPos.x + windowSize.x +10;
        topY = windowPos.y + windowSize.y+10;

        // Get the framebuffer texture Id
        int textureId = Window.getFramebuffer().getTextureId();

        // Sample from the bottom left corner (0,1) to top right corner (1,0) of the framebuffer texture
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1 ,1 , 0);

        MouseListener.setGameViewportPos(new Vector2f(windowPos.x+10, windowPos.y+10));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }



    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // Cancel out any scrolling just in case
        //windowSize.x -= ImGui.getScrollX();
        //windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight  = aspectWidth / Window.getTargetAspectRatio();

        if (aspectHeight > windowSize.y) {
            // Switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    // Determines if the mouse is in the viewport. If it is, return true.
    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {

        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        // windowSize.x -= ImGui.getScrollX();
        // windowSize.y -= ImGui.getScrollY();

        // Calculate the center coordinates
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        // Remove space at bottom caused by the offset of the titlebar
        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }
}
