package dao.service;

import dao.entity.Item;
import dao.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item getById(int id) {
        return itemRepository.findById(id).orElse(null);
    }

    public void delete(Item item) {
        itemRepository.delete(item);
    }
}
