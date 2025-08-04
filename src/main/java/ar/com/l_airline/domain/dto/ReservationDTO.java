package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.Room;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationDTO {
    private Long id;
    private int numberOfPeople;
    private int numberOfNights;
    private LocalDate startAt;
    private LocalDate endAt;
    private Long personId;
    private Long roomBookedId;
}
