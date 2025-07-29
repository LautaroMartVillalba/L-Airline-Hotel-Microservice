package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.entities.Attraction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for the {@link Attraction} entity.
 * Used to transfer attraction data between application layers without exposing the entity directly.
 */
@AllArgsConstructor
@Builder
@Data
public class AttractionDTO {

    private Long id;
    private String name;
    private String description;
    private int peopleCapacity;
    private LocalTime openAt;
    private LocalTime closeAt;
    private Long hotelId;

}
