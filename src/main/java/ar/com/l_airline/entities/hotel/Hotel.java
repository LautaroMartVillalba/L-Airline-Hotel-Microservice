package ar.com.l_airline.entities.hotel;

import ar.com.l_airline.entities.hotel.enums.Room;
import ar.com.l_airline.location.City;
import jakarta.persistence.*;
import lombok.*;

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
    private String name;
    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private Room roomType;
    @Column(name = "price_per_night")
    private double pricePerNight;

}
