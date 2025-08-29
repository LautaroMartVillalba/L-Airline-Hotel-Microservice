package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.enums.RoomBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import ar.com.l_airline.domain.entities.RoomBookingPeriod;

/**
 * Data Transfer Object for {@link RoomBookingPeriod}.
 * <p>
 * Used to transfer room booking period data between layers without exposing the entity directly.
 * Contains booking period dates, status, and references to the associated room and reservation.
 * </p>
 */
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
