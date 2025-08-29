package ar.com.l_airline.domain.dto;

import lombok.*;

import java.time.LocalTime;
import ar.com.l_airline.domain.entities.Benefit;

/**
 * Data Transfer Object for {@link Benefit}.
 * <p>
 * Used to transfer benefit (service) data between layers without exposing the entity directly.
 * Contains basic benefit fields and reference to the associated hotel.
 * </p>
 */
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BenefitDTO {

    private Long id;
    private String name;
    private String description;
    private LocalTime openAt;
    private LocalTime closeAt;
    private Long hotelId;
}
