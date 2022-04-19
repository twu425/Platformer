package observers;

import jade.GameObject;
import observers.events.Event;

import java.util.ArrayList;
import java.util.List;

// The purpose of this class is to notify other classes of events
// The list of event types is in EventTypes

public class EventSystem {
    private static List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public static void notify(GameObject obj, Event event) {
        for (Observer observer : observers) {
            observer.onNotify(obj, event);
        }
    }

}
