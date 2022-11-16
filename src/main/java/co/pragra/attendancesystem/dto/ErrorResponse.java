package co.pragra.attendancesystem.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
public class ErrorResponse {
    private int errorCode;
    private String appId;
    private Date dateTime;
    private String message;
}
