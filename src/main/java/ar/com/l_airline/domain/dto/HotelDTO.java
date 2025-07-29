package ar.com.l_airline.domain.dto;

import ar.com.l_airline.domain.entities.Attraction;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.domain.entities.Benefit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class HotelDTO {
    private Long id;
    private String name;
    private double stars;
    private int totalRooms;
    private int freeRooms;
    private int reservedRooms;
    private List<Long> roomsId;
    private List<Long> benefitsId;
    private List<Long> attractionsId;
}