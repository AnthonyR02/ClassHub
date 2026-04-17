import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * calendar class where you can add and remove events
 * Generates an automatic study time that fits into your schedule
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

    public Event addStudySession(LocalDate date, Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative())
            throw new IllegalArgumentException("Duration must be positive");

        LocalDateTime windowStart = studyWindow.startOn(date);
        LocalDateTime windowEnd = studyWindow.endOn(date);
        LocalDateTime currentPos = windowStart;

        for (Event e : getEventsOn(date)) {
            LocalDateTime gapEnd;
            if (e.getStart().isBefore(currentPos)) {
                gapEnd = currentPos;
            } else {
                gapEnd = e.getStart();
            }

            if (Duration.between(currentPos, gapEnd).compareTo(duration) >= 0) {
                if (!gapEnd.isAfter(windowEnd)) {
                    return scheduleStudy(currentPos, duration);
                }
            }

            if (e.getEnd().isAfter(currentPos)) {
                currentPos = e.getEnd();
            }
        }

        if (Duration.between(currentPos, windowEnd).compareTo(duration) >= 0) {
            return scheduleStudy(currentPos, duration);
        }

        return null;
    }

    private Event scheduleStudy(LocalDateTime start, Duration duration) {
        Event study = new Event("Study Session", start, start.plus(duration));
        addEvent(study);
        return study;
    }
}
