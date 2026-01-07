package com.techrent.controller;

import com.techrent.model.Client;
import com.techrent.service.ClientService; // <-- On importe le Service
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ClientDetailController {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtSociete;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTel;
    @FXML private TextField txtVille;
    @FXML private ComboBox<String> comboStatut;

    private Client clientActuel;
    private ClientController mainController;


    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        comboStatut.setItems(FXCollections.observableArrayList(
                "ACTIF", "NOUVEAU", "EN ATTENTE", "SUSPENDU"
        ));
    }

    public void setClient(Client client, ClientController controller) {
        this.clientActuel = client;
        this.mainController = controller;

        if (client != null) {
            txtNom.setText(client.getNom());
            txtPrenom.setText(client.getPrenom());
            txtSociete.setText(client.getSociete());
            txtEmail.setText(client.getEmail());
            txtTel.setText(client.getTelephone());
            txtVille.setText(client.getVille());
            comboStatut.setValue(client.getStatut());
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (isInputValid()) {
            try {
                clientActuel.setNom(txtNom.getText());
                clientActuel.setPrenom(txtPrenom.getText());
                clientActuel.setSociete(txtSociete.getText());
                clientActuel.setEmail(txtEmail.getText());
                clientActuel.setTelephone(txtTel.getText());
                clientActuel.setVille(txtVille.getText());
                clientActuel.setStatut(comboStatut.getValue());


                clientService.update(clientActuel);

                if (mainController != null) {
                    mainController.handleRefresh();
                }
                fermerFenetre();

            } catch (Exception e) {
                afficherAlerte("Erreur", "Impossible de modifier le client : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSupprimer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer définitivement ce client ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {

                clientService.delete(clientActuel);

                if (mainController != null) {
                    mainController.handleRefresh();
                }
                fermerFenetre();
            } catch (Exception e) {
                afficherAlerte("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (txtNom.getText() == null || txtNom.getText().length() == 0) errorMessage += "Nom invalide!\n";
        if (txtPrenom.getText() == null || txtPrenom.getText().length() == 0) errorMessage += "Prénom invalide!\n";

        if (errorMessage.length() == 0) {
            return true;
        } else {
            afficherAlerte("Champs Invalides", errorMessage);
            return false;
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}