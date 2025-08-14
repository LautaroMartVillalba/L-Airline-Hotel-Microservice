package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.enums.RoomBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RoomBookingPeriodDTO {
    private Long id;
    private LocalDate startAt;
    private LocalDate endAt;
    private RoomBookingStatus status;
    private Long roomId;
    private Long reservationId;
}
