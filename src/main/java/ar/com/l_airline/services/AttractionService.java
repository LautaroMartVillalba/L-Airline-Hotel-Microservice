package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.AttractionDTO;
import ar.com.l_airline.domain.entities.Attraction;
import ar.com.l_airline.domain.entities.Hotel;
import ar.com.l_airline.repositories.AttractionRepository;
import ar.com.l_airline.repositories.HotelRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;

import java.time.LocalTime;
import java.util.List;

@Service
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;


    public AttractionService(AttractionRepository attractionRepository, HotelRepository hotelRepository) {
        this.attractionRepository = attractionRepository;
        this.hotelRepository = hotelRepository;
    }

    private void validateAttraction(AttractionDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("Attraction name cannot be null.");
        }
        if (dto.getPeopleCapacity() < 1) {
            throw new RuntimeException("At least the attraction must be capable to be used by one person.");
        }
        if (dto.getOpenAt() == null) {
            throw new RuntimeException("Attraction opening cannot be null.");
        }
        if (dto.getCloseAt() == null) {
            throw new RuntimeException("Attraction ending cannot be null.");
        }
    }

    private void validateAttraction(Attraction data) {
        if (data.getName() == null || data.getName().isBlank()) {
            throw new RuntimeException("Attraction name cannot be null.");
        }
        if (data.getPeopleCapacity() < 1) {
            throw new RuntimeException("At least the attraction must be capable to be used by one person.");
        }
        if (data.getOpenAt() == null) {
            throw new RuntimeException("Attraction opening cannot be null.");
        }
        if (data.getCloseAt() == null) {
            throw new RuntimeException("Attraction ending cannot be null.");
        }
    }

    public Attraction createAttraction(AttractionDTO dto, Long hotelId) {
        validateAttraction(dto);

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow();

        Attraction attraction = Attraction.builder()
                .name(dto.getName())
                .peopleCapacity(dto.getPeopleCapacity())
                .openAt(dto.getOpenAt())
                .closeAt(dto.getCloseAt())
                .hotel(hotel).build();

        attractionRepository.save(attraction);

        return attraction;
    }

    public List<Attraction> getAttractionByName(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("Name parameter cannot be empty.");
        }

        return attractionRepository.findByNameContaining(name);
    }

    List<Attraction> getAttractionByDesc(String desc) {
        if (desc.isBlank()) {
            throw new RuntimeException("Description cannot be null.");
        }

        return attractionRepository.findByDescriptionContaining(desc);
    }

    List<Attraction> getAttractionByCapacity(int min, int max) {
        if (min <= 0 || min > max || max <= 0) {
            throw new RuntimeException("Insert valid minimum and maximum values.");
        }

        return attractionRepository.findByPeopleCapacityBetween(min, max);
    }

    List<Attraction> getAttractionByOpening(LocalTime time) {
        if (time == null) {
            throw new RuntimeException("Invalid time format.");
        }

        return attractionRepository.findByOpenAtGreaterThan(time);
    }

    List<Attraction> getAttractionByEnding(LocalTime time) {
        if (time == null) {
            throw new RuntimeException("Invalid time format.");
        }

        return attractionRepository.findByCloseAtLessThan(time);
    }

    List<Attraction> getAttractionBetweenOpeningAndEnding(LocalTime opening, LocalTime ending) {
        if (opening == null || ending == null) {
            throw new RuntimeException("Invalid time format.");
        }

        return attractionRepository.findByOpenAtGreaterThanEqualAndCloseAtLessThanEqual(opening, ending);
    }

    Attraction updateAttraction(Long id, AttractionDTO dto) {
        Attraction attractionInDB = attractionRepository.findById(id).orElseThrow();

        if (dto.getName() != null && !dto.getName().isBlank()) {
            attractionInDB.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            attractionInDB.setDescription(dto.getDescription());
        }
        if (dto.getOpenAt() != null) {
            attractionInDB.setOpenAt(dto.getOpenAt());
        }
        if (dto.getCloseAt() != null) {
            attractionInDB.setCloseAt(dto.getCloseAt());
        }
        if (dto.getPeopleCapacity() != 0) {
            attractionInDB.setPeopleCapacity(attractionInDB.getPeopleCapacity());
        }

        validateAttraction(attractionInDB);
        attractionRepository.save(attractionInDB);

        return attractionInDB;
    }

    public void deleteAttraction(Long id) {
        Attraction attractionInDB = attractionRepository.findById(id).orElseThrow();

        LocalTime opening = attractionInDB.getOpenAt();
        LocalTime ending = attractionInDB.getCloseAt();

        if (opening.isAfter(LocalTime.now()) && ending.isBefore(LocalTime.now())){
            throw new RuntimeException("Cannot delete an attraction when is working.");
        }

        attractionRepository.deleteById(id);
    }

}
