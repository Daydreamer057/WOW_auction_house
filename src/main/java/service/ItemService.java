package service;

import entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

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
}
