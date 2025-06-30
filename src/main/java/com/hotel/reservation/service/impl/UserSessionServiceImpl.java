package com.hotel.reservation.service.impl;

import com.hotel.reservation.repository.UserSessionRepository;
import com.hotel.reservation.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    @Override
    public Map<String, Long> getWeeklyUserLogins(LocalDateTime startOfWeek) {
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        List<Object[]> results = userSessionRepository.findUniqueLoginsPerDayOfWeek(startOfWeek, endOfWeek);

        Map<String, Long> loginCounts = new HashMap<>();
        for (Object[] result : results) {
            String dayName = ((String) result[0]).toUpperCase().trim();
            Long count = (Long) result[1];
            loginCounts.put(dayName, count);
        }

        Map<String, Long> weeklyLogins = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            weeklyLogins.put(day.name(), loginCounts.getOrDefault(day.name(), 0L));
        }

        return weeklyLogins;
    }
}
