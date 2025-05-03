package dao;

import entity.Currency;
import entity.CurrencyId;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class CurrencyDAO {

    public void save(Currency currency) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(currency);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Currency findById(CurrencyId id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Currency.class, id);
        }
    }

    public List<Currency> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Currency> cq = cb.createQuery(Currency.class);
            cq.from(Currency.class);
            return session.createQuery(cq).getResultList();
        }
    }

    public void delete(Currency currency) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(currency);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
