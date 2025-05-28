package ru.practicum.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.rating.model.Rating;
import ru.practicum.rating.model.RatingType;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query(value = "SELECT COUNT(DISTINCT user_id) FROM events_rating " +
            "WHERE event_id = ?1 AND rating = ?2", nativeQuery = true)
    Long countRatingByEventIdAndRating(Long eventId, String ratingType);

    @Query(value = "SELECT COUNT(DISTINCT er.user_id) FROM events_rating er " +
            "JOIN events e ON e.id = er.event_id " +
            "WHERE e.user_id = ?1 AND er.rating = ?2", nativeQuery = true)
    Long countRatingByUserIdAndRating(Long userId, String ratingType);

    List<Rating> findAllByEventIdAndUserIdAndRating(Long eventId, Long userId, RatingType ratingType);

    List<Rating> findAllByEventIdAndUserId(Long eventId, Long userId);

    List<Rating> findAllByEventId(Long eventId);

    void deleteByIdIn(List<Long> ids);

    @Query(value = """
        SELECT SUM(CASE
                     WHEN rating = 'LIKE' THEN 1
                     WHEN rating = 'DISLIKE' THEN -1
                     ELSE 0
                   END)
        FROM events_rating
        WHERE event_id = :eventId
        """, nativeQuery = true)
    Integer getEventRating(@Param("eventId") Long eventId);

    @Query(value = """
        SELECT SUM(CASE
                     WHEN er.rating = 'LIKE' THEN 1
                     WHEN er.rating = 'DISLIKE' THEN -1
                     ELSE 0
                   END)
        FROM events_rating er
        JOIN events e ON er.event_id = e.id
        WHERE e.user_id = :userId
        """, nativeQuery = true)
    Integer getUserRating(@Param("userId") Long userId);

    @Query(value = """
        SELECT * FROM events_rating
        WHERE user_id = :userId AND event_id = :eventId
        LIMIT 1
        """, nativeQuery = true)
    Optional<Rating> findByUserAndEvent(@Param("userId") Long userId,
                                        @Param("eventId") Long eventId);
}