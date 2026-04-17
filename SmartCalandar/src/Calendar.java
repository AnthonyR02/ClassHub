import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Calendar {
    private final List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        for (Event existing : events) {
            if (existing.overlaps(event)) {
                throw new IllegalStateException(
                        "Conflict with existing event: " + existing);
            }
        }
        events.add(event);
        Collections.sort(events);
    }

    public boolean removeEvent(Event event) {
        return events.remove(event);
    }

    public List<Event> getEventsOn(LocalDate date) {
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            if (e.getStart().toLocalDate().equals(date)) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }
}
