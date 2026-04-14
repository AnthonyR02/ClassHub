package com.classhub.views;

import com.classhub.models.Assignment;
import com.classhub.utils.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class AssignmentsView extends VBox {

    private Map<Assignment, CheckBox> checkMap = new HashMap<>();

    public AssignmentsView() {
        setSpacing(16);
        setBackground(StyleHelper.bg(StyleHelper.BG));
        build();
    }

    private void build() {
        checkMap.clear(); // IMPORTANT: reset checkbox tracking

        // Filter chips
        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);

        String[] labels = {"All", "Pending", "Completed", "High Priority"};
        for (int i = 0; i < labels.length; i++) {
            Label chip = new Label(labels[i]);
            if (i == 0) {
                chip.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT + "30", 20));
                chip.setTextFill(Color.web(StyleHelper.ACCENT));
            } else {
                chip.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 20));
                chip.setTextFill(Color.web(StyleHelper.TEXT3));
            }
            chip.setPadding(new Insets(5, 14, 5, 14));
            chip.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 12));
            filters.getChildren().add(chip);
        }

        HBox sp = new HBox();
        HBox.setHgrow(sp, Priority.ALWAYS);

        // Add button
        Label addBtn = new Label("+ New Assignment");
        addBtn.setOnMouseClicked(e -> openNewAssignmentDialog());
        addBtn.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT, 8));
        addBtn.setTextFill(Color.WHITE);
        addBtn.setPadding(new Insets(7, 16, 7, 16));
        addBtn.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 13));

        // Delete button
        Label deleteBtn = new Label("Delete Selected");
        deleteBtn.setBackground(StyleHelper.bgRadius("#ff4d4d", 8));
        deleteBtn.setTextFill(Color.WHITE);
        deleteBtn.setPadding(new Insets(7, 16, 7, 16));
        deleteBtn.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 13));

        deleteBtn.setOnMouseClicked(e -> {
            DataStore.getInstance().getAssignments().removeIf(a ->
                    checkMap.containsKey(a) && checkMap.get(a).isSelected()
            );

            getChildren().clear();
            build();
        });

        filters.getChildren().addAll(sp, addBtn, deleteBtn);

        // Table card
        VBox card = StyleHelper.card();
        card.setSpacing(0);
        card.setPadding(new Insets(0));

        // Header
        HBox thead = new HBox(0);
        thead.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 12));
        thead.setPadding(new Insets(10, 16, 10, 16));
        thead.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0)
        )));

        thead.getChildren().addAll(
                thCell("", 36),
                thCell("Title", 300),
                thCell("Subject", 150),
                thCell("Due Date", 120),
                thCell("Priority", 110),
                thCell("Status", 110)
        );

        VBox tbody = new VBox(0);

        for (Assignment a : DataStore.getInstance().getAssignments()) {
            HBox row = new HBox(0);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setBorder(new Border(new BorderStroke(
                    Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0)
            )));

            CheckBox cb = new CheckBox();
            cb.setSelected(a.isCompleted());
            cb.setMinWidth(36);

            checkMap.put(a, cb);

            Label title = StyleHelper.labelBold(
                    a.getTitle(), 13,
                    a.isCompleted() ? StyleHelper.TEXT3 : StyleHelper.TEXT
            );
            title.setMinWidth(300);
            if (a.isCompleted()) title.setStyle("-fx-strikethrough:true;");

            Label subj = StyleHelper.label(a.getSubject(), 13, StyleHelper.TEXT2);
            subj.setMinWidth(150);

            Label due = StyleHelper.label(a.getDueDate(), 13, StyleHelper.TEXT3);
            due.setMinWidth(120);

            Label priority = chip(a.getPriority(), StyleHelper.priorityColor(a.getPriority()));
            priority.setMinWidth(110);

            String statusTxt = a.isCompleted() ? "Done" : "Pending";
            String statusColor = a.isCompleted() ? StyleHelper.TEXT3 : StyleHelper.ACCENT;
            Label status = chip(statusTxt, statusColor);

            row.getChildren().addAll(cb, title, subj, due, priority, status);
            tbody.getChildren().add(row);
        }

        card.getChildren().addAll(thead, tbody);
        getChildren().addAll(filters, card);
    }

    private Label thCell(String text, int w) {
        Label l = StyleHelper.labelBold(text, 10, StyleHelper.TEXT3);
        l.setMinWidth(w);
        if (w == 300) {
            l.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(l, Priority.ALWAYS);
        }
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

    private void openNewAssignmentDialog() {
        Dialog<Assignment> dialog = new Dialog<>();
        dialog.setTitle("New Assignment");

        TextField titleField = new TextField();
        TextField subjectField = new TextField();
        DatePicker datePicker = new DatePicker();

        VBox content = new VBox(10,
                new Label("Title:"), titleField,
                new Label("Subject:"), subjectField,
                new Label("Due Date:"), datePicker
        );

        dialog.getDialogPane().setContent(content);

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == addBtn) {
                return new Assignment(
                        titleField.getText(),
                        subjectField.getText(),
                        datePicker.getValue().toString(),
                        "HIGH",
                        false
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(a -> {
            DataStore.getInstance().getAssignments().add(a);
            getChildren().clear();
            build();
        });
    }
}
