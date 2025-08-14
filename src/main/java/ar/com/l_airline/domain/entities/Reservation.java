package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
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
    private int numberOfPeople;
    private int numberOfNights;
    private LocalDate startAt;
    private LocalDate endAt;
    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person client;
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room roomBooked;
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private RoomBookingPeriod roomBookingPeriod;

}
