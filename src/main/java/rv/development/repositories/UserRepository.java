package rv.development.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rv.development.entities.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameAndActivated(String username, boolean activated);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
