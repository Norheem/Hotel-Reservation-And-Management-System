package com.hotel.reservation.repository;

import com.hotel.reservation.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findFirstByUserIdOrderByLoginTimeDesc(Long userId);

    void deleteByUserId(Long userId);


    @Query("SELECT TRIM(BOTH ' ' FROM TO_CHAR(us.loginTime, 'Day')) AS dayName, " +
            "COUNT(DISTINCT us.user.id) AS uniqueUsers " +
            "FROM UserSession us " +
            "WHERE us.loginTime >= :startOfWeek AND us.loginTime < :endOfWeek " +
            "GROUP BY TRIM(BOTH ' ' FROM TO_CHAR(us.loginTime, 'Day'))")
    List<Object[]> findUniqueLoginsPerDayOfWeek(LocalDateTime startOfWeek, LocalDateTime endOfWeek);

}
