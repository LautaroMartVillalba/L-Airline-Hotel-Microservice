package ar.com.l_airline.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationDTO {
    private Long id;
    private Long numberOfPeople;
    private Long numberOfNights;
    private LocalDate startAt;
    private LocalDate endAt;
    private BigDecimal totalPrice;
    private Long personId;
    private Long roomBookedId;
}
