package ar.com.l_airline.repositories;

import ar.com.l_airline.entities.hotel.Hotel;
import ar.com.l_airline.entities.hotel.enums.Room;
import ar.com.l_airline.location.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCity(City city);
    List<Hotel> findByNameContaining(String name);
    List<Hotel> findByRoomType(Room roomType);
    List<Hotel> findByPricePerNightBetween(double min, double max);
    Optional<Hotel> findByNameAndCityAndRoomType(String name, City city, Room roomType);

}
