package ni.edu.uam.psyconnect_backend.service;

import ni.edu.uam.psyconnect_backend.model.Mood;
import ni.edu.uam.psyconnect_backend.repository.MoodRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class MoodService {

    private final MoodRepository moodRepository;

    public MoodService(
            MoodRepository moodRepository
    ) {
        this.moodRepository = moodRepository;
    }

    public Mood saveMood(
            Mood mood
    ) {

        if (mood.getId() != null && mood.getId() == 0L) {
            mood.setId(null);
        }

        if (
                mood.getDate() == null
        ) {

            mood.setDate(
                    LocalDate.now().toString()
            );
        }

        return moodRepository.save(
                mood
        );
    }

    public Optional<Mood> getTodayMood(
            Long userId
    ) {
        return moodRepository.findByUserIdAndDate(
                userId,
                LocalDate.now().toString()
        );
    }

    public List<Mood> getMoodHistory(Long userId) {
        return moodRepository.findAllByUserIdOrderByDateAsc(userId);
    }

    public void deleteMood(Long id) {
        moodRepository.deleteById(id);
    }
}