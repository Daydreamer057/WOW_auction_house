package dao.repository;

import dao.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAll();       // Get all items
    Item findById(int id);      // Get item by ID
}
