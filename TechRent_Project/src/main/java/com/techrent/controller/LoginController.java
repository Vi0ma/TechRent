package com.techrent.controller;

import com.techrent.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private StackPane rootPane;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void handleLogin() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        if (authService.authentifier(user, pass)) {
            ouvrirApplication();
        } else {
            lblError.setText("Identifiant ou mot de passe incorrect.");
            lblError.setVisible(true);
            shakeStage();
        }
    }

    private void ouvrirApplication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/techrent/view/AccueilView.fxml"));
            Parent root = loader.load();

            Stage mainStage = new Stage();
            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.setTitle("TechRent");

            mainStage.setMaximized(true);
            mainStage.show();

            Stage loginStage = (Stage) txtUsername.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Erreur critique : " + e.getMessage());
            lblError.setVisible(true);
        }
    }

    private void shakeStage() {
        txtUsername.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        txtPassword.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }
}