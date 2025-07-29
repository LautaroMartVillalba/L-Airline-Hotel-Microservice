package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.AttractionDTO;
import ar.com.l_airline.domain.entities.Attraction;
import ar.com.l_airline.domain.entities.Hotel;
import ar.com.l_airline.repositories.AttractionRepository;
import ar.com.l_airline.repositories.HotelRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public List<AttractionDTO> parseFromAttractionListToAttractionDTOList(List<Attraction> list){
        List<AttractionDTO> response = new ArrayList<>();

        list.forEach(attraction -> {
            AttractionDTO dto = AttractionDTO.builder()
                    .name(attraction.getName())
                    .description(attraction.getDescription())
                    .peopleCapacity(attraction.getPeopleCapacity())
                    .openAt(attraction.getOpenAt())
                    .closeAt(attraction.getCloseAt())
                    .hotelId(attraction.getHotel().getId()).build();

            response.add(dto);
        });

        return response;
    }

    @Transactional
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

    public AttractionDTO getAttractionByIdDTO(Long id) {
        if (id <= 0) {
            throw new RuntimeException("Id cannot be null.");
        }

        Optional<Attraction> attractionInDB = attractionRepository.findById(id);
        if (attractionInDB.isEmpty()){
            throw new RuntimeException("Attraction cannot be found by id.");
        }
        Attraction attraction = attractionInDB.get();
        return AttractionDTO.builder()
                .name(attraction.getName())
                .description(attraction.getDescription())
                .peopleCapacity(attraction.getPeopleCapacity())
                .openAt(attraction.getOpenAt())
                .closeAt(attraction.getCloseAt()).build();
    }
    public Optional<Attraction> getAttractionByIdObject(Long id) {
        if (id <= 0) {
            throw new RuntimeException("Id cannot be null.");
        }

        return attractionRepository.findById(id);
    }

    public List<AttractionDTO> getAttractionByName(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("Name parameter cannot be empty.");
        }

        List<Attraction> result = attractionRepository.findByNameContaining(name);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromAttractionListToAttractionDTOList(result);
    }

    List<AttractionDTO> getAttractionByDesc(String desc) {
        if (desc.isBlank()) {
            throw new RuntimeException("Description cannot be null.");
        }

        List<Attraction> result = attractionRepository.findByDescriptionContaining(desc);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromAttractionListToAttractionDTOList(result);
    }

    List<AttractionDTO> getAttractionByCapacity(int min, int max) {
        if (min <= 0 || min > max || max <= 0) {
            throw new RuntimeException("Insert valid minimum and maximum values.");
        }

        List<Attraction> result = attractionRepository.findByPeopleCapacityBetween(min, max);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromAttractionListToAttractionDTOList(result);
    }

    List<AttractionDTO> getAttractionByOpening(LocalTime time) {
        if (time == null) {
            throw new RuntimeException("Invalid time format.");
        }

        List<Attraction> result = attractionRepository.findByOpenAtGreaterThan(time);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromAttractionListToAttractionDTOList(result);
    }

    List<AttractionDTO> getAttractionByEnding(LocalTime time) {
        if (time == null) {
            throw new RuntimeException("Invalid time format.");
        }

        List<Attraction> result = attractionRepository.findByCloseAtLessThan(time);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromAttractionListToAttractionDTOList(result);
    }

    List<AttractionDTO> getAttractionBetweenOpeningAndEnding(LocalTime opening, LocalTime ending) {
        if (opening == null || ending == null) {
            throw new RuntimeException("Invalid time format.");
        }

        List<Attraction> result = attractionRepository.findByOpenAtGreaterThanEqualAndCloseAtLessThanEqual(opening, ending);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromAttractionListToAttractionDTOList(result);
    }

    @Transactional
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

    @Transactional
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
