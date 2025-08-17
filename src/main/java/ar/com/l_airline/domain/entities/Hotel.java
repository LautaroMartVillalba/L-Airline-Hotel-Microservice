package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    @NotBlank
    @Size(min = 2, max = 40)
    private String name;
    @NotNull
    private double stars;
    @NotNull
    @Min(2)
    @Column(name = "total_rooms")
    private int totalRooms;
    @NotNull
    @Column(name = "free_rooms")
    private int freeRooms;
    @NotNull
    @Column(name = "reserved_rooms")
    private int reservedRooms;
    @NotNull
    @Size(min = 9, max = 12)
    @Column(unique = true, name = "contact_phone")
    private String contactPhone;
    @NotNull
    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms = new ArrayList<>();
    @NotNull
    @OneToMany(mappedBy = "hotel")
    private List<Benefit> benefits = new ArrayList<>();
    @NotNull
    @OneToMany(mappedBy = "hotel")
    private List <Attraction> attractions = new ArrayList<>();

}
