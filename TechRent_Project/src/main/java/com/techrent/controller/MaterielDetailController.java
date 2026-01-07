package com.techrent.controller;

import com.techrent.dao.CategorieDAO;
import com.techrent.dao.MaterielDAO;
import com.techrent.model.Categorie;
import com.techrent.model.Materiel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class MaterielDetailController {

    @FXML private TextField txtRef;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private ComboBox<String> comboEtat;
    @FXML private ComboBox<Categorie> comboCategorie;
    @FXML private ImageView imageProduit;

    private Materiel materielActuel;
    private String cheminImageTemp;

    private final MaterielDAO materielDAO = new MaterielDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();
    private MaterielController materielController;

    @FXML
    public void initialize() {

        List<Categorie> cats = categorieDAO.findAll();
        comboCategorie.setItems(FXCollections.observableArrayList(cats));

        comboEtat.setItems(FXCollections.observableArrayList(
                "DISPONIBLE", "EN LOCATION", "EN PANNE", "EN MAINTENANCE", "INDISPONIBLE"
        ));
    }


    public void setMateriel(Materiel materiel, MaterielController mainCtrl) {
        this.materielActuel = materiel;
        this.materielController = mainCtrl;
        this.cheminImageTemp = materiel.getCheminImage();


        txtRef.setText(materiel.getReference());
        txtNom.setText(materiel.getNom());
        txtPrix.setText(String.valueOf(materiel.getPrixParJour()));
        comboEtat.setValue(materiel.getEtat());
        comboCategorie.setValue(materiel.getCategorie());
        comboCategorie.getEditor().setText(materiel.getCategorie().getLibelle());


        if (materiel.getCheminImage() != null) {
            try {
                imageProduit.setImage(new Image("file:" + materiel.getCheminImage()));
            } catch (Exception e) {
                System.out.println("Image introuvable");
            }
        }
    }

    @FXML
    private void handleChangerImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cheminImageTemp = file.getAbsolutePath();
            imageProduit.setImage(new Image("file:" + cheminImageTemp));
        }
    }

    @FXML
    private void handleEnregistrer() {
        try {
            materielActuel.setReference(txtRef.getText());
            materielActuel.setNom(txtNom.getText());
            materielActuel.setPrixParJour(Double.parseDouble(txtPrix.getText()));
            materielActuel.setEtat(comboEtat.getValue());
            materielActuel.setCheminImage(cheminImageTemp);


            String nomCat = comboCategorie.getEditor().getText();
            Categorie catFinale = null;
            for (Categorie c : comboCategorie.getItems()) {
                if (c.getLibelle().equalsIgnoreCase(nomCat)) {
                    catFinale = c; break;
                }
            }
            if (catFinale == null) {
                catFinale = new Categorie();
                catFinale.setLibelle(nomCat);
            }
            materielActuel.setCategorie(catFinale);

            // Sauvegarde en BDD
            materielDAO.update(materielActuel);


            materielController.rafraichirTableau();
            fermerFenetre();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void handleSupprimer() {
        // 1. Demander confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce produit ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {

                materielDAO.delete(materielActuel);


                materielController.rafraichirTableau();
                fermerFenetre();

            } catch (Exception e) {

                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur Suppression");
                errorAlert.setHeaderText("Impossible de supprimer ce matériel");

                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }

    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) txtRef.getScene().getWindow();
        stage.close();
    }
}