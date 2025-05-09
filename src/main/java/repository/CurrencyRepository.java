package repository;

import entity.Currency;
import entity.CurrencyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, CurrencyId> {
    List<Currency> findAll();                      // Get all currency entries
    Optional<Currency> findById(CurrencyId id);              // Get by composite key
}
