package com.ecole;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.ecole.dao.*;
import com.ecole.model.*;
import java.util.List;

public class App extends Application {

    private final AlerteEmotionnelleDao alerteDao = DaoFactory.getAlerteEmotionnelleDao();
    private final InterventionDao interventionDao = DaoFactory.getInterventionDao();
    private final UtilisateurDao utilisateurDao = DaoFactory.getUtilisateurDao();

    private BorderPane mainLayout;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Emotional Guard V2 - Desktop Console");

        mainLayout = new BorderPane();
        mainLayout.setTop(createHeader());
        mainLayout.setLeft(createSidebar());

        showDashboard(); // Default view

        Scene scene = new Scene(mainLayout, 1100, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ToolBar createHeader() {
        Label logo = new Label("ECO-EMOTION GUARD");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label("Session: Administrateur (Connecté)");
        statusLabel.setStyle("-fx-font-style: italic;");

        return new ToolBar(logo, spacer, statusLabel);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Button btnDash = createSidebarButton("Tableau de bord");
        Button btnAlerts = createSidebarButton("Gestion Alertes");
        Button btnInter = createSidebarButton("Interventions");
        Button btnUsers = createSidebarButton("Utilisateurs");

        btnDash.setOnAction(e -> showDashboard());
        btnAlerts.setOnAction(e -> showAlerts());
        btnInter.setOnAction(e -> showInterventions());

        sidebar.getChildren().addAll(btnDash, btnAlerts, btnInter, btnUsers);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: center-left; -fx-font-size: 14px;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: center-left; -fx-font-size: 14px;"));
        return btn;
    }

    private void showDashboard() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));

        Label title = new Label("Vue d'ensemble");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
                createStatCard("ÉLÈVES", String.valueOf(utilisateurDao.count()), "#3498db"),
                createStatCard("ALERTES", String.valueOf(alerteDao.count()), "#e74c3c"),
                createStatCard("ACTIVES", String.valueOf(interventionDao.countByStatut("En cours")), "#2ecc71"));

        container.getChildren().addAll(title, statsRow);
        mainLayout.setCenter(container);
    }

    private void showAlerts() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));

        Label title = new Label("Liste des Alertes Émotionnelles");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TableView<AlerteEmotionnelle> table = new TableView<>();

        TableColumn<AlerteEmotionnelle, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<AlerteEmotionnelle, String> emotionCol = new TableColumn<>("Émotion");
        emotionCol.setCellValueFactory(new PropertyValueFactory<>("emotionDetectee"));

        TableColumn<AlerteEmotionnelle, Gravite> graviteCol = new TableColumn<>("Gravité");
        graviteCol.setCellValueFactory(new PropertyValueFactory<>("gravite"));

        table.getColumns().addAll(idCol, emotionCol, graviteCol);
        table.getItems().addAll(alerteDao.findAll());

        container.getChildren().addAll(title, table);
        mainLayout.setCenter(container);
    }

    private void showInterventions() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));

        Label title = new Label("Interventions en cours");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TableView<Intervention> table = new TableView<>();

        TableColumn<Intervention, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Intervention, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        table.getColumns().addAll(descCol, statutCol);
        table.getItems().addAll(interventionDao.findAll());

        container.getChildren().addAll(title, table);
        mainLayout.setCenter(container);
    }

    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setPrefSize(250, 120);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + color
                + "; -fx-border-width: 0 0 5 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: bold;");

        card.getChildren().addAll(lblValue, lblLabel);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
