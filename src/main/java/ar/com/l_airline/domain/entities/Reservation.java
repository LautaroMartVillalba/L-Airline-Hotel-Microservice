package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
    @OneToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room roomBooked;

}
