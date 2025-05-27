package service;

import entity.Currency;
import entity.CurrencyId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CurrencyRepository;
import repository.ItemRepository;
import repository.RealmRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RealmRepository realmRepository;

    @Transactional
    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

//    @Transactional
//    public void insertOrUpdateCurrency(Currency newCurrency) {
//        CurrencyId id = newCurrency.getId();
//
//        Currency existing = currencyRepository.findById(id).orElse(null);
//
//        if (existing != null) {
//            // Update only the necessary fields
//            existing.setCost(newCurrency.getCost());
//            // Hibernate will detect and update on commit
//        } else {
//            // Ensure you attach managed Item/Realm
//            newCurrency.setItem(itemRepository.findById(newCurrency.getItem().getId()).orElseThrow());
//            newCurrency.setRealm(realmRepository.findById(newCurrency.getRealm().getId()).orElseThrow());
//            currencyRepository.save(newCurrency);
//        }
//    }


    @Transactional
    public void saveAll(List<Currency> currencies) {
        List<Currency> toSave = new ArrayList<>();

        for (Currency currency : currencies) {
            if (!currencyRepository.existsById(currency.getId())) {
                toSave.add(currency);
            } else {
                // Optional: update existing value
                Currency existing = currencyRepository.findById(currency.getId()).orElse(null);
                if (existing != null) {
                    existing.setCost(currency.getCost());
                    currencyRepository.save(existing);
                }
            }
        }

        if (!toSave.isEmpty()) {
            currencyRepository.saveAll(toSave);
        }
    }

    public List<Currency> getAll() {
        return currencyRepository.findAll();
    }

    public List<Currency> findByItemId(int itemId) {
        return currencyRepository.findByItemId(itemId);
    }


    public Currency getById(CurrencyId id) {
        return currencyRepository.findById(id).orElse(null);
    }

    public void delete(Currency currency) {
        currencyRepository.delete(currency);
    }

}
