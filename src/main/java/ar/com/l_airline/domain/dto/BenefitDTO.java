package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.entities.Hotel;
import lombok.*;

import java.time.LocalTime;

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
    private Hotel hotel;
}
