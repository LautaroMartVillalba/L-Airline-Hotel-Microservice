package ar.com.l_airline.repositories;

import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.domain.enums.BedsType;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.domain.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Repository interface for accessing and managing {@link Room} entities.
 *
 * <p>Custom query methods are automatically implemented by Spring Data JPA
 * based on method names and parameter types.</p>
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    public List<Room> findByNumberOfBeds(int beds);
    public List<Room> findByBedType(BedsType bedType);
    public List<Room> findByPeopleCapacity(int numberOfPeople);
    public List<Room> findByRoomType(RoomType roomType);
    public List<Room> findByState(RoomState state);

}
