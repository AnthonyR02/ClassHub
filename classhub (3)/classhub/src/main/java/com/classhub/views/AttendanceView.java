package com.classhub.views;

import com.classhub.models.Attendance;
import com.classhub.utils.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class AttendanceView extends VBox {

    private static final String[] AVATAR_COLORS = {
        StyleHelper.ACCENT, StyleHelper.MINT, StyleHelper.ROSE, StyleHelper.VIOLET,
        StyleHelper.AMBER,  StyleHelper.SKY,  StyleHelper.MINT, StyleHelper.ROSE
    };

    public AttendanceView() {
        setSpacing(16);
        setBackground(StyleHelper.bg(StyleHelper.BG));
        build();
    }

    private void build() {
        DataStore ds = DataStore.getInstance();
        java.util.List<Attendance> list = ds.getAttendance();

        double avg = list.stream().mapToDouble(Attendance::getRate).average().orElse(0);
        long perfect = list.stream().filter(a -> a.getRate() >= 100).count();
        long atRisk  = list.stream().filter(a -> a.getRate() < 80).count();

        // ── Summary stat cards ────────────────────────────────
        HBox stats = new HBox(14);
        VBox[] cards = {
            statCard(String.valueOf(list.size()), "Total Students", StyleHelper.ACCENT),
            statCard(String.format("%.0f%%", avg), "Class Average", StyleHelper.MINT),
            statCard(String.valueOf(atRisk),  "Below 80%",      StyleHelper.ROSE),
            statCard(String.valueOf(perfect), "Perfect Record",  StyleHelper.VIOLET)
        };
        for (VBox c : cards) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
            stats.getChildren().add(c);
        }

        // ── Student table card ────────────────────────────────
        VBox card = StyleHelper.card();
        card.setSpacing(0);
        card.setPadding(new Insets(0));

        HBox thead = new HBox(0);
        thead.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 12));
        thead.setPadding(new Insets(10, 20, 10, 20));
        thead.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        thead.getChildren().addAll(
            thCell("Student", 220), thCell("Present", 90),
            thCell("Absent",  80),  thCell("Late", 80),
            thCell("Rate", -1),     thCell("Status", 110)
        );
        card.getChildren().add(thead);

        int ci = 0;
        for (Attendance a : list) {
            String color = AVATAR_COLORS[ci % AVATAR_COLORS.length];
            double rate = a.getRate();

            HBox row = new HBox(0);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 20, 12, 20));
            row.setBorder(new Border(new BorderStroke(
                    Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

            // Avatar
            String initials = getInitials(a.getStudentName());
            Label av = new Label(initials);
            av.setBackground(StyleHelper.bgRadius(color + "33", 50));
            av.setTextFill(Color.web(color));
            av.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 11));
            av.setMinWidth(34); av.setMinHeight(34);
            av.setMaxWidth(34); av.setMaxHeight(34);
            av.setAlignment(Pos.CENTER);

            Label name = StyleHelper.labelBold(a.getStudentName(), 13, StyleHelper.TEXT);
            name.setMinWidth(176);
            name.setPadding(new Insets(0, 0, 0, 10));

            Label present = StyleHelper.label(String.valueOf(a.getPresent()), 13, StyleHelper.TEXT2);
            present.setMinWidth(90);
            Label absent = StyleHelper.label(String.valueOf(a.getAbsent()), 13, StyleHelper.TEXT2);
            absent.setMinWidth(80);
            Label late = StyleHelper.label(String.valueOf(a.getLate()), 13, StyleHelper.TEXT2);
            late.setMinWidth(80);

            // Progress
            HBox progressWrap = new HBox(10);
            progressWrap.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(progressWrap, Priority.ALWAYS);
            ProgressBar pb = new ProgressBar(rate / 100.0);
            String barColor = rate >= 90 ? StyleHelper.MINT : rate >= 80 ? StyleHelper.ACCENT : StyleHelper.ROSE;
            pb.setStyle("-fx-accent:" + barColor + ";-fx-pref-height:6;-fx-background-radius:4;");
            pb.setPrefWidth(160);
            Label pct = StyleHelper.labelBold(String.format("%.0f%%", rate), 12, barColor);
            progressWrap.getChildren().addAll(pb, pct);

            String statusTxt   = rate >= 90 ? "Excellent" : rate >= 80 ? "Good" : "At Risk";
            String statusColor = rate >= 90 ? StyleHelper.MINT : rate >= 80 ? StyleHelper.ACCENT : StyleHelper.ROSE;
            Label status = chip(statusTxt, statusColor);
            status.setMinWidth(110);

            row.getChildren().addAll(av, name, present, absent, late, progressWrap, status);
            card.getChildren().add(row);
            ci++;
        }

        getChildren().addAll(stats, card);
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private VBox statCard(String val, String label, String color) {
        VBox c = StyleHelper.card();
        c.getChildren().addAll(
            StyleHelper.label(label, 11, StyleHelper.TEXT3),
            StyleHelper.labelBold(val, 26, color)
        );
        return c;
    }

    private Label thCell(String text, int w) {
        Label l = StyleHelper.labelBold(text, 10, StyleHelper.TEXT3);
        if (w > 0) l.setMinWidth(w);
        else { l.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(l, Priority.ALWAYS); }
        return l;
    }

    private Label chip(String text, String color) {
        Label l = new Label(text);
        l.setBackground(StyleHelper.bgRadius(color + "22", 20));
        l.setTextFill(Color.web(color));
        l.setPadding(new Insets(3, 10, 3, 10));
        l.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 11));
        return l;
    }
}
