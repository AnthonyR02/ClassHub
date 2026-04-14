package com.classhub.views;

import com.classhub.models.ClassSession;
import com.classhub.utils.DataStore;
import com.classhub.utils.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ScheduleView extends VBox {

    private static final String[] DAYS  = {"Monday","Tuesday","Wednesday","Thursday","Friday"};
    private static final String[] SLOTS = {"8:00","9:15","10:30"};

    public ScheduleView() {
        setSpacing(20);
        setBackground(StyleHelper.bg(StyleHelper.BG));
        build();
    }

    private void build() {
        // ── Day-header row ────────────────────────────────────
        HBox header = new HBox(0);
        header.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 12));
        header.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                new CornerRadii(12, 12, 0, 0, false), new BorderWidths(1, 1, 0, 1))));

        header.getChildren().add(headerCell("", 80));
        for (String day : DAYS) {
            Label h = headerCell(day, -1);
            HBox.setHgrow(h, Priority.ALWAYS);
            header.getChildren().add(h);
        }

        // ── Time-slot rows ─────────────────────────────────────
        VBox gridBody = new VBox(0);
        gridBody.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE, 12));
        gridBody.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                new CornerRadii(0, 0, 12, 12, false), new BorderWidths(0, 1, 1, 1))));

        for (String slot : SLOTS) {
            HBox row = new HBox(0);
            row.setMinHeight(80);

            Label timeLbl = StyleHelper.label(slot, 11, StyleHelper.TEXT3);
            timeLbl.setMinWidth(80); timeLbl.setMaxWidth(80);
            timeLbl.setPadding(new Insets(10, 0, 0, 12));
            timeLbl.setAlignment(Pos.TOP_LEFT);
            row.getChildren().add(timeLbl);

            for (String day : DAYS) {
                StackPane cell = new StackPane();
                cell.setBorder(new Border(new BorderStroke(
                        Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 1))));
                HBox.setHgrow(cell, Priority.ALWAYS);
                cell.setMinWidth(0);
                cell.setPadding(new Insets(6));
                cell.setAlignment(Pos.TOP_LEFT);

                for (ClassSession s : DataStore.getInstance().getSessions()) {
                    if (s.getDay().equals(day) && s.getStartTime().equals(slot)) {
                        cell.getChildren().add(sessionBlock(s));
                        break;
                    }
                }
                row.getChildren().add(cell);
            }
            gridBody.getChildren().add(row);
        }

        VBox weekGrid = new VBox(0);
        weekGrid.getChildren().addAll(header, gridBody);

        // ── Class list ─────────────────────────────────────────
        Label listTitle = StyleHelper.labelBold("All Classes", 15, StyleHelper.TEXT);

        VBox listCard = StyleHelper.card();
        listCard.setSpacing(0);
        listCard.setPadding(new Insets(0));

        HBox thead = new HBox(0);
        thead.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 12));
        thead.setPadding(new Insets(10, 20, 10, 20));
        thead.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        thead.getChildren().addAll(
                thCell("Subject", 180), thCell("Teacher", 160),
                thCell("Room", 100),    thCell("Day", 130),
                thCell("Time", -1));
        listCard.getChildren().add(thead);

        for (ClassSession s : DataStore.getInstance().getSessions()) {
            HBox row = new HBox(0);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(11, 20, 11, 20));
            row.setBorder(new Border(new BorderStroke(
                    Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

            Label dot = StyleHelper.label("●", 10, s.getColor());
            dot.setPadding(new Insets(0, 10, 0, 0));

            Label subj = StyleHelper.labelBold(s.getName(), 13, StyleHelper.TEXT);
            subj.setMinWidth(170);
            Label teacher = StyleHelper.label(s.getTeacher(), 13, StyleHelper.TEXT2);
            teacher.setMinWidth(160);
            Label room = StyleHelper.label(s.getRoom(), 13, StyleHelper.TEXT2);
            room.setMinWidth(100);
            Label day = StyleHelper.label(s.getDay(), 13, StyleHelper.TEXT2);
            day.setMinWidth(130);
            Label time = StyleHelper.label(s.getStartTime() + " – " + s.getEndTime(), 13, StyleHelper.TEXT3);
            HBox.setHgrow(time, Priority.ALWAYS);

            row.getChildren().addAll(dot, subj, teacher, room, day, time);
            listCard.getChildren().add(row);
        }

        getChildren().addAll(weekGrid, listTitle, listCard);
    }

    private VBox sessionBlock(ClassSession s) {
        String hex = s.getColor();
        VBox box = new VBox(2);
        box.setBackground(StyleHelper.bgRadius(hex + "26", 6));
        box.setBorder(new Border(new BorderStroke(
                Color.web(hex + "55"), BorderStrokeStyle.SOLID,
                new CornerRadii(6), new BorderWidths(1))));
        box.setMaxWidth(Double.MAX_VALUE);
        box.setPadding(new Insets(6, 8, 6, 8));

        Label name = StyleHelper.labelBold(s.getName(), 11, hex);
        name.setWrapText(true);
        Label room = StyleHelper.label(s.getRoom(), 10, hex);
        room.setOpacity(0.7);
        box.getChildren().addAll(name, room);
        return box;
    }

    private Label headerCell(String text, int fixedWidth) {
        Label lbl = StyleHelper.labelBold(text.toUpperCase(), 10, StyleHelper.TEXT3);
        lbl.setAlignment(Pos.CENTER);
        lbl.setPadding(new Insets(11, 14, 11, 14));
        lbl.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 1))));
        if (fixedWidth > 0) { lbl.setMinWidth(fixedWidth); lbl.setMaxWidth(fixedWidth); }
        return lbl;
    }

    private Label thCell(String text, int w) {
        Label l = StyleHelper.labelBold(text, 10, StyleHelper.TEXT3);
        if (w > 0) l.setMinWidth(w);
        else { l.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(l, Priority.ALWAYS); }
        return l;
    }
}
