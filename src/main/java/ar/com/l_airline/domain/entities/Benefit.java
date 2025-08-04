package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Entity representing a benefit (or service) offered by a hotel.
 * Contains metadata such as name, description, and operational time range.
 */
@Entity
@Table(name = "entity_service")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "hotel")
public class Benefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalTime openAt;
    private LocalTime closeAt;
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel;

}
