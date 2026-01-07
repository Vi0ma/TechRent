package com.techrent.controller;

import com.techrent.model.Client;
import com.techrent.service.ClientService;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;

public class ClientController {

    @FXML private BorderPane mainLayout;

    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colPrenom;
    @FXML private TableColumn<Client, String> colSociete;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colTel;
    @FXML private TableColumn<Client, String> colVille;
    @FXML private TableColumn<Client, String> colStatut;

    @FXML private Label lblTotalClients;
    @FXML private Label lblNouveaux;
    @FXML private Label lblActifs;
    @FXML private Label lblAttente;
    @FXML private Label lblSuspendus;

    @FXML private TextField txtRecherche;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtSociete;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTel;
    @FXML private TextField txtVille;
    @FXML private ComboBox<String> comboStatut;

    private final ClientService clientService = new ClientService();
    private ObservableList<Client> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupStatutColoring();

        comboStatut.setItems(FXCollections.observableArrayList("ACTIF", "NOUVEAU", "EN ATTENTE", "SUSPENDU"));
        comboStatut.getSelectionModel().select("ACTIF");

        loadClients();
        setupDoubleClickAndContextMenu();
        setupRecherche();
    }

    private void setupTableColumns() {

        TableColumn<Client, Void> colAction = new TableColumn<>("Action");
        colAction.setPrefWidth(80);

        Callback<TableColumn<Client, Void>, TableCell<Client, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Client, Void> call(final TableColumn<Client, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Louer");

                    {
                        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            Client client = getTableView().getItems().get(getIndex());
                            handleLouerPourClient(client);
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


        tableClients.getColumns().add(0, colAction);


        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colSociete.setCellValueFactory(new PropertyValueFactory<>("societe"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }

    private void setupStatutColoring() {
        colStatut.setCellFactory(column -> new TableCell<Client, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "ACTIF": setStyle("-fx-text-fill: #43e97b; -fx-font-weight: bold;"); break;
                        case "NOUVEAU": setStyle("-fx-text-fill: #a18cd1; -fx-font-weight: bold;"); break;
                        case "EN ATTENTE": setStyle("-fx-text-fill: #f6d365; -fx-font-weight: bold;"); break;
                        case "SUSPENDU": setStyle("-fx-text-fill: #ff0844; -fx-font-weight: bold;"); break;
                        default: setStyle("-fx-text-fill: white;"); break;
                    }
                }
            }
        });
    }

    private void loadClients() {
        List<Client> liste = clientService.findAll();
        masterData.setAll(liste);
        tableClients.setItems(masterData);
        calculerStatistiques();
        setupRecherche();
    }

    private void calculerStatistiques() {
        int total = masterData.size();
        long nouveaux = masterData.stream().filter(c -> "NOUVEAU".equals(c.getStatut())).count();
        long actifs = masterData.stream().filter(c -> "ACTIF".equals(c.getStatut())).count();
        long attente = masterData.stream().filter(c -> "EN ATTENTE".equals(c.getStatut())).count();
        long suspendus = masterData.stream().filter(c -> "SUSPENDU".equals(c.getStatut())).count();

        lblTotalClients.setText(String.valueOf(total));
        lblNouveaux.setText(String.valueOf(nouveaux));
        lblActifs.setText(String.valueOf(actifs));
        lblAttente.setText(String.valueOf(attente));
        lblSuspendus.setText(String.valueOf(suspendus));
    }

    private void setupRecherche() {
        FilteredList<Client> filteredData = new FilteredList<>(masterData, p -> true);
        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (client.getNom().toLowerCase().contains(lowerCaseFilter)) return true;
                if (client.getPrenom().toLowerCase().contains(lowerCaseFilter)) return true;
                if (client.getSociete() != null && client.getSociete().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
        SortedList<Client> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableClients.comparatorProperty());
        tableClients.setItems(sortedData);
    }

    @FXML
    public void handleRefresh() {
        loadClients();
        txtRecherche.clear();
    }

    @FXML
    private void handleAjouter() {
        if (txtNom.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            afficherAlerte("Erreur", "Champs obligatoires manquants");
            return;
        }
        try {
            Client c = new Client();
            c.setNom(txtNom.getText()); c.setPrenom(txtPrenom.getText());
            c.setSociete(txtSociete.getText()); c.setEmail(txtEmail.getText());
            c.setTelephone(txtTel.getText()); c.setVille(txtVille.getText());
            c.setStatut(comboStatut.getValue());

            clientService.save(c);

            txtNom.clear(); txtPrenom.clear(); txtSociete.clear();
            txtEmail.clear(); txtTel.clear(); txtVille.clear();
            comboStatut.getSelectionModel().select("ACTIF");

            loadClients();
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur ajout : " + e.getMessage());
        }
    }

    private void setupDoubleClickAndContextMenu() {
        tableClients.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem itemBannir = new MenuItem("Suspendre");
            MenuItem itemActiver = new MenuItem("Activer");
            MenuItem itemVoir = new MenuItem("Voir détail");

            itemBannir.setOnAction(e -> changerStatutRapide(row.getItem(), "SUSPENDU"));
            itemActiver.setOnAction(e -> changerStatutRapide(row.getItem(), "ACTIF"));
            itemVoir.setOnAction(e -> ouvrirFenetreDetail(row.getItem()));

            contextMenu.getItems().addAll(itemVoir, new SeparatorMenuItem(), itemActiver, itemBannir);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(contextMenu)
            );

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    ouvrirFenetreDetail(row.getItem());
                }
            });
            return row ;
        });
    }

    private void changerStatutRapide(Client client, String nouveauStatut) {
        if (client == null) return;
        try {
            client.setStatut(nouveauStatut);
            clientService.update(client);
            loadClients();
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de changer le statut.");
        }
    }

    private void ouvrirFenetreDetail(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/techrent/view/ClientDetailView.fxml"));
            Parent root = loader.load();
            ClientDetailController controller = loader.getController();
            controller.setClient(client, this);
            Stage stage = new Stage();
            stage.setTitle("Fiche Client : " + client.getNomComplet());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Impossible d'ouvrir le détail : " + e.getMessage());
        }
    }

    // --- NAVIGATION LOGIC ---


    private void handleLouerPourClient(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/techrent/view/LocationView.fxml"));
            Parent view = loader.load();


            LocationController controller = loader.getController();
            controller.preselectionnerClient(client);


            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(view);

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur Navigation", "Impossible d'ouvrir la location : " + e.getMessage());
        }
    }

    @FXML private void handleAfficherMateriels() { changerVue("/com/techrent/view/MaterielView.fxml"); }
    @FXML private void handleAfficherClients() {  }
    @FXML private void handleAfficherLocations() { changerVue("/com/techrent/view/LocationView.fxml"); }

    private void changerVue(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Impossible de charger la vue : " + fxmlPath);
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.show();
    }
    @FXML
    private void handleAfficherAccueil() {
        changerVue("/com/techrent/view/AccueilView.fxml");
    }
    @FXML
    private void handleAfficherPannes() {
        changerVue("/com/techrent/view/MaintenanceView.fxml");
    }
}