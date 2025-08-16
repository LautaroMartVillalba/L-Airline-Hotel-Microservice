package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

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
    private Long numberOfPeople;
    @NotNull
    @Min(1)
    private Long numberOfNights;
    @NotNull
    @FutureOrPresent
    private LocalDate startAt;
    @NotNull
    @Future
    private LocalDate endAt;
    @NotNull
    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person client;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room roomBooked;
    @NotNull
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private RoomBookingPeriod roomBookingPeriod;

}
