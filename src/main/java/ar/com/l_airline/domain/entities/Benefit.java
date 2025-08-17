package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    @Size(min = 4, max = 20)
    private String name;
    @NotNull
    @Size(min = 50, max = 500)
    private String description;
    @NotNull
    private LocalTime openAt;
    @NotNull
    private LocalTime closeAt;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel;

}
