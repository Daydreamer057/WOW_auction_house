package service;

import dto.SelectDTO;
import entity.Currency;
import entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.CurrencyRepository;
import repository.ItemRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void saveAll(List<Item> items) {
        itemRepository.saveAll(items);
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(int id) {
        return itemRepository.findById(id);
    }

    public void delete(Item item) {
        itemRepository.delete(item);
    }

    public List<SelectDTO> calculateProfitForItem() {
        List<Currency> currencies = currencyRepository.findAll();
        List<SelectDTO> profits = new ArrayList<>();

        if (currencies.isEmpty()) {
            return null;
        }

        for(Currency currency : currencies) {
            Long minPrice = currencies.stream()
                    .map(Currency::getCost)
                    .min(Long::compare)
                    .orElse(0L);

            Long maxPrice = currencies.stream()
                    .map(Currency::getCost)
                    .max(Long::compare)
                    .orElse(0L);

            if(maxPrice - minPrice > 100000000) {
                SelectDTO selectDTO = new SelectDTO();
                selectDTO.setItemId(currency.getItem().getId());
                selectDTO.setName(currency.getItem().getEnGb());
                selectDTO.setProfit(maxPrice - minPrice);
                profits.add(selectDTO);
            }

        }

        profits.sort(Comparator.comparingLong(SelectDTO::getProfit).reversed());

        return profits;
    }
}
