package com.classhub.views;

import com.classhub.models.*;
import com.classhub.utils.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DashboardView extends VBox {

    public DashboardView() {
        setSpacing(20);
        setBackground(StyleHelper.bg(StyleHelper.BG));
        build();
    }

    private void build() {
        DataStore ds = DataStore.getInstance();

        long pending = ds.getAssignments().stream().filter(a -> !a.isCompleted()).count();
        double avgGrade = ds.getGrades().stream()
                .mapToDouble(Grade::getPercentage).average().orElse(0);
        double avgAtt = ds.getAttendance().stream()
                .mapToDouble(Attendance::getRate).average().orElse(0);

        // ── Stat cards ──────────────────────────────────────
        HBox stats = new HBox(14);
        VBox c1 = statCard("GPA",           "3.8",                              StyleHelper.ACCENT);
        VBox c2 = statCard("Pending Tasks", String.valueOf(pending),            StyleHelper.ROSE);
        VBox c3 = statCard("Avg Grade",     String.format("%.0f%%", avgGrade),  StyleHelper.MINT);
        VBox c4 = statCard("Attendance",    String.format("%.0f%%", avgAtt),    StyleHelper.AMBER);
        for (VBox c : new VBox[]{c1, c2, c3, c4}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }
        stats.getChildren().addAll(c1, c2, c3, c4);

        // ── Two-column middle row ─────────────────────────────
        HBox cols = new HBox(16);

        // Today's classes
        VBox todayCard = StyleHelper.card();
        HBox.setHgrow(todayCard, Priority.ALWAYS);
        todayCard.getChildren().add(StyleHelper.labelBold("Today's Schedule", 15, StyleHelper.TEXT));

        String[] todaySubjects = {"Mathematics — Room 204", "English Lit — Room 110", "Chemistry — Lab 3"};
        String[] todayTimes    = {"8:00 – 9:00", "9:15 – 10:15", "10:30 – 11:30"};
        String[] todayColors   = {StyleHelper.ACCENT, StyleHelper.MINT, StyleHelper.ROSE};
        for (int i = 0; i < todaySubjects.length; i++) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 8));
            row.setPadding(new Insets(10, 14, 10, 14));
            Label dot = StyleHelper.label("●", 10, todayColors[i]);
            VBox info = new VBox(2);
            info.getChildren().addAll(
                StyleHelper.labelBold(todaySubjects[i], 13, StyleHelper.TEXT),
                StyleHelper.label(todayTimes[i], 11, StyleHelper.TEXT3)
            );
            HBox.setHgrow(info, Priority.ALWAYS);
            row.getChildren().addAll(dot, info);
            todayCard.getChildren().add(row);
        }

        // Upcoming assignments
        VBox assignCard = StyleHelper.card();
        HBox.setHgrow(assignCard, Priority.ALWAYS);
        assignCard.getChildren().add(StyleHelper.labelBold("Upcoming Assignments", 15, StyleHelper.TEXT));

        int shown = 0;
        for (Assignment a : ds.getAssignments()) {
            if (a.isCompleted() || shown >= 4) continue;
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label name = StyleHelper.label(a.getTitle(), 13, StyleHelper.TEXT2);
            HBox.setHgrow(name, Priority.ALWAYS);
            name.setWrapText(false);
            Label due  = StyleHelper.label(a.getDueDate(), 11, StyleHelper.TEXT3);
            Label chip = priorityChip(a.getPriority());
            row.getChildren().addAll(name, due, chip);
            assignCard.getChildren().add(row);
            shown++;
        }
        cols.getChildren().addAll(todayCard, assignCard);

        // ── Announcements ─────────────────────────────────────
        VBox annCard = StyleHelper.card();
        annCard.getChildren().add(StyleHelper.labelBold("Recent Announcements", 15, StyleHelper.TEXT));

        for (int i = 0; i < Math.min(3, ds.getAnnouncements().size()); i++) {
            Announcement ann = ds.getAnnouncements().get(i);
            VBox item = new VBox(3);
            item.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 8));
            item.setPadding(new Insets(10, 14, 10, 14));
            if (ann.isPinned()) {
                item.setBorder(StyleHelper.borderLeft(StyleHelper.ACCENT));
            }
            Label t = StyleHelper.labelBold((ann.isPinned() ? "📌 " : "") + ann.getTitle(), 13, StyleHelper.TEXT);
            Label d = StyleHelper.label(ann.getDate(), 11, StyleHelper.TEXT3);
            item.getChildren().addAll(t, d);
            annCard.getChildren().add(item);
        }

        getChildren().addAll(stats, cols, annCard);
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = StyleHelper.card();
        card.getChildren().addAll(
            StyleHelper.label(label, 11, StyleHelper.TEXT3),
            StyleHelper.labelBold(value, 28, color)
        );
        return card;
    }

    private Label priorityChip(String priority) {
        String color = StyleHelper.priorityColor(priority);
        Label chip = new Label(priority);
        chip.setBackground(StyleHelper.bgRadius(color + "22", 20));
        chip.setTextFill(Color.web(color));
        chip.setPadding(new Insets(3, 10, 3, 10));
        chip.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 11));
        return chip;
    }
}
