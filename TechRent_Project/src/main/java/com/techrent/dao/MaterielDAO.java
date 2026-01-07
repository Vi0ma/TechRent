package com.techrent.dao;

import com.techrent.model.Materiel;
import com.techrent.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class MaterielDAO {

    public void save(Materiel materiel) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();


            session.merge(materiel);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public List<Materiel> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Materiel m JOIN FETCH m.categorie", Materiel.class).list();
        }
    }

    public void update(Materiel materiel) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(materiel);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Materiel materiel) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();


            Materiel matInDb = session.get(Materiel.class, materiel.getId());

            if (matInDb != null) {
                session.remove(matInDb);
            } else {
                System.out.println("Impossible de supprimer : l'objet n'existe plus en base.");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();

            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
