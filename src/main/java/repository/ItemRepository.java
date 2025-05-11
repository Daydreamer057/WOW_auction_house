package repository;

import entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAll();
    Optional<Item> findById(int id);
    Optional<Item> findByName(String name);
    Optional<Item> findByNameIgnoreCase(String name);
    List<Item> findByNameContainingIgnoreCase(String name);

}
