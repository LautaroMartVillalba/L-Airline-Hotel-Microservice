package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Hotel entity mapped to the "entity_hotel" table in the database.
 * This entity contains basic hotel data including the number of rooms, star rating,
 * and its associations with rooms, benefits, and attractions.
 */
@Entity
@Table(name = "entity_hotel")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double stars;
    private int totalRooms;
    private int freeRooms;
    private int reservedRooms;
    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms = new ArrayList<>();
    @OneToMany(mappedBy = "hotel")
    private List<Benefit> benefits = new ArrayList<>();
    @OneToMany(mappedBy = "hotel")
    private List <Attraction> attractions = new ArrayList<>();

}
