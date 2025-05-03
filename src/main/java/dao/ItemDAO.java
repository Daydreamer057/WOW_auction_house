package dao;

import entity.Item;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.*;
import util.HibernateUtil;

import java.util.List;

public class ItemDAO {

    public void save(Item item) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(item);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Item findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Item> cq = cb.createQuery(Item.class);
            Root<Item> root = cq.from(Item.class);
            cq.select(root).where(cb.equal(root.get("id"), id));
            return session.createQuery(cq).uniqueResult();
        }
    }

    public List<Item> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Item> cq = cb.createQuery(Item.class);
            cq.from(Item.class);
            return session.createQuery(cq).getResultList();
        }
    }

    public void delete(Item item) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(item);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
