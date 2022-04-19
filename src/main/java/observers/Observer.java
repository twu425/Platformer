package observers;

import jade.GameObject;
import observers.events.Event;

// This class is to be implemented in other classes where something needs to be notified

public interface Observer {
    void onNotify(GameObject object, Event event);
}
