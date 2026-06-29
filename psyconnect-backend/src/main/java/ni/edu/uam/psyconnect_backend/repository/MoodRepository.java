package ni.edu.uam.psyconnect_backend.repository;

import ni.edu.uam.psyconnect_backend.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface MoodRepository
        extends JpaRepository<Mood, Long> {

    Optional<Mood> findByUserIdAndDate(
            Long userId,
            String date
    );

    List<Mood> findAllByUserIdOrderByDateAsc(
            Long userId
    );
}