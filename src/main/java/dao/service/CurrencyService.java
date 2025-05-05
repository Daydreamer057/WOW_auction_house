package dao.service;

import dao.entity.Currency;
import dao.entity.CurrencyId;
import dao.repository.CurrencyRepository;
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
