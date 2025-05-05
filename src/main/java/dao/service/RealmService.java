package dao.service;

import dao.entity.Realm;
import dao.repository.RealmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealmService {

    @Autowired
    private RealmRepository realmRepository;

    public Realm save(Realm realm) {
        return realmRepository.save(realm);
    }

    public List<Realm> getAll() {
        return realmRepository.findAll();
    }

    public Realm getById(int id) {
        return realmRepository.findById(id).orElse(null);
    }

    public void delete(Realm realm) {
        realmRepository.delete(realm);
    }
}
