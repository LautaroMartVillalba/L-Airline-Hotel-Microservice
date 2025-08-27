package ar.com.l_airline.domain.dto;

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
    private String contactPhone;
    private List<Long> roomsId;
    private List<Long> benefitsId;
    private List<Long> attractionsId;
    private String ubication;
    private String address;
}