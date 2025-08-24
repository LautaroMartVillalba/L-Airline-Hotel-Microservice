package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a reservation entity in the system.
 * <p>
 * This entity stores information about a client's reservation, including the
 * number of people, number of nights, reservation dates, the client who made
 * the reservation, and the room that was booked.
 * <p>
 * Mapped to the database table "entity_reservation".
 */
@Entity
@Table(name = "entity_reservation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "client")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Min(1)
    @Max(value = 4, message = "")
    @Column(name = "number_of_people")
    private Long numberOfPeople;
    @NotNull
    @Min(1)
    @Column(name = "number_of_nights")
    private Long numberOfNights;
    @NotNull
    @FutureOrPresent
    @Column(name = "start_at")
    private LocalDate startAt;
    @NotNull
    @Future
    @Column(name = "end_at")
    private LocalDate endAt;
    @NotNull
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @NotNull
    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person client;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room roomBooked;

}
