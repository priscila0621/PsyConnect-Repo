package ni.edu.uam.psyconnect_backend.service;

import ni.edu.uam.psyconnect_backend.dto.LoginRequest;
import ni.edu.uam.psyconnect_backend.dto.LoginResponse;
import ni.edu.uam.psyconnect_backend.model.User;
import ni.edu.uam.psyconnect_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(
                    "El correo ya está registrado"
            );
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException(
                    "El nombre de usuario ya está en uso"
            );
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        String loginValue =
                request.getEmail();

        User user =
                userRepository
                        .findByEmail(loginValue)
                        .orElse(
                                userRepository
                                        .findByUsername(loginValue)
                                        .orElse(null)
                        );

        if (user == null) {

            return new LoginResponse(
                    false,
                    "Usuario no encontrado",
                    null,
                    null
            );
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            return new LoginResponse(
                    false,
                    "Contraseña incorrecta",
                    null,
                    null
            );
        }

        return new LoginResponse(
                true,
                "Inicio de sesión exitoso",
                user.getId(),
                user.getName()
        );
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    public User getUserByUsername(
            String username
    ) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Usuario no encontrado"
                        )
                );
    }
    public User updateUser(
            Long id,
            User updatedUser
    ) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        if (
                !user.getUsername().equals(
                        updatedUser.getUsername()
                )
                        &&
                        userRepository.existsByUsername(
                                updatedUser.getUsername()
                        )
        ) {

            throw new RuntimeException(
                    "El nombre de usuario ya está en uso"
            );
        }

        user.setName(
                updatedUser.getName()
        );

        user.setUsername(
                updatedUser.getUsername()
        );

        user.setEmail(
                updatedUser.getEmail()
        );

        user.setBirthdate(
                updatedUser.getBirthdate()
        );

        user.setDescription(
                updatedUser.getDescription()
        );

        user.setProfileImage(
                updatedUser.getProfileImage()
        );

        return userRepository.save(
                user
        );
    }

    public void resetPassword(
            String email,
            String newPassword
    ) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }

    public void sendRecoveryCode(
            String email
    ) {

        if (!userRepository.existsByEmail(email)) {

            throw new RuntimeException(
                    "No existe una cuenta asociada a este correo"
            );
        }
    }

    public boolean existsByEmail(
            String email
    ) {

        return userRepository
                .existsByEmail(email);
    }

    public void changePassword(
            Long userId,
            String currentPassword,
            String newPassword
    ) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Usuario no encontrado"
                                        )
                        );

        if (
                !passwordEncoder.matches(
                        currentPassword,
                        user.getPassword()
                )
        ) {

            throw new RuntimeException(
                    "Contraseña actual incorrecta"
            );
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(
                user
        );
    }

    public void changeEmail(
            Long userId,
            String newEmail
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado"
                                )
                        );

        if (
                userRepository.existsByEmail(
                        newEmail
                )
                        &&
                        !user.getEmail().equals(
                                newEmail
                        )
        ) {

            throw new RuntimeException(
                    "El correo ya está registrado"
            );
        }

        user.setEmail(
                newEmail
        );

        userRepository.save(
                user
        );
    }

    public boolean existsByUsername(
            String username
    ) {

        return userRepository
                .existsByUsername(
                        username
                );
    }

}