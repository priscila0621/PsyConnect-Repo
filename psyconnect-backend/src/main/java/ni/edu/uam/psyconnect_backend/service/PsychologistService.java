package ni.edu.uam.psyconnect_backend.service;

import ni.edu.uam.psyconnect_backend.model.Psychologist;
import ni.edu.uam.psyconnect_backend.repository.PsychologistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PsychologistService {

    private final PsychologistRepository repository;

    public PsychologistService(
            PsychologistRepository repository
    ) {
        this.repository = repository;
    }

    public List<Psychologist> getAll() {
        return repository.findAll();
    }

    public Psychologist save(
            Psychologist psychologist
    ) {
        return repository.save(psychologist);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Psychologist findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}