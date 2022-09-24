package ru.kudinov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kudinov.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
}