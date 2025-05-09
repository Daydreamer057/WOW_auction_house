package service;

import entity.BattlePet;
import repository.BattlePetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BattlePetService {

    @Autowired
    private BattlePetRepository battlePetRepository;

    public BattlePet save(BattlePet battlePet) {
        return battlePetRepository.save(battlePet);
    }

    public List<BattlePet> getAll() {
        return battlePetRepository.findAll();
    }

    public BattlePet getById(int id) {
        return battlePetRepository.findById(id).orElse(null);
    }

    public void delete(BattlePet battlePet) {
        battlePetRepository.delete(battlePet);
    }
}
