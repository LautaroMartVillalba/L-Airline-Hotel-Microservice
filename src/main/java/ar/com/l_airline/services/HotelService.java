package ar.com.l_airline.services;

import ar.com.l_airline.domain.hotel.Hotel;
import ar.com.l_airline.domain.dto.HotelDTO;
import ar.com.l_airline.domain.enums.Room;
import ar.com.l_airline.exceptionHandler.custom_exceptions.ExistingObjectException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.domain.enums.City;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundException;
import ar.com.l_airline.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    @Autowired
    private HotelRepository repository;

    /**
     * Check if any data is blank or null.
     * @param dto Hotel data.
     * @return False if any data is blank or null. True if not.
     */
    private boolean validateHotel(HotelDTO dto){
        return !dto.getName().isBlank()
                && !dto.getRoomType().name().isBlank()
                && !dto.getCity().name().isBlank()
                && !(dto.getPricePerNight() <= 0);
    }

    /**
     * Persist a new Hotel record in the DataBase, checking if exists a duplicated record.
     * @param dto Hotel data to persist.
     * @return Persisted hotel. Null if exists one record with same data.
     */
    public Hotel createHotel (HotelDTO dto){
        Optional<Hotel> dbHotel = repository.findByNameAndCityAndRoomType(dto.getName(),
                                                                          dto.getCity(),
                                                                          dto.getRoomType());
        if (dbHotel.isPresent()
                && dto.getName().equals(dbHotel.get().getName())
                && dto.getCity() == dbHotel.get().getCity()
                && dto.getRoomType() == dbHotel.get().getRoomType()
                && dto.getPricePerNight() == dbHotel.get().getPricePerNight()){
            throw new ExistingObjectException();
        }
        if (!validateHotel(dto)){
            throw new MissingDataException();
        }

        Hotel hotelSave = Hotel.builder()
                               .name(dto.getName())
                               .city(dto.getCity())
                               .roomType(dto.getRoomType())
                               .pricePerNight(dto.getPricePerNight()).build();
         repository.save(hotelSave);
         return hotelSave;
    }

    /**
     * Search one Hotel record in the DataBase by his id.
     * @param id Identification number.
     * @return Hotel optional if it can found a record. Empty optional if id >= 0, or can't found a matching record.
     */
    public Hotel findHotelById(Long id){
        if (id == null){
            throw new MissingDataException();
        }

        return repository.findById(id).orElseThrow(NotFoundException::new);
    }

    /**
     * Search some Hotel records in the DataBase by his name.
     * @param name Hotel name.
     * @return List of Hotels if it can found some records. Empty List if it can't found.
     */
    public List<Hotel> findHotelByName(String name){
        if (name == null || name.isEmpty()){
            throw new MissingDataException();
        }

        List<Hotel> result = repository.findByNameContaining(name);

        if (result.isEmpty()){
            throw new NotFoundException();
        }

        return result;
    }

    /**
     * Search some Hotel records in the DataBase by his city.
     * @param city Hotel location.
     * @return List of Hotels if it can found some records. Empty List if it can't found.
     */
    public List<Hotel> findHotelByCity(City city){
        if (city == null){
            throw new MissingDataException();
        }

        List<Hotel> result = repository.findByCity(city);

        if (result.isEmpty()){
            throw new NotFoundException();
        }

        return result;
    }

    /**
     * Search some Hotel records in the DataBase by his room type.
     * @param room Hotel room type.
     * @return List of Hotels if it can found some records. Empty List if it can't found.
     */
    public List<Hotel> findHotelByRoom(Room room){
        if (room == null){
            throw new MissingDataException();
        }

        List<Hotel> result = repository.findByRoomType(room);

        if (result.isEmpty()){
            throw new NotFoundException();
        }

        return result;
    }

    /**
     * Search some Hotel records in the DataBase by his room type.
     * @param min Minimum price value.
     * @param max Maximum price value.
     * @return List of Hotels if it can found some records. Null if the min or max values are null.
     */
    public List<Hotel> findHotelByPrice(double min, double max){
        if (min < 0 || max <= min){
            throw new  MissingDataException();
        }

        List<Hotel> result = repository.findByPricePerNightBetween(min, max);

        if (result.isEmpty()){
            throw new NotFoundException();
        }
        return result;
    }

    /**
     * Replace one or more data of one record in the DataBase.
     * @param id Identification number.
     * @param dto Data to replace in the record.
     * @return The persisted changes.
     */
    public Hotel updateHotel (Long id, HotelDTO dto){
        Hotel findHotel = this.findHotelById(id);

        if (dto.getName() != null){
            findHotel.setName(dto.getName());
        }
        if (dto.getCity() != null){
            findHotel.setCity(dto.getCity());
        }
        if (dto.getRoomType() != null){
            findHotel.setRoomType(dto.getRoomType());
        }
        if (dto.getPricePerNight() <= 0){
            findHotel.setPricePerNight(dto.getPricePerNight());
        }

        repository.save(findHotel);
        return  findHotel;
    }

    /**
     * Search and delete (if it can find) one Hotel record in the DataBase.
     * @param id Identification Number.
     * @return True if it can find and delete the Hotel. False if it can't.
     */
    public boolean deleteHotelById(Long id){
        Hotel findHotel = this.findHotelById(id);
        repository.deleteById(findHotel.getId());
        return true;
    }
}
