package com.techrent.dao;

import com.techrent.model.Location;
import com.techrent.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class LocationDAO {

    public void save(Location location) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(location);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void update(Location location) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(location);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<Location> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.materiel", Location.class).list();
        }
    }

    public boolean isMaterielLoue(Long materielId, LocalDate debutDemande, LocalDate finDemande) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = "SELECT count(l) FROM Location l WHERE l.materiel.id = :matId " +
                    "AND l.dateRetourReelle IS NULL " +
                    "AND l.dateDebut <= :finDemande " +
                    "AND l.dateFinPrevue >= :debutDemande";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("matId", materielId);
            query.setParameter("finDemande", finDemande);
            query.setParameter("debutDemande", debutDemande);

            Long count = query.uniqueResult();
            return count > 0;
        }
    }
}