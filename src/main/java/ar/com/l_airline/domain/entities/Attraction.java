package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Time;
import java.time.LocalTime;

/**
 * Entity representing an attraction that belongs to a hotel.
 * Contains information about the attraction's name, description,
 * operating hours, capacity, and the associated hotel.
 */
@Entity
@Table(name = "entity_attraction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "hotel")
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 4, max = 20)
    private String name;
    @NotNull
    @Size(min = 50, max = 500)
    private String description;
    @NotNull
    private int peopleCapacity;
    @NotNull
    private LocalTime openAt;
    @NotNull
    private LocalTime closeAt;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel;


}
