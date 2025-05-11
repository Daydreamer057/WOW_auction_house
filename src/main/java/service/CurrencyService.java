package service;

import entity.Currency;
import entity.CurrencyId;
import repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    public Currency update(Currency currency) {
        if (currencyRepository.existsById(currency.getId())) {
            return currencyRepository.save(currency); // ✅ this performs update
        }
        throw new RuntimeException("Currency with ID " + currency.getId() + " does not exist.");
    }

    public List<Currency> getAll() {
        return currencyRepository.findAll();
    }

    public Currency getById(CurrencyId id) {
        return currencyRepository.findById(id).orElse(null);
    }

    public void delete(Currency currency) {
        currencyRepository.delete(currency);
    }

}
