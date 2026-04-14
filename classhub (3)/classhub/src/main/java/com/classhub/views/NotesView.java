package com.classhub.views;

import com.classhub.models.Note;
import com.classhub.utils.*;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class NotesView extends HBox {

    public NotesView() {
        setBackground(StyleHelper.bg(StyleHelper.BG));
        setMinHeight(520);
        build();
    }

    private void build() {
        DataStore ds = DataStore.getInstance();
        List<Note> notes = ds.getNotes();

        // ── Left: note list ───────────────────────────────────
        VBox list = new VBox(0);
        list.setMinWidth(270); list.setMaxWidth(270);
        list.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE, 12));
        list.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                new CornerRadii(12, 0, 0, 12, false), new BorderWidths(1, 0, 1, 1))));

        TextField search = new TextField();
        search.setPromptText("Search notes…");
        search.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 0));
        search.setStyle("-fx-text-fill:" + StyleHelper.TEXT + ";" +
                        "-fx-prompt-text-fill:" + StyleHelper.TEXT3 + ";" +
                        "-fx-border-color:" + StyleHelper.BORDER + ";" +
                        "-fx-border-width:0 0 1 0;-fx-padding:10 12 10 12;-fx-font-size:13;");
        list.getChildren().add(search);

        for (int i = 0; i < notes.size(); i++) {
            Note n = notes.get(i);
            VBox item = new VBox(4);
            item.setPadding(new Insets(12, 16, 12, 16));
            item.setBorder(new Border(new BorderStroke(
                    Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
            if (i == 0) {
                item.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT + "14", 0));
                item.setBorder(new Border(new BorderStroke(
                        Color.TRANSPARENT, Color.TRANSPARENT,
                        Color.web(StyleHelper.BORDER), Color.web(StyleHelper.ACCENT),
                        BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                        BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 2), Insets.EMPTY)));
            }
            Label title = StyleHelper.labelBold(n.getTitle(), 13, StyleHelper.TEXT);
            title.setWrapText(true);
            Label sub = StyleHelper.label(n.getSubject() + "  ·  " + n.getDate(), 11, StyleHelper.TEXT3);
            Label tag = new Label("#" + n.getTag());
            tag.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT + "18", 20));
            tag.setTextFill(Color.web(StyleHelper.ACCENT));
            tag.setPadding(new Insets(2, 8, 2, 8));
            tag.setFont(javafx.scene.text.Font.font("System", 10));
            item.getChildren().addAll(title, sub, tag);
            list.getChildren().add(item);
        }

        // ── Right: editor ─────────────────────────────────────
        VBox editor = new VBox(14);
        HBox.setHgrow(editor, Priority.ALWAYS);
        editor.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE, 12));
        editor.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                new CornerRadii(0, 12, 12, 0, false), new BorderWidths(1, 1, 1, 0))));
        editor.setPadding(new Insets(24, 28, 24, 28));

        if (!notes.isEmpty()) {
            Note first = notes.get(0);

            HBox meta = new HBox(10);
            meta.setAlignment(Pos.CENTER_LEFT);
            Label subjChip = chip(first.getSubject(), StyleHelper.ACCENT);
            Label tagChip  = chip("#" + first.getTag(), StyleHelper.VIOLET);
            Label dateChip = StyleHelper.label(first.getDate(), 11, StyleHelper.TEXT3);
            meta.getChildren().addAll(subjChip, tagChip, dateChip);

            Label title = StyleHelper.labelBold(first.getTitle(), 22, StyleHelper.TEXT);
            title.setWrapText(true);

            Label body = StyleHelper.label(first.getBody(), 14, StyleHelper.TEXT2);
            body.setWrapText(true);
            body.setLineSpacing(4);

            editor.getChildren().addAll(meta, title, body);
        }

        getChildren().addAll(list, editor);
    }

    private Label chip(String text, String color) {
        Label l = new Label(text);
        l.setBackground(StyleHelper.bgRadius(color + "20", 20));
        l.setTextFill(Color.web(color));
        l.setPadding(new Insets(3, 10, 3, 10));
        l.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 11));
        return l;
    }
}
