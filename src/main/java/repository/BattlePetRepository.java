package repository;

import entity.BattlePet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BattlePetRepository extends JpaRepository<BattlePet, Integer> {
    List<BattlePet> findAll();       // Get all realms
    BattlePet findById(int id);      // Get realm by ID
}
