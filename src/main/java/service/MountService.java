package service;

import entity.Mount;
import repository.MountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MountService {

    @Autowired
    private MountRepository mountRepository;

    public Mount save(Mount mount) {
        return mountRepository.save(mount);
    }

    public List<Mount> getAll() {
        return mountRepository.findAll();
    }

    public Mount getById(int id) {
        return mountRepository.findById(id).orElse(null);
    }

    public void delete(Mount mount) {
        mountRepository.delete(mount);
    }
}
