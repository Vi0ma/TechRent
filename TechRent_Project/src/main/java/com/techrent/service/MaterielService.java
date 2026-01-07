package com.techrent.service;

import com.techrent.dao.CategorieDAO;
import com.techrent.dao.MaterielDAO;
import com.techrent.model.Categorie;
import com.techrent.model.Materiel;

import java.util.List;

public class MaterielService {

    private final MaterielDAO materielDAO = new MaterielDAO();
    private final CategorieDAO categorieDAO = new CategorieDAO();


    public List<Materiel> findAll() {
        return materielDAO.findAll();
    }

    public void save(Materiel materiel) {

        materielDAO.save(materiel);
    }

    public void update(Materiel materiel) {
        materielDAO.update(materiel);
    }

    public void delete(Materiel materiel) {
        materielDAO.delete(materiel);
    }


    public List<Categorie> findAllCategories() {
        return categorieDAO.findAll();
    }
}