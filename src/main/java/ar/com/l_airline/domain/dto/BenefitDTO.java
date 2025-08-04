package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.entities.Hotel;
import lombok.*;

import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for the {@link ar.com.l_airline.domain.entities.Benefit} entity.
 * Used to transfer benefit data across layers without exposing the full entity.
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
