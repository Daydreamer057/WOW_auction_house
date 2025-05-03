package dao;


import entity.Realm;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.*;
import util.HibernateUtil;

import java.util.List;

public class RealmDAO {

    public void save(entity.Realm realm) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(realm);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw e;
        }
    }

    public Realm findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Realm> cq = cb.createQuery(Realm.class);
            Root<Realm> root = cq.from(Realm.class);
            cq.select(root).where(cb.equal(root.get("id"), id));
            return session.createQuery(cq).uniqueResult();
        }
    }

    public List<Realm> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Realm> cq = cb.createQuery(Realm.class);
            cq.from(Realm.class);
            return session.createQuery(cq).getResultList();
        }
    }

    public void delete(Realm realm) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(realm);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
