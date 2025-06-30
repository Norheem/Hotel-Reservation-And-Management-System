package com.hotel.reservation.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface UserSessionService {

    public Map<String, Long> getWeeklyUserLogins(LocalDateTime startOfWeek);
}
