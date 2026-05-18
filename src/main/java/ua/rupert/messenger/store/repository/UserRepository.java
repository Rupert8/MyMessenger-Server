package ua.rupert.messenger.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.rupert.messenger.store.entities.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByUsername(String username);

    List<Users> findAllByUsernameStartingWithIgnoreCase(String username);
}
