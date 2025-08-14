package ar.com.l_airline.domain.entities;

import ar.com.l_airline.domain.enums.RoomBookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "entity_room_booking_period")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoomBookingPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startAt;
    private LocalDate endAt;
    private RoomBookingStatus status;
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;
    @OneToOne
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    private Reservation reservation;
}
