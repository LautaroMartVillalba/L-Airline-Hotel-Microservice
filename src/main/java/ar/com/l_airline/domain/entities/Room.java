package ar.com.l_airline.domain.entities;

import ar.com.l_airline.domain.enums.BedsType;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.domain.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents a room entity in the hotel domain.
 *
 * <p>This class is mapped to the "entity_room" table in the database. It defines the
 * structure and attributes of a room, including bed configuration,
 * capacity, type, state, and associated hotel.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *   <li>{@code @Entity} marks this class as a JPA entity.</li>
 *   <li>{@code @Table(name = "entity_room")} maps it to the "entity_room" table.</li>
 *   <li>Lombok annotations like {@code @Getter}, {@code @Setter}, {@code @Builder}, etc.,
 *       automatically generate boilerplate code.</li>
 *   <li>{@code @ToString(exclude = "hotel")} avoids recursive printing when logging or debugging.</li>
 * </ul>
 */

@Entity
@Table(name = "entity_room")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "hotel")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int floor;
    private int numberOfBeds;
    @Enumerated(EnumType.STRING)
    private BedsType bedType;
    private int peopleCapacity;
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    @Enumerated(EnumType.STRING)
    private RoomState state;
    private int timeWasBooked;
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel;
    @OneToMany(mappedBy = "roomBooked")
    private List<Reservation> reservation;
    @OneToMany(mappedBy = "room")
    private List<RoomBookingPeriod> roomBookingPeriod;

}
