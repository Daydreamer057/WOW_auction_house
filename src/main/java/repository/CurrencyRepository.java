package repository;

import entity.Currency;
import entity.CurrencyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, CurrencyId> {
    List<Currency> findAll();                      // Get all currency entries

    @Query("SELECT c FROM Currency c WHERE c.item.id = :itemId")
    List<Currency> findByItemId(@Param("itemId") int itemId);
}
