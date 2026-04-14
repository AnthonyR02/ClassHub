package com.classhub.utils;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StyleHelper {
    // ── Palette ──────────────────────────────────────────────────────────────
    public static final String BG           = "#0f1117";
    public static final String SURFACE      = "#181c27";
    public static final String SURFACE2     = "#1f2436";
    public static final String SURFACE3     = "#252a3d";
    public static final String BORDER       = "#ffffff12";   // ~7% white
    public static final String BORDER2      = "#ffffff20";   // ~12%
    public static final String TEXT         = "#e8eaf2";
    public static final String TEXT2        = "#9097b4";
    public static final String TEXT3        = "#5e6482";
    public static final String ACCENT       = "#6c8ef5";
    public static final String ACCENT_DARK  = "#4f6fe0";
    public static final String MINT         = "#3ecfb0";
    public static final String ROSE         = "#f5697b";
    public static final String AMBER        = "#f5a623";
    public static final String VIOLET       = "#9f7ffe";
    public static final String SKY          = "#5ab4f5";

    // ── Background helpers ────────────────────────────────────────────────────
    public static Background bg(String hex) {
        return new Background(new BackgroundFill(Color.web(hex), CornerRadii.EMPTY, Insets.EMPTY));
    }
    public static Background bgRadius(String hex, double r) {
        return new Background(new BackgroundFill(Color.web(hex), new CornerRadii(r), Insets.EMPTY));
    }

    // ── Border helper ─────────────────────────────────────────────────────────
    public static Border border(String hex, double r) {
        return new Border(new BorderStroke(Color.web(hex), BorderStrokeStyle.SOLID,
                new CornerRadii(r), new BorderWidths(1)));
    }
    public static Border borderLeft(String hex) {
        return new Border(new BorderStroke(
                Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.web(hex),
                BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 3), Insets.EMPTY));
    }

    // ── Label factories ───────────────────────────────────────────────────────
    public static Label label(String text, int size, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("System", size));
        l.setTextFill(Color.web(color));
        return l;
    }
    public static Label labelBold(String text, int size, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, size));
        l.setTextFill(Color.web(color));
        return l;
    }

    // ── Card factory ──────────────────────────────────────────────────────────
    public static VBox card() {
        VBox card = new VBox(10);
        card.setBackground(bgRadius(SURFACE, 12));
        card.setBorder(border(BORDER, 12));
        card.setPadding(new Insets(16, 18, 16, 18));
        return card;
    }

    // ── Priority / letter-grade colours ──────────────────────────────────────
    public static String priorityColor(String p) {
        return switch (p.toUpperCase()) {
            case "HIGH"   -> ROSE;
            case "MEDIUM" -> AMBER;
            default       -> MINT;
        };
    }
    public static String gradeColor(String letter) {
        return switch (letter) {
            case "A" -> MINT;
            case "B" -> ACCENT;
            case "C" -> AMBER;
            default  -> ROSE;
        };
    }
}
