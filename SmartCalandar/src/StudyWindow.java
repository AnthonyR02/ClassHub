import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Made so you can generate an automatic study time with your current schedule
 * Has a default time of 8-10 for earliest possible and latest possible
 */
public class StudyWindow {
    private LocalTime dayStart;
    private LocalTime dayEnd;

    public StudyWindow(LocalTime dayStart, LocalTime dayEnd) {
        setWindow(dayStart, dayEnd);
    }

    public StudyWindow() {
        this(LocalTime.of(8, 0), LocalTime.of(22, 0));
    }

    public void setWindow(LocalTime dayStart, LocalTime dayEnd) {
        if (dayStart == null || dayEnd == null)
            throw new IllegalArgumentException("Start and end required");
        if (!dayEnd.isAfter(dayStart))
            throw new IllegalArgumentException("End must be after start");
        this.dayStart = dayStart;
        this.dayEnd = dayEnd;
    }

    public LocalTime getDayStart() { return dayStart; }
    public LocalTime getDayEnd() { return dayEnd; }

    public LocalDateTime startOn(LocalDate date) {
        return LocalDateTime.of(date, dayStart);
    }

    public LocalDateTime endOn(LocalDate date) {
        return LocalDateTime.of(date, dayEnd);
    }
}