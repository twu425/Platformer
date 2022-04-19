package observers.events;

public class Event {
    public EventType type;

    /** Default to UserEvent type */
    public Event() {
        this.type = EventType.UserEvent;
    }

    /** Set the event type **/
    public Event(EventType type) {
        this.type = type;
    }


}
