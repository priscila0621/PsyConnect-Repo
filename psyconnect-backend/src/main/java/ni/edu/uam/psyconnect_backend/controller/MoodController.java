package ni.edu.uam.psyconnect_backend.controller;

import ni.edu.uam.psyconnect_backend.model.Mood;
import ni.edu.uam.psyconnect_backend.service.AchievementService;
import ni.edu.uam.psyconnect_backend.service.MoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/moods")
@CrossOrigin("*")
public class MoodController {

    private final MoodService moodService;
    private final AchievementService achievementService;

    public MoodController(
            MoodService moodService,
            AchievementService achievementService
    ) {
        this.moodService = moodService;
        this.achievementService = achievementService;
    }

    @PostMapping
    public Mood saveMood(
            @RequestBody Mood mood
    ) {

        Mood savedMood =
                moodService.saveMood(
                        mood
                );

        achievementService.unlock(
                mood.getUserId(),
                "😊 Conectado Contigo",
                "Registraste tu primer estado emocional"
        );

        return savedMood;
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<Boolean> hasMoodToday(
            @PathVariable Long userId
    ) {

        Optional<Mood> mood =
                moodService.getTodayMood(
                        userId
                );

        return ResponseEntity.ok(
                mood.isPresent()
        );
    }

    @GetMapping("/user/{userId}")
    public List<Mood> getMoodHistory(
            @PathVariable Long userId
    ) {
        return moodService.getMoodHistory(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMood(@PathVariable Long id) {
        moodService.deleteMood(id);
        return ResponseEntity.noContent().build();
    }
}