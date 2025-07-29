package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
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
    private String name;
    private String description;
    private int peopleCapacity;
    private LocalTime openAt;
    private LocalTime closeAt;
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel;


}
