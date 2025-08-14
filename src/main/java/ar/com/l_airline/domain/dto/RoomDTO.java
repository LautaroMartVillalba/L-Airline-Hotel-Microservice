package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.enums.BedsType;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.domain.enums.RoomType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    @Enumerated(EnumType.STRING)
    private RoomState state;
    private Long hotelId;
    private List<Long> reservationId;
    private List<Long> roomBookingPeriodId;

}
