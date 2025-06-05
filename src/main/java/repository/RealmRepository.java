package repository;

import entity.Realm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealmRepository extends JpaRepository<Realm, Integer> {
    List<Realm> findAll();       // Get all realms

    @Query(value = """
    SELECT * FROM realm r 
    WHERE r.id IN (
        SELECT MIN(id) 
        FROM realm 
        GROUP BY connected_realm_id
    )
""", nativeQuery = true)
    List<Realm> findDistinctConnectedRealms();

    Realm getByConnectedRealmId(int ConnectedRealmId);

}
