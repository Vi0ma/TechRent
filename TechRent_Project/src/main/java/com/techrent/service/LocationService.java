package com.techrent.service;

import com.techrent.dao.LocationDAO;
import com.techrent.dao.MaterielDAO;
import com.techrent.model.Location;
import com.techrent.model.Materiel;

import java.time.LocalDate;
import java.util.List;

public class LocationService {

    private final LocationDAO locationDAO = new LocationDAO();
    private final MaterielDAO materielDAO = new MaterielDAO();

    public List<Location> findAll() {
        return locationDAO.findAll();
    }

    public boolean isMaterielLoue(long materielId, LocalDate debut, LocalDate fin) {
        return locationDAO.isMaterielLoue(materielId, debut, fin);
    }

    public void createLocation(Location location) {

        locationDAO.save(location);


        Materiel m = location.getMateriel();
        m.setEtat("EN LOCATION");
        materielDAO.update(m);
    }

    public void cloturerLocation(Location location) {

        locationDAO.update(location);


        Materiel m = location.getMateriel();
        m.setEtat("DISPONIBLE");
        materielDAO.update(m);
    }
}