import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * basic calendar class where you can add and remove events
 */
public class Calendar {
    private final List<Event> events = new ArrayList<>();
    private StudyWindow studyWindow = new StudyWindow();

    public Calendar() {}

    public Calendar(StudyWindow studyWindow) {
        this.studyWindow = studyWindow;
    }

    public StudyWindow getStudyWindow() { return studyWindow; }

    public void setStudyWindow(StudyWindow studyWindow) {
        if (studyWindow == null) throw new IllegalArgumentException("Window required");
        this.studyWindow = studyWindow;
    }

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
