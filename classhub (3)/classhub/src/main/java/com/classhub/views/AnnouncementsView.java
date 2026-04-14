package com.classhub.views;

import com.classhub.models.Announcement;
import com.classhub.utils.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class AnnouncementsView extends VBox {

    public AnnouncementsView() {
        setSpacing(14);
        setBackground(StyleHelper.bg(StyleHelper.BG));
        build();
    }

    private void build() {
        // Filter toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        String[] filters = {"All", "Pinned", "High Priority"};
        for (int i = 0; i < filters.length; i++) {
            Label chip = new Label(filters[i]);
            if (i == 0) {
                chip.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT + "30", 20));
                chip.setTextFill(Color.web(StyleHelper.ACCENT));
            } else {
                chip.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 20));
                chip.setTextFill(Color.web(StyleHelper.TEXT3));
            }
            chip.setPadding(new Insets(5, 14, 5, 14));
            chip.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 12));
            toolbar.getChildren().add(chip);
        }
        HBox sp = new HBox(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label postBtn = new Label("+ Post Announcement");
        postBtn.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT, 8));
        postBtn.setTextFill(Color.WHITE);
        postBtn.setPadding(new Insets(7, 16, 7, 16));
        postBtn.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 13));
        toolbar.getChildren().addAll(sp, postBtn);
        getChildren().add(toolbar);

        for (Announcement ann : DataStore.getInstance().getAnnouncements()) {
            VBox card = StyleHelper.card();

            if (ann.isPinned()) {
                card.setBorder(StyleHelper.borderLeft(StyleHelper.ACCENT));
            }

            // Title row
            HBox topRow = new HBox(10);
            topRow.setAlignment(Pos.CENTER_LEFT);
            Label title = StyleHelper.labelBold(ann.getTitle(), 14, StyleHelper.TEXT);
            HBox.setHgrow(title, Priority.ALWAYS);
            topRow.getChildren().add(title);

            if (ann.isPinned()) {
                Label pin = new Label("📌 Pinned");
                pin.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT + "18", 20));
                pin.setTextFill(Color.web(StyleHelper.ACCENT));
                pin.setPadding(new Insets(2, 8, 2, 8));
                pin.setFont(javafx.scene.text.Font.font("System", 10));
                topRow.getChildren().add(pin);
            }

            String pColor = StyleHelper.priorityColor(ann.getPriority());
            Label priority = new Label(ann.getPriority() + " Priority");
            priority.setBackground(StyleHelper.bgRadius(pColor + "22", 20));
            priority.setTextFill(Color.web(pColor));
            priority.setPadding(new Insets(3, 10, 3, 10));
            priority.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 11));
            topRow.getChildren().add(priority);

            Label meta = StyleHelper.label(ann.getDate(), 12, StyleHelper.TEXT3);
            Label body = StyleHelper.label(ann.getBody(), 13, StyleHelper.TEXT2);
            body.setWrapText(true);
            body.setLineSpacing(3);

            card.getChildren().addAll(topRow, meta, body);
            getChildren().add(card);
        }
    }
}
