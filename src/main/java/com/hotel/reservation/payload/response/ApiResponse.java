package com.hotel.reservation.payload.response;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@Data
public class ApiResponse<T> {

    private String responseMessage;

    private T responseData;

    private String timestamp;

    private boolean success;

    public ApiResponse(String message, T data) {
        this.responseMessage = message;
        this.responseData = data;
        this.timestamp = convertDateToFormat();
        this.success = true;
    }

    public ApiResponse() {
        this.responseMessage = "success";
        this.timestamp = convertDateToFormat();
        this.success = true;
    }

    public ApiResponse(String message, String detail, List<T> emptyList) {
        this.responseMessage = message;
        this.responseData = (T) emptyList;  // Casting List<T> to the correct type
        this.timestamp = convertDateToFormat();
        this.success = false;
    }


    private String convertDateToFormat() {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern(dateFormat);
        String formattedDate = localDateTime.format(simpleDateFormat);
        System.out.println(formattedDate);
        return formattedDate;
    }

}
