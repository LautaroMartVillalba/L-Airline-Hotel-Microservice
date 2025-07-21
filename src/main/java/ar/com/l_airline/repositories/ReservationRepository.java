package ar.com.l_airline.repositories;

import ar.com.l_airline.domain.entities.Reservation;
import org.apache.catalina.LifecycleState;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByNumberOfPeople(int people);
    List<Reservation> findByNumberOfNights(int nights);
    List<Reservation> findByNumberOfPeopleAndNumberOfNights(int people, int nights);
    List<Reservation> findByStartAtGreaterThan(LocalDate start);
    List<Reservation> findByEndAtLessThan(LocalDate end);
    List<Reservation> findByStartAtGreaterThanAndEndAtLessThan(LocalDate start, LocalDate end);
    @Query("SELECT r FROM Reservation r WHERE r.roomBooked = :roomId")
    List<Reservation> findByRoom(Long roomId);
    @Query("SELECT r FROM Reservation r WHERE r.client = :personId")
    List<Reservation> findByPerson(Long personId);

}
