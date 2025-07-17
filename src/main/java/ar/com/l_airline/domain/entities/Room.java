package ar.com.l_airline.domain.entities;

import ar.com.l_airline.domain.enums.BedsType;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.domain.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

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

    /**
     * Unique identifier for the room (primary key, auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The floor number where the room is located.
     */
    private int floor;
    /**
     * The number of beds available in the room.
     */
    private int numberOfBeds;
    /**
     * The type of beds in the room (SINGLE_BED, DOUBLE_BED, QUEEN_BED, KING_BED, TWIN_BED.).
     */
    @Enumerated(EnumType.STRING)
    private BedsType bedType;
    /**
     * The maximum number of people that can occupy the room.
     */
    private int peopleCapacity;
    /**
     * The classification or category of the room (STANDARD, DELUXE, SUITE, EXECUTIVE, PRESIDENTIAL).
     */
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    /**
     * The current state of the room (OCCUPIED, UNOCCUPIED, BEING_CLEANED, CLEANED, FREE, RESERVED).
     */
    @Enumerated(EnumType.STRING)
    private RoomState state;
    /**
     * Number of times the room has been booked.
     */
    private int timeWasBooked;
    /**
     * The hotel to which this room belongs.
     * This is a many-to-one relationship (many rooms per hotel).
     */
    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private Hotel hotel;

}
