package com.techrent.controller;

import com.techrent.model.Location;
import com.techrent.model.Materiel;
import com.techrent.service.ClientService;
import com.techrent.service.LocationService;
import com.techrent.service.MaterielService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AccueilController {

    @FXML private BorderPane mainLayout;

    @FXML private Label lblLocationsEnCours;
    @FXML private Label lblRetards;
    @FXML private Label lblMaterielsDispo;
    @FXML private Label lblRevenuEstime;
    @FXML private PieChart pieChartEtat;
    @FXML private ListView<String> listRetoursUrgents;

    private final LocationService locationService = new LocationService();
    private final MaterielService materielService = new MaterielService();
    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        chargerStatistiques();
        chargerGraphique();
        chargerRetoursUrgents();
    }

    private void chargerStatistiques() {
        List<Location> locations = locationService.findAll();
        List<Materiel> materiels = materielService.findAll();

        long enCours = locations.stream().filter(l -> l.getDateRetourReelle() == null).count();
        lblLocationsEnCours.setText(String.valueOf(enCours));

        long retards = locations.stream()
                .filter(l -> l.getDateRetourReelle() == null && l.getDateFinPrevue().isBefore(LocalDate.now()))
                .count();
        lblRetards.setText(String.valueOf(retards));

        long dispo = materiels.stream().filter(m -> "DISPONIBLE".equalsIgnoreCase(m.getEtat())).count();
        lblMaterielsDispo.setText(String.valueOf(dispo));

        double revenu = locations.stream()
                .filter(l -> l.getDateRetourReelle() == null)
                .mapToDouble(l -> l.getMateriel().getPrixParJour())
                .sum();
        lblRevenuEstime.setText(String.format("%.0f MAD", revenu));
    }


    private void chargerGraphique() {
        List<Materiel> materiels = materielService.findAll();

        long loue = materiels.stream().filter(m -> "EN LOCATION".equalsIgnoreCase(m.getEtat())).count();
        long dispo = materiels.stream().filter(m -> "DISPONIBLE".equalsIgnoreCase(m.getEtat())).count();
        long panne = materiels.stream().filter(m -> "EN PANNE".equalsIgnoreCase(m.getEtat()) || "EN MAINTENANCE".equalsIgnoreCase(m.getEtat())).count();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();


        pieData.add(new PieChart.Data("Loué", loue));
        pieData.add(new PieChart.Data("Disponible", dispo));
        pieData.add(new PieChart.Data("Maintenance", panne));

        pieChartEtat.setData(pieData);
        pieChartEtat.setLabelsVisible(false);
        pieChartEtat.setLegendVisible(true);


        pieChartEtat.setStyle(
                "CHART_COLOR_1: #f6d365; " +
                        "CHART_COLOR_2: #43e97b; " +
                        "CHART_COLOR_3: #ff0844;"
        );
    }

    private void chargerRetoursUrgents() {
        List<Location> locations = locationService.findAll();
        LocalDate aujourdhui = LocalDate.now();

        List<String> urgences = locations.stream()
                .filter(l -> l.getDateRetourReelle() == null)
                .filter(l -> l.getDateFinPrevue().isEqual(aujourdhui) || l.getDateFinPrevue().isBefore(aujourdhui))
                .map(l -> {
                    String status = l.getDateFinPrevue().isBefore(aujourdhui) ? "RETARD" : "AUJOURD'HUI";
                    return status + " : " + l.getMateriel().getNom() + " (" + l.getClient().getNom() + ")";
                })
                .collect(Collectors.toList());

        listRetoursUrgents.getItems().clear();
        if (urgences.isEmpty()) {
            listRetoursUrgents.getItems().add("Aucun retour urgent.");
        } else {
            listRetoursUrgents.getItems().addAll(urgences);
        }
    }

    // --- NAVIGATION ---
    @FXML private void handleAfficherAccueil() {  }
    @FXML private void handleAfficherMateriels() { changerVue("/com/techrent/view/MaterielView.fxml"); }
    @FXML private void handleAfficherClients() { changerVue("/com/techrent/view/ClientView.fxml"); }
    @FXML private void handleAfficherLocations() { changerVue("/com/techrent/view/LocationView.fxml"); }
    @FXML private void handleAfficherPannes() { changerVue("/com/techrent/view/MaintenanceView.fxml"); }

    private void changerVue(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (Exception e) { e.printStackTrace(); }
    }
}