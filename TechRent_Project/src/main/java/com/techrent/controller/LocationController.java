package com.techrent.controller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.techrent.model.Client;
import com.techrent.model.Location;
import com.techrent.model.Materiel;
import com.techrent.service.ClientService;
import com.techrent.service.LocationService;
import com.techrent.service.MaterielService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class LocationController {

    @FXML private BorderPane mainLayout;
    @FXML private StackPane calendarContainer;

    @FXML private ComboBox<Client> comboClient;
    @FXML private ComboBox<Materiel> comboMateriel;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;

    private CalendarView calendarView;
    private Calendar locationsCalendar;

    private final LocationService locationService = new LocationService();
    private final ClientService clientService = new ClientService();
    private final MaterielService materielService = new MaterielService();

    private ObservableList<Client> listeClients = FXCollections.observableArrayList();
    private ObservableList<Materiel> listeMateriels = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupCalendar();
        setupFormulaire();
        loadLocationsIntoCalendar();
    }

    private void setupFormulaire() {
        listeClients.setAll(clientService.findAll());
        listeMateriels.setAll(materielService.findAll());

        setupDisplayConverters();
        setupAutocompleteClient();
        setupAutocompleteMateriel();

        dateDebut.setValue(LocalDate.now());
        dateFin.setValue(LocalDate.now().plusDays(1));
    }

    private void setupDisplayConverters() {
        comboClient.setConverter(new StringConverter<Client>() {
            @Override public String toString(Client c) { return c == null ? "" : c.getNom() + " " + c.getPrenom(); }
            @Override public Client fromString(String s) { return null; }
        });

        comboMateriel.setConverter(new StringConverter<Materiel>() {
            @Override public String toString(Materiel m) { return m == null ? "" : m.getNom() + " (" + m.getReference() + ")"; }
            @Override public Materiel fromString(String s) { return null; }
        });
    }

    // Auto-complétion Client
    private void setupAutocompleteClient() {
        comboClient.setEditable(true);
        FilteredList<Client> filteredItems = new FilteredList<>(listeClients, p -> true);
        comboClient.setItems(filteredItems);

        comboClient.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (comboClient.getSelectionModel().getSelectedItem() != null
                        && comboClient.getEditor().getText().equals(comboClient.getConverter().toString(comboClient.getSelectionModel().getSelectedItem()))) {
                    return;
                }
                filteredItems.setPredicate(client -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lowerVal = newValue.toLowerCase();
                    return client.getNom().toLowerCase().contains(lowerVal) || client.getPrenom().toLowerCase().contains(lowerVal);
                });
                if (!filteredItems.isEmpty() && !comboClient.isShowing()) {
                    comboClient.show();
                }
            });
        });
    }

    // Auto-complétion Matériel
    private void setupAutocompleteMateriel() {
        comboMateriel.setEditable(true);
        FilteredList<Materiel> filteredItems = new FilteredList<>(listeMateriels, p -> true);
        comboMateriel.setItems(filteredItems);

        comboMateriel.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (comboMateriel.getSelectionModel().getSelectedItem() != null
                        && comboMateriel.getEditor().getText().equals(comboMateriel.getConverter().toString(comboMateriel.getSelectionModel().getSelectedItem()))) {
                    return;
                }
                filteredItems.setPredicate(materiel -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String lowerVal = newValue.toLowerCase();
                    return materiel.getNom().toLowerCase().contains(lowerVal) || materiel.getReference().toLowerCase().contains(lowerVal);
                });
                if (!filteredItems.isEmpty() && !comboMateriel.isShowing()) {
                    comboMateriel.show();
                }
            });
        });
    }

    @FXML
    private void handleNouvelleLocation() {
        try {
            Client client = comboClient.getValue();
            Materiel materiel = comboMateriel.getValue();
            LocalDate debut = dateDebut.getValue();
            LocalDate fin = dateFin.getValue();

            // 1. Vérification des champs
            if (client == null || materiel == null || debut == null || fin == null) {
                afficherAlerte("Erreur", "Veuillez sélectionner un client et un matériel valides.");
                return;
            }

            // 2. Vérification cohérence dates
            if (!fin.isAfter(debut) && !fin.isEqual(debut)) {
                afficherAlerte("Erreur Date", "La date de fin doit être postérieure à la date de début.");
                return;
            }

            // 3. Vérification disponibilité (Conflit)
            if (locationService.isMaterielLoue(materiel.getId(), debut, fin)) {
                afficherAlerte("Conflit", "Le matériel '" + materiel.getNom() + "' est déjà réservé sur cette période !");
                return;
            }

            long nbJours = ChronoUnit.DAYS.between(debut, fin);
            if (nbJours == 0) nbJours = 1;
            double prixEstime = nbJours * materiel.getPrixParJour();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            initOwner(confirm);
            confirm.setTitle("Validation Location");
            confirm.setHeaderText("Confirmer la location ?");
            confirm.setContentText(
                    "Client : " + client.getNom() + " " + client.getPrenom() + "\n" +
                            "Matériel : " + materiel.getNom() + "\n" +
                            "Durée : " + nbJours + " jours\n\n" +
                            "Total estimé : " + prixEstime + " MAD"
            );

            Optional<ButtonType> resultat = confirm.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                Location loc = new Location();
                loc.setClient(client);
                loc.setMateriel(materiel);
                loc.setDateDebut(debut);
                loc.setDateFinPrevue(fin);

                locationService.createLocation(loc);

                loadLocationsIntoCalendar();
                afficherInfo("Succès", "Location enregistrée !");

                // Reset simple
                comboClient.setValue(null); comboClient.getEditor().clear();
                comboMateriel.setValue(null); comboMateriel.getEditor().clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur Technique", e.getMessage());
        }
    }

    private void setupCalendar() {
        calendarView = new CalendarView();

        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(true);
        calendarView.setShowSearchField(false);
        calendarView.setShowSourceTray(false);
        calendarView.setEntryFactory(param -> null);
        calendarView.showMonthPage();

        locationsCalendar = new Calendar("Locations");
        locationsCalendar.setStyle(Calendar.Style.STYLE1);
        locationsCalendar.setReadOnly(true);

        CalendarSource source = new CalendarSource("TechRent");
        source.getCalendars().add(locationsCalendar);
        calendarView.getCalendarSources().setAll(source);

        Platform.runLater(() -> calendarContainer.getChildren().add(calendarView));


        calendarView.setEntryContextMenuCallback(param -> {
            ContextMenu menu = new ContextMenu();
            MenuItem itemRetour = new MenuItem("Terminer / Retour");

            Entry<?> entry = param.getEntry();
            if (entry.getUserObject() instanceof Location) {
                Location loc = (Location) entry.getUserObject();
                // Si déjà retourné, on désactive le bouton
                if (loc.getDateRetourReelle() != null) {
                    itemRetour.setDisable(true);
                    itemRetour.setText("Déjà terminé");
                }
                itemRetour.setOnAction(e -> handleRetourLocation(loc));
                menu.getItems().add(itemRetour);
            }
            return menu;
        });
    }

    private void loadLocationsIntoCalendar() {
        locationsCalendar.clear();
        List<Location> locations = locationService.findAll();

        for (Location loc : locations) {
            Entry<Location> entry = new Entry<>(loc.getMateriel().getNom() + " (" + loc.getClient().getNom() + ")");

            entry.setInterval(loc.getDateDebut(), LocalTime.of(9, 0), loc.getDateFinPrevue(), LocalTime.of(18, 0));
            entry.setUserObject(loc);

            if (loc.getDateRetourReelle() != null) {
                entry.getStyleClass().add("termine"); // CSS class si besoin
                entry.setTitle("TERMINE - " + entry.getTitle());
            }
            locationsCalendar.addEntry(entry);
        }
    }

    private void handleRetourLocation(Location loc) {
        Dialog<LocalDate> dialog = new Dialog<>();
        initOwner(dialog);
        dialog.setTitle("Retour Matériel");
        dialog.setHeaderText("Date de retour effective ?");

        DatePicker dateRetourPicker = new DatePicker(LocalDate.now());
        dialog.getDialogPane().setContent(dateRetourPicker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(b -> b == ButtonType.OK ? dateRetourPicker.getValue() : null);

        Optional<LocalDate> dateRetourOpt = dialog.showAndWait();
        if (dateRetourOpt.isPresent()) {
            LocalDate retourReel = dateRetourOpt.get();

            long joursPrevus = ChronoUnit.DAYS.between(loc.getDateDebut(), loc.getDateFinPrevue());
            if(joursPrevus == 0) joursPrevus = 1;

            long joursRetard = 0;
            if (retourReel.isAfter(loc.getDateFinPrevue())) {
                joursRetard = ChronoUnit.DAYS.between(loc.getDateFinPrevue(), retourReel);
            }

            double prixBase = joursPrevus * loc.getMateriel().getPrixParJour();
            double penalite = joursRetard * (loc.getMateriel().getPrixParJour() * 2); // Pénalité double
            double totalFinal = prixBase + penalite;

            Alert recap = new Alert(Alert.AlertType.CONFIRMATION);
            initOwner(recap);
            recap.setTitle("Clôture");
            recap.setHeaderText("Bilan du retour");
            recap.setContentText("Total à payer : " + totalFinal + " MAD (Dont pénalité : " + penalite + ")");

            Optional<ButtonType> validation = recap.showAndWait();
            if (validation.isPresent() && validation.get() == ButtonType.OK) {
                loc.setDateRetourReelle(retourReel);
                loc.setPrixTotal(totalFinal);

                locationService.cloturerLocation(loc);

                loadLocationsIntoCalendar();
                afficherInfo("Terminé", "Location clôturée.");
            }
        }
    }


    @FXML private void handleAfficherMateriels() { changerVue("/com/techrent/view/MaterielView.fxml"); }
    @FXML private void handleAfficherClients() { changerVue("/com/techrent/view/ClientView.fxml"); }
    @FXML private void handleAfficherAccueil() { changerVue("/com/techrent/view/AccueilView.fxml"); }
    @FXML private void handleAfficherLocations() { loadLocationsIntoCalendar(); }
    @FXML private void handleActualiser() { loadLocationsIntoCalendar(); }


    @FXML private void handleAfficherPannes() { changerVue("/com/techrent/view/MaintenanceView.fxml"); }

    private void changerVue(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) mainLayout.getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur Navigation", "Impossible de charger la vue : " + fxmlPath);
        }
    }


    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        initOwner(alert);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        initOwner(alert);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initOwner(Dialog<?> dialog) {
        if (mainLayout != null && mainLayout.getScene() != null) {
            Window window = mainLayout.getScene().getWindow();
            if (window != null) {
                dialog.initOwner(window);
            }
        }
    }


    public void preselectionnerClient(Client clientCible) {
        if (clientCible == null) return;
        for (Client c : comboClient.getItems()) {
            boolean match = (c.getId() != null && c.getId().equals(clientCible.getId()));
            if (!match && c.getId() == null) match = c.getNom().equals(clientCible.getNom());
            if (match) {
                comboClient.setValue(c);
                Platform.runLater(() -> comboClient.getEditor().setText(c.getNom() + " " + c.getPrenom()));
                break;
            }
        }
    }


    public void preselectionnerMateriel(Materiel materielCible) {
        if (materielCible == null) return;
        for (Materiel m : comboMateriel.getItems()) {
            boolean match = (m.getId() != null && m.getId().equals(materielCible.getId()));
            if (!match && m.getId() == null) match = m.getReference().equals(materielCible.getReference());
            if (match) {
                comboMateriel.setValue(m);
                Platform.runLater(() -> comboMateriel.getEditor().setText(m.getNom() + " (" + m.getReference() + ")"));
                break;
            }
        }
    }
}