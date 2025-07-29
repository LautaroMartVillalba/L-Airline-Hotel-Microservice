package ar.com.l_airline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;


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
