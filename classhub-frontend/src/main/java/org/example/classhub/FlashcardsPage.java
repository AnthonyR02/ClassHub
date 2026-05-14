package org.example.classhub;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardsPage {

    // ── colours ───────────────────────────────────────────────────────────────
    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#181c27";
    private static final String BORDER  = "#ffffff12";
    private static final String TEXT    = "#e8eaf2";
    private static final String TEXT2   = "#9097b4";
    private static final String TEXT3   = "#5e6482";
    private static final String ACCENT  = "#6c8ef5";
    private static final String ROSE    = "#f5697b";
    private static final String GREEN   = "#3ecfb0";
    private static final String AMBER   = "#f5a623";

    // ── state ─────────────────────────────────────────────────────────────────
    private static class Deck {
        String name;
        String subject;
        List<String[]> cards = new ArrayList<>(); // { front, back }

        Deck(String name, String subject) {
            this.name = name;
            this.subject = subject;
        }
    }

    private List<Deck> decks = new ArrayList<>();
    private Deck activeDeck = null;
    private int  activeCardIndex = 0;
    private boolean cardFlipped = false;

    private VBox deckListPanel;
    private VBox studyPanel;
    private StackPane cardFace;
    private Label cardText;
    private Label cardHint;
    private Label cardCounter;
    private Label progressLbl;
    private Scene scene;

    // ── constructor ───────────────────────────────────────────────────────────
    public FlashcardsPage(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:" + BG + ";");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
    }

    // ── sidebar ───────────────────────────────────────────────────────────────
    private VBox buildSidebar(Stage stage) {
        VBox sidebar = new VBox(0);
        sidebar.setMinWidth(200); sidebar.setMaxWidth(200);
        sidebar.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:0 1 0 0;");

        VBox logo = new VBox(3);
        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");
        Label logoTitle = new Label("ClassHub");
        logoTitle.setStyle("-fx-font-size:18px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Label logoSub = new Label("STUDENT PORTAL");
        logoSub.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        logo.getChildren().addAll(logoTitle, logoSub);

        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);
        nav.getChildren().add(navSection("MAIN"));
        Label dashItem = navItem("Dashboard", false);
        dashItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.dashboardScene));
        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.calendarScene));
        Label assignItem = navItem("Assignments", false);
        assignItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.assignmentsScene));
        Label gradesItem = navItem("Grades", false);
        gradesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.gradesScene));
        Label notesItem = navItem("Notes", false);
        notesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.notesScene));
        Label flashItem = navItem("Flashcards", true);
        nav.getChildren().addAll(dashItem, calItem, assignItem, gradesItem, notesItem, flashItem);

        VBox footer = new VBox(8);
        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:1 0 0 0;");
        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);
        Label initials = new Label(SessionManager.getInitials());
        initials.setMinWidth(32); initials.setMinHeight(32);
        initials.setMaxWidth(32); initials.setMaxHeight(32);
        initials.setAlignment(Pos.CENTER);
        initials.setStyle("-fx-background-color:rgba(108,142,245,0.2);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:50;");
        VBox userInfo = new VBox(1);
        Label userName = new Label(SessionManager.getFullName());
        userName.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Label userRole = new Label(SessionManager.getRole());
        userRole.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        userInfo.getChildren().addAll(userName, userRole);
        userRow.getChildren().addAll(initials, userInfo);
        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:" + ROSE + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-cursor:hand;-fx-border-color:rgba(245,105,123,0.2);-fx-border-width:1;-fx-border-radius:8;");
        logoutBtn.setOnAction(e -> { SessionManager.logout(); stage.setScene(ClassHubApplication.loginScene); });
        footer.getChildren().addAll(userRow, logoutBtn);
        sidebar.getChildren().addAll(logo, nav, footer);
        return sidebar;
    }

    // ── main content ──────────────────────────────────────────────────────────
    private VBox buildMain() {
        VBox main = new VBox(0);
        HBox.setHgrow(main, Priority.ALWAYS);

        // Top bar
        HBox topbar = new HBox();
        topbar.setPrefHeight(52); topbar.setMinHeight(52);
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setPadding(new Insets(0, 20, 0, 20));
        topbar.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");
        Label title = new Label("Flashcards");
        title.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button newDeckBtn = new Button("+ New Deck");
        newDeckBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;");
        newDeckBtn.setOnAction(e -> showNewDeckDialog());
        topbar.getChildren().addAll(title, spacer, newDeckBtn);

        // Split: deck list left, study area right
        HBox body = new HBox(0);
        VBox.setVgrow(body, Priority.ALWAYS);

        // Deck list panel
        deckListPanel = new VBox(8);
        deckListPanel.setPadding(new Insets(16));
        deckListPanel.setMinWidth(240); deckListPanel.setMaxWidth(240);
        deckListPanel.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:0 1 0 0;");

        Label deckHeader = new Label("MY DECKS");
        deckHeader.setStyle("-fx-font-size:9px;-fx-font-weight:700;-fx-letter-spacing:1px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        deckListPanel.getChildren().add(deckHeader);
        refreshDeckList();

        ScrollPane deckScroll = new ScrollPane(deckListPanel);
        deckScroll.setMinWidth(240); deckScroll.setMaxWidth(240);
        deckScroll.setStyle("-fx-background-color:transparent;-fx-background:" + SURFACE + ";");
        deckScroll.setFitToWidth(true);
        deckScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        deckScroll.setBorder(Border.EMPTY);

        // Study panel
        studyPanel = new VBox(20);
        studyPanel.setAlignment(Pos.CENTER);
        studyPanel.setPadding(new Insets(40));
        studyPanel.setStyle("-fx-background-color:" + BG + ";");
        HBox.setHgrow(studyPanel, Priority.ALWAYS);
        renderEmptyState();

        ScrollPane studyScroll = new ScrollPane(studyPanel);
        studyScroll.setStyle("-fx-background-color:transparent;-fx-background:" + BG + ";");
        studyScroll.setFitToWidth(true);
        studyScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        studyScroll.setBorder(Border.EMPTY);
        HBox.setHgrow(studyScroll, Priority.ALWAYS);

        body.getChildren().addAll(deckScroll, studyScroll);
        main.getChildren().addAll(topbar, body);
        return main;
    }

    // ── deck list ─────────────────────────────────────────────────────────────
    private void refreshDeckList() {
        // Preserve header, remove everything after
        if (deckListPanel.getChildren().size() > 1)
            deckListPanel.getChildren().remove(1, deckListPanel.getChildren().size());

        if (decks.isEmpty()) {
            Label empty = new Label("No decks yet.\nClick + New Deck to start.");
            empty.setWrapText(true);
            empty.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:8 0 0 0;");
            deckListPanel.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < decks.size(); i++) {
            Deck deck = decks.get(i);
            boolean isActive = deck == activeDeck;

            VBox card = new VBox(3);
            card.setPadding(new Insets(10, 12, 10, 12));
            card.setStyle(isActive
                ? "-fx-background-color:rgba(108,142,245,0.14);-fx-border-color:" + ACCENT + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;"
                : "-fx-background-color:#1a1f2e;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;");

            Label nameL = new Label(deck.name);
            nameL.setStyle("-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
            Label subL = new Label(deck.subject);
            subL.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
            Label countL = new Label(deck.cards.size() + " card" + (deck.cards.size() == 1 ? "" : "s"));
            countL.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + ACCENT + ";");

            card.getChildren().addAll(nameL, subL, countL);
            final int idx = i;
            card.setOnMouseClicked(e -> {
                activeDeck = decks.get(idx);
                activeCardIndex = 0;
                cardFlipped = false;
                refreshDeckList();
                renderStudyView();
            });
            deckListPanel.getChildren().add(card);
        }
    }

    // ── empty state ───────────────────────────────────────────────────────────
    private void renderEmptyState() {
        studyPanel.getChildren().clear();
        studyPanel.setAlignment(Pos.CENTER);

        Label icon = new Label("🗂");
        icon.setStyle("-fx-font-size:52px;");
        Label msg = new Label("Create your first deck to start studying");
        msg.setStyle("-fx-font-size:14px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        Button createBtn = new Button("+ Create Deck");
        createBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:10 22 10 22;-fx-cursor:hand;");
        createBtn.setOnAction(e -> showNewDeckDialog());
        studyPanel.getChildren().addAll(icon, msg, createBtn);
    }

    // ── study view ────────────────────────────────────────────────────────────
    private void renderStudyView() {
        studyPanel.getChildren().clear();
        studyPanel.setAlignment(Pos.TOP_LEFT);
        studyPanel.setPadding(new Insets(28));

        if (activeDeck == null) { renderEmptyState(); return; }

        // Deck header row
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        VBox deckInfo = new VBox(2);
        Label deckNameL = new Label(activeDeck.name);
        deckNameL.setStyle("-fx-font-size:20px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        Label deckSubL = new Label(activeDeck.subject);
        deckSubL.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        deckInfo.getChildren().addAll(deckNameL, deckSubL);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button addCardBtn = new Button("+ Add Card");
        addCardBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:6 12 6 12;-fx-cursor:hand;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:6;");
        addCardBtn.setOnAction(e -> showAddCardDialog());

        Button editDeckBtn = new Button("Edit Deck");
        editDeckBtn.setStyle("-fx-background-color:#1a1f2e;-fx-text-fill:" + TEXT2 + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:6 12 6 12;-fx-cursor:hand;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:6;");
        editDeckBtn.setOnAction(e -> showEditDeckDialog());

        Button deleteDeckBtn = new Button("Delete");
        deleteDeckBtn.setStyle("-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:" + ROSE + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:6 12 6 12;-fx-cursor:hand;-fx-border-color:rgba(245,105,123,0.2);-fx-border-width:1;-fx-border-radius:6;");
        deleteDeckBtn.setOnAction(e -> {
            decks.remove(activeDeck);
            activeDeck = null;
            refreshDeckList();
            renderEmptyState();
        });

        headerRow.getChildren().addAll(deckInfo, sp, addCardBtn, editDeckBtn, deleteDeckBtn);
        studyPanel.getChildren().add(headerRow);

        if (activeDeck.cards.isEmpty()) {
            VBox emptyCards = new VBox(12);
            emptyCards.setAlignment(Pos.CENTER);
            emptyCards.setPadding(new Insets(60));
            Label emptyIcon = new Label("📝");
            emptyIcon.setStyle("-fx-font-size:40px;");
            Label emptyMsg = new Label("No cards yet. Add your first card!");
            emptyMsg.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
            Button quickAddBtn = new Button("+ Add Card");
            quickAddBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:10 22 10 22;-fx-cursor:hand;");
            quickAddBtn.setOnAction(e -> showAddCardDialog());
            emptyCards.getChildren().addAll(emptyIcon, emptyMsg, quickAddBtn);
            studyPanel.getChildren().add(emptyCards);
            return;
        }

        // Flashcard area
        if (activeCardIndex >= activeDeck.cards.size()) activeCardIndex = 0;
        String[] card = activeDeck.cards.get(activeCardIndex);

        cardCounter = new Label((activeCardIndex + 1) + " / " + activeDeck.cards.size());
        cardCounter.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

        // The big card
        StackPane cardContainer = new StackPane();
        cardContainer.setMaxWidth(560);
        cardContainer.setPrefHeight(280);
        cardContainer.setMinHeight(280);

        cardFace = new StackPane();
        cardFace.setPrefWidth(560);
        cardFace.setPrefHeight(280);
        cardFace.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:rgba(108,142,245,0.3);-fx-border-width:1;-fx-border-radius:16;-fx-background-radius:16;-fx-cursor:hand;");
        cardFace.setMaxWidth(560);

        VBox cardContent = new VBox(10);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(32));

        cardHint = new Label(cardFlipped ? "ANSWER" : "QUESTION");
        cardHint.setStyle("-fx-font-size:9px;-fx-font-weight:700;-fx-letter-spacing:1.5px;-fx-font-family:'Segoe UI';-fx-text-fill:" + (cardFlipped ? GREEN : ACCENT) + ";-fx-background-color:" + (cardFlipped ? "rgba(62,207,176,0.1)" : "rgba(108,142,245,0.1)") + ";-fx-background-radius:4;-fx-padding:3 8 3 8;");

        cardText = new Label(cardFlipped ? card[1] : card[0]);
        cardText.setStyle("-fx-font-size:20px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;-fx-wrap-text:true;-fx-text-alignment:center;");
        cardText.setWrapText(true);
        cardText.setMaxWidth(480);
        cardText.setAlignment(Pos.CENTER);

        Label tapHint = new Label("Click to flip");
        tapHint.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

        cardContent.getChildren().addAll(cardHint, cardText, tapHint);
        cardFace.getChildren().add(cardContent);
        cardContainer.getChildren().add(cardFace);

        cardFace.setOnMouseClicked(e -> flipCard(card));

        // Navigation buttons
        HBox navRow = new HBox(12);
        navRow.setAlignment(Pos.CENTER);

        Button prevBtn = new Button("← Prev");
        prevBtn.setStyle("-fx-background-color:#1a1f2e;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:9 18 9 18;-fx-cursor:hand;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;");
        prevBtn.setDisable(activeCardIndex == 0);
        prevBtn.setOnAction(e -> {
            if (activeCardIndex > 0) { activeCardIndex--; cardFlipped = false; renderStudyView(); }
        });

        Button shuffleBtn = new Button("⇄ Shuffle");
        shuffleBtn.setStyle("-fx-background-color:rgba(245,166,35,0.1);-fx-text-fill:" + AMBER + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:9 18 9 18;-fx-cursor:hand;-fx-border-color:rgba(245,166,35,0.2);-fx-border-width:1;-fx-border-radius:8;");
        shuffleBtn.setOnAction(e -> {
            Collections.shuffle(activeDeck.cards);
            activeCardIndex = 0;
            cardFlipped = false;
            renderStudyView();
        });

        Button nextBtn = new Button("Next →");
        nextBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:9 18 9 18;-fx-cursor:hand;");
        nextBtn.setDisable(activeCardIndex >= activeDeck.cards.size() - 1);
        nextBtn.setOnAction(e -> {
            if (activeCardIndex < activeDeck.cards.size() - 1) { activeCardIndex++; cardFlipped = false; renderStudyView(); }
        });

        navRow.getChildren().addAll(prevBtn, shuffleBtn, nextBtn);

        // Card grid for all cards
        Label allCardsTitle = new Label("All Cards");
        allCardsTitle.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;-fx-padding:8 0 4 0;");

        Button manageBtn = new Button("Manage Cards");
        manageBtn.setStyle("-fx-background-color:#1a1f2e;-fx-text-fill:" + TEXT2 + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 12 5 12;-fx-cursor:hand;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:6;");
        manageBtn.setOnAction(e -> showManageCardsDialog());

        HBox allCardsRow = new HBox(12);
        allCardsRow.setAlignment(Pos.CENTER_LEFT);
        allCardsRow.getChildren().addAll(allCardsTitle, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, manageBtn);

        // Mini card grid
        FlowPane miniGrid = new FlowPane(8, 8);
        miniGrid.setPrefWrapLength(700);
        for (int i = 0; i < activeDeck.cards.size(); i++) {
            String[] c = activeDeck.cards.get(i);
            final int ci = i;
            VBox miniCard = new VBox(4);
            miniCard.setPadding(new Insets(10, 12, 10, 12));
            miniCard.setPrefWidth(160);
            miniCard.setStyle(ci == activeCardIndex
                ? "-fx-background-color:rgba(108,142,245,0.14);-fx-border-color:" + ACCENT + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;"
                : "-fx-background-color:#1a1f2e;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;");
            Label qL = new Label(c[0].length() > 40 ? c[0].substring(0, 40) + "…" : c[0]);
            qL.setStyle("-fx-font-size:11px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
            qL.setWrapText(true);
            Label aL = new Label(c[1].length() > 40 ? c[1].substring(0, 40) + "…" : c[1]);
            aL.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
            aL.setWrapText(true);
            miniCard.getChildren().addAll(qL, aL);
            miniCard.setOnMouseClicked(e -> { activeCardIndex = ci; cardFlipped = false; renderStudyView(); });
            miniGrid.getChildren().add(miniCard);
        }

        progressLbl = new Label("Card " + (activeCardIndex + 1) + " of " + activeDeck.cards.size());
        progressLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

        studyPanel.getChildren().addAll(
            cardCounter,
            cardContainer,
            navRow,
            progressLbl,
            new Separator() {{ setStyle("-fx-background-color:" + BORDER + ";"); }},
            allCardsRow,
            miniGrid
        );
    }

    // ── flip animation ────────────────────────────────────────────────────────
    private void flipCard(String[] card) {
        Timeline timeline = new Timeline();
        // Scale X to 0 (half flip)
        KeyValue kv1 = new KeyValue(cardFace.scaleXProperty(), 0);
        KeyFrame kf1 = new KeyFrame(Duration.millis(150), kv1);
        timeline.getKeyFrames().add(kf1);
        timeline.setOnFinished(e -> {
            cardFlipped = !cardFlipped;
            cardHint.setText(cardFlipped ? "ANSWER" : "QUESTION");
            cardHint.setStyle("-fx-font-size:9px;-fx-font-weight:700;-fx-letter-spacing:1.5px;-fx-font-family:'Segoe UI';-fx-text-fill:" + (cardFlipped ? GREEN : ACCENT) + ";-fx-background-color:" + (cardFlipped ? "rgba(62,207,176,0.1)" : "rgba(108,142,245,0.1)") + ";-fx-background-radius:4;-fx-padding:3 8 3 8;");
            cardText.setText(cardFlipped ? card[1] : card[0]);
            cardFace.setStyle("-fx-background-color:" + (cardFlipped ? "rgba(62,207,176,0.07)" : SURFACE) + ";-fx-border-color:" + (cardFlipped ? "rgba(62,207,176,0.3)" : "rgba(108,142,245,0.3)") + ";-fx-border-width:1;-fx-border-radius:16;-fx-background-radius:16;-fx-cursor:hand;");
            // Scale X back to 1
            Timeline tl2 = new Timeline(new KeyFrame(Duration.millis(150), new KeyValue(cardFace.scaleXProperty(), 1)));
            tl2.play();
        });
        timeline.play();
    }

    // ── New Deck dialog ───────────────────────────────────────────────────────
    private void showNewDeckDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("New Deck");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(360);

        Label heading = new Label("Create New Deck");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        Label nameL = new Label("Deck Name");
        nameL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextField nameField = styledTextField("e.g. Biology Chapter 5");

        Label subL = new Label("Subject");
        subL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextField subField = styledTextField("e.g. Biology");

        Label errL = new Label("");
        errL.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:11px;");

        Button createBtn = new Button("Create Deck");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:10 0 10 0;-fx-cursor:hand;");
        createBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String sub  = subField.getText().trim();
            if (name.isEmpty()) { errL.setText("Please enter a deck name."); return; }
            Deck deck = new Deck(name, sub.isEmpty() ? "General" : sub);
            decks.add(deck);
            activeDeck = deck;
            activeCardIndex = 0;
            cardFlipped = false;
            refreshDeckList();
            renderStudyView();
            dialog.close();
        });

        form.getChildren().addAll(heading, nameL, nameField, subL, subField, errL, createBtn);
        dialog.setScene(new Scene(form));
        dialog.show();
    }

    // ── Edit Deck dialog ──────────────────────────────────────────────────────
    private void showEditDeckDialog() {
        if (activeDeck == null) return;
        Stage dialog = new Stage();
        dialog.setTitle("Edit Deck");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(360);

        Label heading = new Label("Edit Deck");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        Label nameL = new Label("Deck Name");
        nameL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextField nameField = styledTextField(activeDeck.name);
        nameField.setText(activeDeck.name);

        Label subL = new Label("Subject");
        subL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextField subField = styledTextField(activeDeck.subject);
        subField.setText(activeDeck.subject);

        Button saveBtn = new Button("Save Changes");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:10 0 10 0;-fx-cursor:hand;");
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;
            activeDeck.name = name;
            activeDeck.subject = subField.getText().trim().isEmpty() ? "General" : subField.getText().trim();
            refreshDeckList();
            renderStudyView();
            dialog.close();
        });

        form.getChildren().addAll(heading, nameL, nameField, subL, subField, saveBtn);
        dialog.setScene(new Scene(form));
        dialog.show();
    }

    // ── Add Card dialog ───────────────────────────────────────────────────────
    private void showAddCardDialog() {
        if (activeDeck == null) return;
        Stage dialog = new Stage();
        dialog.setTitle("Add Flashcard");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(400);

        Label heading = new Label("Add New Card");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        Label frontL = new Label("Front (Question / Term)");
        frontL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextArea frontArea = styledTextArea();
        frontArea.setPromptText("e.g. What is mitosis?");
        frontArea.setPrefRowCount(3);

        Label backL = new Label("Back (Answer / Definition)");
        backL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextArea backArea = styledTextArea();
        backArea.setPromptText("e.g. Cell division producing two identical daughter cells.");
        backArea.setPrefRowCount(3);

        Label errL = new Label("");
        errL.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:11px;");

        HBox btnRow = new HBox(10);
        Button addMoreBtn = new Button("Save & Add Another");
        addMoreBtn.setStyle("-fx-background-color:#1a1f2e;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:9 14 9 14;-fx-cursor:hand;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;");
        Button saveBtn = new Button("Save Card");
        saveBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:9 18 9 18;-fx-cursor:hand;");
        HBox.setHgrow(saveBtn, Priority.ALWAYS);
        btnRow.getChildren().addAll(addMoreBtn, saveBtn);

        addMoreBtn.setOnAction(e -> {
            String front = frontArea.getText().trim();
            String back  = backArea.getText().trim();
            if (front.isEmpty() || back.isEmpty()) { errL.setText("Both sides are required."); return; }
            activeDeck.cards.add(new String[]{front, back});
            frontArea.clear(); backArea.clear(); errL.setText("");
            activeCardIndex = activeDeck.cards.size() - 1;
            cardFlipped = false;
            refreshDeckList();
            renderStudyView();
        });

        saveBtn.setOnAction(e -> {
            String front = frontArea.getText().trim();
            String back  = backArea.getText().trim();
            if (front.isEmpty() || back.isEmpty()) { errL.setText("Both sides are required."); return; }
            activeDeck.cards.add(new String[]{front, back});
            activeCardIndex = activeDeck.cards.size() - 1;
            cardFlipped = false;
            refreshDeckList();
            renderStudyView();
            dialog.close();
        });

        form.getChildren().addAll(heading, frontL, frontArea, backL, backArea, errL, btnRow);
        dialog.setScene(new Scene(form));
        dialog.show();
    }

    // ── Manage Cards dialog ───────────────────────────────────────────────────
    private void showManageCardsDialog() {
        if (activeDeck == null) return;
        Stage dialog = new Stage();
        dialog.setTitle("Manage Cards — " + activeDeck.name);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(520);
        dialog.setHeight(500);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:" + SURFACE + ";");

        HBox header = new HBox();
        header.setPadding(new Insets(18, 20, 14, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");
        Label heading = new Label("Manage Cards");
        heading.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button addBtn = new Button("+ Add Card");
        addBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;");
        addBtn.setOnAction(e -> { dialog.close(); showAddCardDialog(); });
        header.getChildren().addAll(heading, sp, addBtn);

        VBox list = new VBox(6);
        list.setPadding(new Insets(14));
        refreshManageList(list, dialog);

        ScrollPane scroll = new ScrollPane(list);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + SURFACE + ";");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);
        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private void refreshManageList(VBox list, Stage dialog) {
        list.getChildren().clear();
        if (activeDeck.cards.isEmpty()) {
            Label empty = new Label("No cards yet.");
            empty.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
            list.getChildren().add(empty);
            return;
        }
        for (int i = 0; i < activeDeck.cards.size(); i++) {
            String[] card = activeDeck.cards.get(i);
            final int ci = i;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 12, 10, 12));
            row.setStyle("-fx-background-color:#1a1f2e;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");

            VBox cardInfo = new VBox(3);
            HBox.setHgrow(cardInfo, Priority.ALWAYS);
            Label frontL = new Label("Q: " + (card[0].length() > 55 ? card[0].substring(0, 55) + "…" : card[0]));
            frontL.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
            Label backL = new Label("A: " + (card[1].length() > 55 ? card[1].substring(0, 55) + "…" : card[1]));
            backL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
            cardInfo.getChildren().addAll(frontL, backL);

            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-background-color:#252a3a;-fx-text-fill:#ffffff;-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-background-radius:5;-fx-padding:4 10 4 10;-fx-cursor:hand;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:5;");
            editBtn.setOnAction(e -> { dialog.close(); showEditCardDialog(ci); });

            Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:" + ROSE + ";-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-background-radius:5;-fx-padding:4 8 4 8;-fx-cursor:hand;");
            delBtn.setOnAction(e -> {
                activeDeck.cards.remove(ci);
                if (activeCardIndex >= activeDeck.cards.size()) activeCardIndex = Math.max(0, activeDeck.cards.size() - 1);
                refreshManageList(list, dialog);
                refreshDeckList();
                renderStudyView();
            });

            row.getChildren().addAll(cardInfo, editBtn, delBtn);
            list.getChildren().add(row);
        }
    }

    // ── Edit Card dialog ──────────────────────────────────────────────────────
    private void showEditCardDialog(int cardIndex) {
        if (activeDeck == null || cardIndex >= activeDeck.cards.size()) return;
        String[] card = activeDeck.cards.get(cardIndex);

        Stage dialog = new Stage();
        dialog.setTitle("Edit Card");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(400);

        Label heading = new Label("Edit Card");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        Label frontL = new Label("Front (Question / Term)");
        frontL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextArea frontArea = styledTextArea();
        frontArea.setText(card[0]);
        frontArea.setPrefRowCount(3);

        Label backL = new Label("Back (Answer / Definition)");
        backL.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        TextArea backArea = styledTextArea();
        backArea.setText(card[1]);
        backArea.setPrefRowCount(3);

        Button saveBtn = new Button("Save Changes");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:10 0 10 0;-fx-cursor:hand;");
        saveBtn.setOnAction(e -> {
            String front = frontArea.getText().trim();
            String back  = backArea.getText().trim();
            if (front.isEmpty() || back.isEmpty()) return;
            card[0] = front; card[1] = back;
            cardFlipped = false;
            renderStudyView();
            dialog.close();
        });

        form.getChildren().addAll(heading, frontL, frontArea, backL, backArea, saveBtn);
        dialog.setScene(new Scene(form));
        dialog.show();
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:#1a1f2e;-fx-text-fill:#ffffff;-fx-prompt-text-fill:" + TEXT3 + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:9 12 9 12;");
        return tf;
    }

    private TextArea styledTextArea() {
        TextArea ta = new TextArea();
        ta.setStyle("-fx-background-color:#1a1f2e;-fx-text-fill:#ffffff;-fx-prompt-text-fill:" + TEXT3 + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:9 12 9 12;-fx-control-inner-background:#1a1f2e;");
        ta.setWrapText(true);
        return ta;
    }

    private Label navSection(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:10 10 6 10;");
        return l;
    }

    private Label navItem(String text, boolean active) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(9, 12, 9, 12));
        l.setStyle(active
            ? "-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-font-weight:600;-fx-background-radius:8;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;"
            : "-fx-background-color:transparent;-fx-text-fill:" + TEXT2 + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-border-color:transparent;-fx-border-width:1;-fx-border-radius:8;"
        );
        return l;
    }

    public Scene getScene() { return scene; }
}
