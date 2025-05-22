package com.learning_platform.auth.repositories;

import com.learning_platform.auth.models.PasswordResetToken;
import com.learning_platform.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // ✅ Find token by the token string (for reset requests)
    Optional<PasswordResetToken> findByToken(String token);

    // ✅ New: Find token by the user (for cleanup before creating a new one)
    Optional<PasswordResetToken> findByUser(User user);
}
