package ni.edu.uam.psyconnect_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "psychologists")
public class Psychologist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String specialty;

    private String phone;

    private String city;

    private String email;

    @Column(length = 2000)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String photo;

    public Psychologist() {
    }

    public Psychologist(
            Long id,
            String name,
            String specialty,
            String phone,
            String city,
            String email,
            String description,
            String photo
    ) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.phone = phone;
        this.city = city;
        this.email = email;
        this.description = description;
        this.photo = photo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}