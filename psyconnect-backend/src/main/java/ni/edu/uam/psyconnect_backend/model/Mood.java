package ni.edu.uam.psyconnect_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "moods")
public class Mood {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private Long userId;

    private String mood;

    private String date;

    @Column(columnDefinition = "TEXT")
    private String reflection;

    private Long timestamp;

    @Column(columnDefinition = "TEXT")
    private String activities;

    public Mood() {
    }

    public Mood(
            Long id,
            Long userId,
            String mood,
            String date,
            String reflection,
            Long timestamp,
            String activities
    ) {
        this.id = id;
        this.userId = userId;
        this.mood = mood;
        this.date = date;
        this.reflection = reflection;
        this.timestamp = timestamp;
        this.activities = activities;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMood() {
        return mood;
    }

    public String getDate() {
        return date;
    }

    public String getReflection() {
        return reflection;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getActivities() {
        return activities;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReflection(String reflection) {
        this.reflection = reflection;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }
}