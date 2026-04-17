import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Event class that is made to make calendar events.
 */
public class Event implements Comparable<Event> {
    private final String title;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public Event(String title, LocalDateTime start, LocalDateTime end) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title required");
        if (start == null || end == null)
            throw new IllegalArgumentException("Start and end required");
        if (!end.isAfter(start))
            throw new IllegalArgumentException("End must be after start");
        if (!start.toLocalDate().equals(end.toLocalDate()))
            throw new IllegalArgumentException("Event must be within a single day");

        this.title = title;
        this.start = start;
        this.end = end;
    }

    public String getTitle() { return title; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public boolean overlaps(Event other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    public boolean overlaps(LocalDateTime s, LocalDateTime e) {
        return start.isBefore(e) && s.isBefore(end);
    }

    @Override
    public int compareTo(Event other) {
        return this.start.compareTo(other.start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event e)) return false;
        return title.equals(e.title) && start.equals(e.start) && end.equals(e.end);
    }

    @Override
    public int hashCode() { return Objects.hash(title, start, end); }

    @Override
    public String toString() {
        return (title + ": " + start + " - " + end);
    }
}
