package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByIdIn(List<Long> ids, Pageable pageable);

    List<User> findAllByOrderByRatingAsc(Pageable pageable);

    List<User> findAllByOrderByRatingDesc(Pageable pageable);
}