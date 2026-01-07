package com.techrent.controller;

import com.techrent.model.Materiel;
import com.techrent.service.MaterielService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceController {

    @FXML private BorderPane mainLayout;
    @FXML private TableView<Materiel> tableMaintenance;
    @FXML private TableColumn<Materiel, String> colImage;
    @FXML private TableColumn<Materiel, String> colRef;
    @FXML private TableColumn<Materiel, String> colNom;
    @FXML private TableColumn<Materiel, String> colEtat;


    @FXML private Label lblTotalPannes;
    @FXML private Label lblEnMaintenance;
    @FXML private TextField txtRecherche;

    private final MaterielService materielService = new MaterielService();
    private ObservableList<Materiel> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();
        setupRecherche();
    }

    private void setupTableColumns() {
        TableColumn<Materiel, Void> colAction = new TableColumn<>("Décision");
        colAction.setPrefWidth(160);

        Callback<TableColumn<Materiel, Void>, TableCell<Materiel, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Materiel, Void> call(final TableColumn<Materiel, Void> param) {
                return new TableCell<>() {
                    private final Button btnReparer = new Button("Réparé");
                    private final Button btnHS = new Button("Hors Service");
                    private final HBox pane = new HBox(5, btnReparer, btnHS);

                    {
                        btnReparer.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 10px; -fx-cursor: hand;");
                        btnHS.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px; -fx-cursor: hand;");

                        btnReparer.setOnAction(e -> handleReparer(getTableView().getItems().get(getIndex())));
                        btnHS.setOnAction(e -> handleDeclarerHS(getTableView().getItems().get(getIndex())));
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(pane);
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
        tableMaintenance.getColumns().add(0, colAction);

        colRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        colImage.setCellValueFactory(new PropertyValueFactory<>("cheminImage"));
        colImage.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String chemin, boolean empty) {
                super.updateItem(chemin, empty);
                if (empty || chemin == null) setGraphic(null);
                else {
                    try {
                        imageView.setImage(new Image("file:" + chemin, 40, 40, true, true));
                        setGraphic(imageView);
                    } catch (Exception e) { setGraphic(null); }
                }
            }
        });

        colEtat.setCellFactory(column -> new TableCell<Materiel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    if (item.equals("EN PANNE")) setStyle("-fx-text-fill: #ff0844; -fx-font-weight: bold;");
                    else if (item.equals("EN MAINTENANCE")) setStyle("-fx-text-fill: #f6d365; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void loadData() {
        List<Materiel> all = materielService.findAll();
        List<Materiel> pannes = all.stream()
                .filter(m -> "EN PANNE".equals(m.getEtat()) || "EN MAINTENANCE".equals(m.getEtat()))
                .collect(Collectors.toList());

        masterData.setAll(pannes);
        tableMaintenance.setItems(masterData);

        long countPanne = pannes.stream().filter(m -> "EN PANNE".equals(m.getEtat())).count();
        long countMaint = pannes.stream().filter(m -> "EN MAINTENANCE".equals(m.getEtat())).count();
        lblTotalPannes.setText(String.valueOf(countPanne));
        lblEnMaintenance.setText(String.valueOf(countMaint));
    }

    private void handleReparer(Materiel m) {
        if(confirmerAction("Réparation", "Remettre " + m.getNom() + " en stock ?")) {
            m.setEtat("DISPONIBLE");
            materielService.update(m);
            loadData();
        }
    }

    private void handleDeclarerHS(Materiel m) {
        if(confirmerAction("Mise au rebut", "Déclarer " + m.getNom() + " définitivement Hors Service ?")) {
            m.setEtat("INDISPONIBLE");
            materielService.update(m);
            loadData();
        }
    }

    private boolean confirmerAction(String titre, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void setupRecherche() {
        FilteredList<Materiel> filteredData = new FilteredList<>(masterData, p -> true);
        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(m -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return m.getNom().toLowerCase().contains(newVal.toLowerCase());
            });
        });
        SortedList<Materiel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableMaintenance.comparatorProperty());
        tableMaintenance.setItems(sortedData);
    }

    @FXML private void handleRefresh() { loadData(); }

    // --- NAVIGATION ---
    @FXML private void handleAfficherAccueil() { changerVue("/com/techrent/view/AccueilView.fxml"); }
    @FXML private void handleAfficherMateriels() { changerVue("/com/techrent/view/MaterielView.fxml"); }
    @FXML private void handleAfficherClients() { changerVue("/com/techrent/view/ClientView.fxml"); }
    @FXML private void handleAfficherLocations() { changerVue("/com/techrent/view/LocationView.fxml"); }
    @FXML private void handleAfficherPannes() { loadData(); }

    private void changerVue(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}