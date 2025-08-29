package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.enums.BedsType;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.domain.enums.RoomType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import ar.com.l_airline.domain.entities.Room;

/**
 * Data Transfer Object for {@link Room}.
 * <p>
 * Used to transfer room data between layers without exposing the entity directly.
 * Contains basic room information, enumerated types for bed, room type, and state,
 * and references to associated hotel, reservations, and booking periods.
 * </p>
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomDTO{

    private Long id;
    private int floor;
    private int numberOfBeds;
    @Enumerated(EnumType.STRING)
    private BedsType bedType;
    private int peopleCapacity;
    private Long timesBooked;
    private BigDecimal pricePerNight;
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    @Enumerated(EnumType.STRING)
    private RoomState state;
    private Long hotelId;
    private List<Long> reservationId;
    private List<Long> roomBookingPeriodId;

}
