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

/**
 * Service class responsible for handling business logic related to {@link Attraction}.
 * It provides methods for creating, retrieving, and transforming attractions.
 */
@Service
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final HotelRepository hotelRepository;


    public AttractionService(AttractionRepository attractionRepository, HotelRepository hotelRepository) {
        this.attractionRepository = attractionRepository;
        this.hotelRepository = hotelRepository;
    }

    /**
     * Validates the contents of a {@link AttractionDTO}.
     * Ensures non-null, non-blank values and valid capacities and time ranges.
     *
     * @param dto the DTO to validate
     * @throws RuntimeException if any validation rule fails
     */
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

    /**
     * Validates the contents of an {@link Attraction} entity.
     * The Logic is identical to the DTO validator.
     *
     * @param data the entity to validate
     * @throws RuntimeException if any validation rule fails
     */
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

    /**
     * Maps a list of {@link Attraction} entities to a list of {@link AttractionDTO}.
     *
     * @param list list of Attraction entities
     * @return list of AttractionDTOs
     */
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

    /**
     * Creates and persists a new attraction for a given hotel.
     *
     * @param dto data transfer object containing attraction details
     * @param hotelId ID of the hotel the attraction belongs to
     * @return the persisted Attraction entity
     * @throws RuntimeException if validation fails or hotel does not exist
     */
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

    /**
     * Retrieves an attraction by ID and maps it to a DTO.
     *
     * @param id the attraction ID
     * @return DTO representing the attraction
     * @throws RuntimeException if the ID is invalid or attraction is not found
     */
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
    /**
     * Retrieves an attraction entity by ID.
     *
     * @param id the attraction ID
     * @return an Optional containing the Attraction if found, or empty otherwise
     * @throws RuntimeException if the ID is invalid
     */
    public Optional<Attraction> getAttractionByIdObject(Long id) {
        if (id <= 0) {
            throw new RuntimeException("Id cannot be null.");
        }

        return attractionRepository.findById(id);
    }

    /**
     * Searches for attractions whose names contain the specified substring.
     *
     * @param name substring to match
     * @return a list of matching AttractionDTOs, or an empty list if none are found
     * @throws RuntimeException if the name is blank
     */
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

    /**
     * Searches for attractions whose descriptions contain the specified substring.
     *
     * @param desc substring to match
     * @return a list of matching AttractionDTOs, or an empty list if none are found
     * @throws RuntimeException if the description is blank
     */
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

    /**
     * Retrieves a list of attractions whose capacity falls within the specified range.
     *
     * @param min the minimum allowed people capacity
     * @param max the maximum allowed people capacity
     * @return a list of matching AttractionDTOs, or an empty list if none are found
     * @throws RuntimeException if provided values are invalid or logically inconsistent
     */
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

    /**
     * Retrieves a list of attractions that open after the specified time.
     *
     * @param time the lower bound for opening time
     * @return a list of AttractionDTOs, or an empty list if none are found
     * @throws RuntimeException if the input time is null
     */
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

    /**
     * Retrieves a list of attractions that close before the specified time.
     *
     * @param time the upper bound for closing time
     * @return a list of AttractionDTOs, or an empty list if none are found
     * @throws RuntimeException if the input time is null
     */
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

    /**
     * Retrieves attractions that open no earlier than the given opening time
     * and close no later than the given closing time.
     *
     * @param opening minimum allowed opening time (inclusive)
     * @param ending maximum allowed closing time (inclusive)
     * @return a list of matching AttractionDTOs, or an empty list if none match
     * @throws RuntimeException if any time input is null
     */
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

    /**
     * Retrieves all attractions associated with a specific hotel.
     *
     * @param hotelId the ID of the hotel
     * @return list of AttractionDTOs, or empty if none are found
     * @throws RuntimeException if the hotel ID is null
     */
    List<AttractionDTO> getByHotelId(Long hotelId){
        if (hotelId == null){
            throw new RuntimeException("Id cannot be null.");
        }

        List<Attraction> result = attractionRepository.findByHotel(hotelId);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromAttractionListToAttractionDTOList(result);
    }

    /**
     * Updates an existing attraction based on its ID using non-null fields from the given DTO.
     *
     * @param id the ID of the attraction to update
     * @param dto the DTO containing fields to update
     * @return the updated Attraction entity
     * @throws RuntimeException if the attraction is not found or fails validation
     */
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
            attractionInDB.setPeopleCapacity(dto.getPeopleCapacity());
        }

        validateAttraction(attractionInDB);
        attractionRepository.save(attractionInDB);

        return attractionInDB;
    }

    /**
     * Deletes an attraction if it is not currently active based on current time.
     *
     * @param id the ID of the attraction to delete
     * @throws RuntimeException if the attraction is not found or is currently active
     */
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
