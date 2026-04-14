package com.classhub.views;

import com.classhub.models.Grade;
import com.classhub.utils.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.util.*;

public class GradesView extends VBox {

    public GradesView() {
        setSpacing(20);
        setBackground(StyleHelper.bg(StyleHelper.BG));
        build();
    }

    private void build() {
        DataStore ds = DataStore.getInstance();

        // ── Top row: GPA card + bar chart ─────────────────────
        HBox top = new HBox(16);

        VBox gpaCard = StyleHelper.card();
        gpaCard.setMinWidth(170);
        gpaCard.getChildren().addAll(
            StyleHelper.label("Cumulative GPA", 11, StyleHelper.TEXT3),
            StyleHelper.labelBold("3.8", 42, StyleHelper.ACCENT),
            chip("Top 15% of class", StyleHelper.MINT)
        );

        VBox chartCard = StyleHelper.card();
        HBox.setHgrow(chartCard, Priority.ALWAYS);
        chartCard.getChildren().add(StyleHelper.labelBold("Average by Subject", 15, StyleHelper.TEXT));

        Map<String, List<Double>> bySubj = new LinkedHashMap<>();
        for (Grade g : ds.getGrades())
            bySubj.computeIfAbsent(g.getSubject(), k -> new ArrayList<>()).add(g.getPercentage());

        String[] barColors = {StyleHelper.ACCENT, StyleHelper.MINT, StyleHelper.ROSE,
                              StyleHelper.VIOLET, StyleHelper.AMBER, StyleHelper.SKY};

        HBox bars = new HBox(10);
        bars.setAlignment(Pos.BOTTOM_CENTER);
        bars.setPrefHeight(140);
        int ci = 0;
        for (Map.Entry<String, List<Double>> e : bySubj.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(d -> d).average().orElse(0);
            String color = barColors[ci % barColors.length];

            VBox barWrap = new VBox(4);
            barWrap.setAlignment(Pos.BOTTOM_CENTER);
            HBox.setHgrow(barWrap, Priority.ALWAYS);

            Label valLbl = StyleHelper.labelBold(String.format("%.0f", avg), 11, color);

            Region bar = new Region();
            bar.setBackground(StyleHelper.bgRadius(color + "26", 4));
            bar.setBorder(new Border(new BorderStroke(Color.web(color + "55"),
                    BorderStrokeStyle.SOLID, new CornerRadii(4, 4, 0, 0, false), new BorderWidths(1))));
            bar.setPrefHeight(Math.max(8, (int)(avg / 100.0 * 110)));
            bar.setMaxWidth(Double.MAX_VALUE);

            String abbr = e.getKey().length() > 6 ? e.getKey().substring(0, 5) + "." : e.getKey();
            Label subLbl = StyleHelper.label(abbr, 10, StyleHelper.TEXT3);
            subLbl.setWrapText(true);
            subLbl.setAlignment(Pos.CENTER);

            barWrap.getChildren().addAll(valLbl, bar, subLbl);
            bars.getChildren().add(barWrap);
            ci++;
        }
        chartCard.getChildren().add(bars);
        top.getChildren().addAll(gpaCard, chartCard);

        // ── Grades table ──────────────────────────────────────
        VBox tableCard = StyleHelper.card();
        tableCard.setSpacing(0);
        tableCard.setPadding(new Insets(0));

        HBox thead = new HBox(0);
        thead.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 12));
        thead.setPadding(new Insets(10, 20, 10, 20));
        thead.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        thead.getChildren().addAll(
            thCell("Subject", 170), thCell("Assignment", -1),
            thCell("Date", 100),    thCell("Score", 90),
            thCell("Grade", 90)
        );
        tableCard.getChildren().add(thead);

        for (Grade g : ds.getGrades()) {
            HBox row = new HBox(0);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 20, 12, 20));
            row.setBorder(new Border(new BorderStroke(
                    Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

            Label subj = StyleHelper.label(g.getSubject(), 13, StyleHelper.TEXT2);
            subj.setMinWidth(170);
            Label assign = StyleHelper.labelBold(g.getAssignmentName(), 13, StyleHelper.TEXT);
            HBox.setHgrow(assign, Priority.ALWAYS);
            Label date  = StyleHelper.label(g.getDate(), 13, StyleHelper.TEXT3);
            date.setMinWidth(100);
            Label score = StyleHelper.label(
                    String.format("%.0f / %.0f", g.getScore(), g.getMaxScore()), 13, StyleHelper.TEXT2);
            score.setMinWidth(90);
            Label letter = chip(g.getLetterGrade(), StyleHelper.gradeColor(g.getLetterGrade()));

            row.getChildren().addAll(subj, assign, date, score, letter);
            tableCard.getChildren().add(row);
        }

        getChildren().addAll(top, tableCard);
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
