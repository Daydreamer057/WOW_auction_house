package repository;

import entity.Mount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MountRepository extends JpaRepository<Mount, Integer> {
    List<Mount> findAll();       // Get all realms
}
