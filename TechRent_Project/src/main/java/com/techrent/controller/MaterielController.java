package com.techrent.controller;

import com.techrent.model.Categorie;
import com.techrent.model.Materiel;
import com.techrent.service.MaterielService;
import com.techrent.service.PdfService;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.List;

public class MaterielController {

    @FXML private TableView<Materiel> tableMateriels;
    @FXML private TableColumn<Materiel, String> colImage;
    @FXML private TableColumn<Materiel, String> colRef;
    @FXML private TableColumn<Materiel, String> colNom;
    @FXML private TableColumn<Materiel, String> colCategorie;
    @FXML private TableColumn<Materiel, Double> colPrix;
    @FXML private TableColumn<Materiel, String> colEtat;

    @FXML private TextField txtRecherche;
    private ObservableList<Materiel> masterData = FXCollections.observableArrayList();

    @FXML private TextField txtRef;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private ComboBox<Categorie> comboCategorie;
    @FXML private ComboBox<String> comboEtat;

    @FXML private BorderPane mainLayout;


    @FXML private ImageView previewImage;
    private String cheminImageSelectionnee = null;

    private final MaterielService materielService = new MaterielService();

    @FXML
    public void initialize() {
        setupTableColumns();
        chargerCategories();

        comboEtat.setItems(FXCollections.observableArrayList(
                "DISPONIBLE", "EN LOCATION", "EN PANNE", "EN MAINTENANCE", "INDISPONIBLE"
        ));
        comboEtat.getSelectionModel().select("DISPONIBLE");

        rafraichirTableau();
        setupRecherche();
        setupContextMenu();
    }

    private void setupTableColumns() {

        TableColumn<Materiel, Void> colAction = new TableColumn<>("Action");
        colAction.setPrefWidth(80);

        Callback<TableColumn<Materiel, Void>, TableCell<Materiel, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Materiel, Void> call(final TableColumn<Materiel, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Louer");

                    {
                        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            Materiel materiel = getTableView().getItems().get(getIndex());
                            handleLouerPourMateriel(materiel);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
        tableMateriels.getColumns().add(0, colAction);


        colRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        colCategorie.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategorie().getLibelle())
        );

        colImage.setCellValueFactory(new PropertyValueFactory<>("cheminImage"));
        colImage.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String chemin, boolean empty) {
                super.updateItem(chemin, empty);
                if (empty || chemin == null) {
                    setGraphic(null);
                } else {
                    try {
                        Image img = new Image("file:" + chemin, 50, 50, true, true);
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        colEtat.setCellFactory(column -> new TableCell<Materiel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "DISPONIBLE": setStyle("-fx-text-fill: #43e97b; -fx-font-weight: bold;"); break;
                        case "EN LOCATION": setStyle("-fx-text-fill: #f6d365; -fx-font-weight: bold;"); break;
                        case "EN PANNE": setStyle("-fx-text-fill: #ff0844; -fx-font-weight: bold;"); break;
                        case "EN MAINTENANCE": setStyle("-fx-text-fill: #a18cd1; -fx-font-weight: bold;"); break;
                        default: setStyle("-fx-text-fill: white;");
                    }
                }
            }
        });
    }

    private void handleLouerPourMateriel(Materiel materiel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/techrent/view/LocationView.fxml"));
            Parent view = loader.load();
            LocationController controller = loader.getController();
            controller.preselectionnerMateriel(materiel);
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur Navigation", "Impossible d'ouvrir la location : " + e.getMessage());
        }
    }

    private void setupContextMenu() {
        tableMateriels.setRowFactory(tv -> {
            TableRow<Materiel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem itemDispo = new MenuItem("Rendre Disponible");
            MenuItem itemLoue = new MenuItem("Marquer En Location");
            MenuItem itemPanne = new MenuItem("Signaler Panne");
            MenuItem itemMaintenance = new MenuItem("Mettre en Maintenance");
            SeparatorMenuItem separateur = new SeparatorMenuItem();
            MenuItem itemSupprimer = new MenuItem("Supprimer");
            itemSupprimer.setStyle("-fx-text-fill: red;");

            itemDispo.setOnAction(e -> changerEtatRapide(row.getItem(), "DISPONIBLE"));
            itemLoue.setOnAction(e -> changerEtatRapide(row.getItem(), "EN LOCATION"));
            itemPanne.setOnAction(e -> changerEtatRapide(row.getItem(), "EN PANNE"));
            itemMaintenance.setOnAction(e -> changerEtatRapide(row.getItem(), "EN MAINTENANCE"));
            itemSupprimer.setOnAction(e -> handleSupprimerRapide(row.getItem()));

            contextMenu.getItems().addAll(itemDispo, itemLoue, itemMaintenance, itemPanne, separateur, itemSupprimer);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(contextMenu)
            );

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ouvrirFenetreDetail(row.getItem());
                }
            });
            return row ;
        });
    }

    private void setupRecherche() {
        FilteredList<Materiel> filteredData = new FilteredList<>(masterData, p -> true);
        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(materiel -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (materiel.getNom().toLowerCase().contains(lowerCaseFilter)) return true;
                if (materiel.getReference().toLowerCase().contains(lowerCaseFilter)) return true;
                if (materiel.getCategorie().getLibelle().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        SortedList<Materiel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableMateriels.comparatorProperty());
        tableMateriels.setItems(sortedData);
    }


    public void rafraichirTableau() {
        masterData.setAll(materielService.findAll());
    }

    private void changerEtatRapide(Materiel materiel, String nouvelEtat) {
        if (materiel == null) return;
        try {
            materiel.setEtat(nouvelEtat);
            materielService.update(materiel);
            tableMateriels.refresh();
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de changer l'état : " + e.getMessage());
        }
    }

    private void handleSupprimerRapide(Materiel materiel) {
        if (materiel == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer définitivement " + materiel.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                materielService.delete(materiel);
                rafraichirTableau();
            } catch (Exception e) {
                afficherAlerte("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportPdf() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer l'inventaire en PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
            fileChooser.setInitialFileName("Inventaire_TechRent.pdf");

            Stage stage = (Stage) tableMateriels.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                PdfService pdfService = new PdfService();
                pdfService.genererPdfMateriel(tableMateriels.getItems(), file);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Réussi");
                alert.setHeaderText(null);
                alert.setContentText("Le fichier PDF a été généré avec succès !");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur Export", "Impossible de générer le PDF : " + e.getMessage());
        }
    }

    @FXML private void handleAfficherAccueil() { changerVue("/com/techrent/view/AccueilView.fxml"); }
    @FXML private void handleAfficherMateriels() { rafraichirTableau(); txtRecherche.clear(); }
    @FXML private void handleAfficherLocations() { changerVue("/com/techrent/view/LocationView.fxml"); }
    @FXML private void handleAfficherClients() { changerVue("/com/techrent/view/ClientView.fxml"); }
    @FXML private void handleAfficherPannes() { changerVue("/com/techrent/view/MaintenanceView.fxml"); }

    private void changerVue(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible de charger la vue : " + e.getMessage());
        }
    }

    private void ouvrirFenetreDetail(Materiel materiel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/techrent/view/MaterielDetailView.fxml"));
            Parent root = loader.load();

            MaterielDetailController controller = loader.getController();
            controller.setMateriel(materiel, this);

            Stage stage = new Stage();
            stage.setTitle("Modifier " + materiel.getNom());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible d'ouvrir le détail : " + e.getMessage());
        }
    }

    @FXML
    private void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image produit");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            cheminImageSelectionnee = file.getAbsolutePath();
            previewImage.setImage(new Image("file:" + cheminImageSelectionnee));
        }
    }

    @FXML
    private void handleAjouterMateriel() {
        try {
            String nomCategorie = comboCategorie.getEditor().getText();
            String etatChoisi = comboEtat.getValue();

            if (txtRef.getText().isEmpty() || txtNom.getText().isEmpty()) {
                afficherAlerte("Erreur", "La référence et le nom sont obligatoires !");
                return;
            }

            Materiel mat = new Materiel();
            mat.setReference(txtRef.getText());
            mat.setNom(txtNom.getText());
            mat.setEtat(etatChoisi);
            mat.setCheminImage(cheminImageSelectionnee);

            try {
                mat.setPrixParJour(Double.parseDouble(txtPrix.getText()));
            } catch (NumberFormatException e) {
                afficherAlerte("Erreur", "Prix invalide");
                return;
            }

            Categorie categorieFinale = null;
            for (Categorie c : comboCategorie.getItems()) {
                if (c.getLibelle().equalsIgnoreCase(nomCategorie)) {
                    categorieFinale = c; break;
                }
            }
            if (categorieFinale == null) {
                categorieFinale = new Categorie();
                categorieFinale.setLibelle(nomCategorie);
            }
            mat.setCategorie(categorieFinale);

            materielService.save(mat);

            txtRef.clear();
            txtNom.clear();
            txtPrix.clear();
            previewImage.setImage(null);
            cheminImageSelectionnee = null;

            chargerCategories();
            rafraichirTableau();

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible d'ajouter : " + e.getMessage());
        }
    }

    private void chargerCategories() {
        List<Categorie> lesCategories = materielService.findAllCategories();
        comboCategorie.setItems(FXCollections.observableArrayList(lesCategories));
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}