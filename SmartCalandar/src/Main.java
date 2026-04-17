import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
public class Main {
    public static void main(String[] args) {
        Calendar cal = new Calendar();
        LocalDate today = LocalDate.now();
        cal.addEvent(new Event("Class",
                LocalDateTime.of(today, LocalTime.of(10, 0)),
                LocalDateTime.of(today, LocalTime.of(11, 30))));
        cal.addEvent(new Event("Tennis",
                LocalDateTime.of(today, LocalTime.of(12, 0)),
                LocalDateTime.of(today, LocalTime.of(13, 0))));

        Event study = cal.addStudySession(today, Duration.ofMinutes(90));
        System.out.println(study); // 8:00-9:30
    }
    //Last commit forgot to add the right comment changed it so it can generate the automic study time now
}