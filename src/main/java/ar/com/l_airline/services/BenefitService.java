package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.BenefitDTO;
import ar.com.l_airline.domain.entities.Benefit;
import ar.com.l_airline.repositories.BenefitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class responsible for handling business logic related to {@link ar.com.l_airline.domain.entities.Benefit}.
 * Provides methods to create, validate, transform, and retrieve Benefit data.
 */
@Service
public class BenefitService {

    private final BenefitRepository repository;

    public BenefitService(BenefitRepository repository) {
        this.repository = repository;
    }


    /**
     * Validates a {@link BenefitDTO} to ensure all required fields are present and logically correct.
     *
     * @param dto the DTO to validate
     * @throws RuntimeException if any field is invalid
     */
    void validateBenefit(BenefitDTO dto){
        if (dto.getName().isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }
        if (dto.getDescription().isBlank()){
            throw new RuntimeException("Description cannot be null.");
        }
        if (dto.getOpenAt() == null){
            throw new RuntimeException("Opening time cannot be null.");
        }
        if (dto.getCloseAt() == null){
            throw new RuntimeException("Ending time cannot be null.");
        }
    }
    /**
     * Validates a {@link Benefit} entity to ensure all required fields are present and logically correct.
     *
     * @param object the Benefit entity to validate
     * @throws RuntimeException if any field is invalid
     */
    void validateBenefit(Benefit object){
        if (object.getName().isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }
        if (object.getDescription().isBlank()){
            throw new RuntimeException("Description cannot be null.");
        }
        if (object.getOpenAt() == null){
            throw new RuntimeException("Opening time cannot be null.");
        }
        if (object.getCloseAt() == null){
            throw new RuntimeException("Ending time cannot be null.");
        }
    }

    /**
     * Maps a list of {@link Benefit} entities to a list of {@link BenefitDTO} objects.
     *
     * @param list the list of Benefit entities to transform
     * @return a list of BenefitDTOs, or an empty list if no elements are found
     */
    public List<BenefitDTO> parseBenefitListToBenefitDTOList (List<Benefit> list){
        if (list.isEmpty()){
            return Collections.emptyList();
        }

        List<BenefitDTO> response = new ArrayList<>();

        list.forEach(benefit ->{
            BenefitDTO dto = BenefitDTO.builder()
                    .name(benefit.getName())
                    .description(benefit.getDescription())
                    .openAt(benefit.getOpenAt())
                    .closeAt(benefit.getCloseAt())
                    .hotelId(benefit.getHotel().getId()).build();

            response.add(dto);
        });

        return response;
    }

    /**
     * Creates and persists a new Benefit entity using the data provided in the DTO.
     *
     * @param dto the BenefitDTO with data to create the benefit
     * @return the persisted Benefit entity
     * @throws RuntimeException if validation fails
     */
    @Transactional
    public Benefit createBenefit(BenefitDTO dto){
        validateBenefit(dto);

        Benefit benefit = Benefit.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .openAt(dto.getOpenAt())
                .closeAt(dto.getCloseAt()).build();

        repository.save(benefit);

        return benefit;
    }

    /**
     * Retrieves a Benefit by its ID and maps it to a BenefitDTO for external use.
     *
     * @param id the unique identifier of the benefit
     * @return the BenefitDTO representation of the benefit
     * @throws RuntimeException if the ID is invalid or the benefit does not exist
     */
    public BenefitDTO getBenefitByIdResponse(Long id){
        if (id == 0){
            throw new RuntimeException("Insert a valid id number.");
        }

        Benefit result = repository.findById(id).orElseThrow();

        return BenefitDTO.builder()
                .name(result.getName())
                .description(result.getDescription())
                .openAt(result.getOpenAt())
                .closeAt(result.getCloseAt())
                .hotelId(result.getHotel().getId()).build();
    }
    /**
    * Retrieves a Benefit entity by its unique identifier.
    *
    * @param id the ID of the Benefit to retrieve
    * @return the Benefit entity with the given ID
    * @throws RuntimeException if the provided ID is zero
    */
    public Benefit getBenefitByIdObject(Long id) {
        if (id == 0) {
            throw new RuntimeException("Insert a valid id number.");
        }

        return repository.findById(id).orElseThrow();
    }

    /**
     * Retrieves a list of BenefitDTOs whose name contains the specified string.
     *
     * @param name a partial or full name to search for
     * @return a list of matching BenefitDTOs, or an empty list if none found
     * @throws RuntimeException if the provided name is blank
     */
    public List<BenefitDTO> getBenefitByName(String name){
        if (name.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Benefit> result = repository.findByNameContaining(name);

        return parseBenefitListToBenefitDTOList(result);
    }

    /**
     * Retrieves a list of BenefitDTOs whose description contains the specified string.
     *
     * @param desc a partial or full description to search for
     * @return a list of matching BenefitDTOs, or an empty list if none found
     * @throws RuntimeException if the provided description is blank
     */
    public List<BenefitDTO> getBenefitByDescription(String desc){
        if (desc.isBlank()){
            throw new RuntimeException("Description cannot be null.");
        }

        List<Benefit> result = repository.findByDescriptionContaining(desc);

        return parseBenefitListToBenefitDTOList(result);
    }

    /**
     * Retrieves a list of BenefitDTOs with opening times greater than the specified time.
     *
     * @param opening the minimum opening time (exclusive)
     * @return a list of BenefitDTOs with later opening times
     * @throws RuntimeException if the provided opening time is null
     */
    public List<BenefitDTO> getBenefitByOpening(LocalTime opening){
        if (opening == null){
            throw new RuntimeException("Opening time cannot be null.");
        }

        List<Benefit> result = repository.findByOpenAtGreaterThan(opening);

        return parseBenefitListToBenefitDTOList(result);
    }

    /**
     * Retrieves a list of BenefitDTOs with closing times less than the specified time.
     *
     * @param ending the maximum closing time (exclusive)
     * @return a list of BenefitDTOs with earlier closing times
     * @throws RuntimeException if the provided ending time is null
     */
    public List<BenefitDTO> getBenefitByEnding(LocalTime ending){
        if (ending == null){
            throw new RuntimeException("ending time cannot be null.");
        }

        List<Benefit> result = repository.findByCloseAtLessThan(ending);

        return parseBenefitListToBenefitDTOList(result);
    }

    /**
     * Retrieves a list of BenefitDTOs whose opening and closing times fall within a specific range.
     *
     * @param open the minimum opening time (inclusive)
     * @param close the maximum closing time (inclusive)
     * @return a list of BenefitDTOs within the specified time range
     * @throws RuntimeException if either open or close time is null
     */
    public List<BenefitDTO> getByOpenBetween(LocalTime open, LocalTime close){
        if (open == null || close == null){
            throw new RuntimeException("Both ending or opening cannot be null.");
        }

        List<Benefit> result = repository.findByOpenAtGreaterThanEqualAndCloseAtLessThanEqual(open, close);

        return parseBenefitListToBenefitDTOList(result);
    }

    /**
     * Retrieves a list of BenefitDTOs associated with a specific hotel ID.
     *
     * @param hotelId the ID of the hotel whose benefits are to be retrieved
     * @return a list of BenefitDTOs linked to the given hotel
     * @throws RuntimeException if the hotel ID is null
     */
    public List<BenefitDTO> getByHotelId(Long hotelId){
        if (hotelId == null){
            throw new RuntimeException("Id cannot be null");
        }

        List<Benefit> result = repository.findByHotel(hotelId);

        return parseBenefitListToBenefitDTOList(result);
    }

    /**
     * Updates an existing Benefit entity with data provided in the BenefitDTO.
     * Only non-blank fields in the DTO are used to update the entity.
     * The updated entity is validated before being saved.
     *
     * @param id the ID of the Benefit to update
     * @param dto the DTO containing the updated data
     * @return the updated Benefit entity
     * @throws RuntimeException if the benefit does not exist or validation fails
     */
    @Transactional
    public Benefit updateBenefit(Long id, BenefitDTO dto){
        Benefit benefitInDB = this.getBenefitByIdObject(id);

        if (!dto.getName().isBlank()){
            benefitInDB.setName(dto.getName());
        }
        if (!dto.getDescription().isBlank()){
            benefitInDB.setDescription(dto.getDescription());
        }
        if (dto.getOpenAt() != null){
            benefitInDB.setOpenAt(dto.getOpenAt());
        }
        if (dto.getCloseAt() != null){
            benefitInDB.setCloseAt(dto.getCloseAt());
        }

        validateBenefit(benefitInDB);
        repository.save(benefitInDB);

        return benefitInDB;
    }

    /**
     * Deletes a Benefit entity by its ID.
     * Deletion is not allowed if the benefit is currently active
     * (i.e., the current time is between its opening and closing times).
     *
     * @param id the ID of the Benefit to delete
     * @throws RuntimeException if the benefit is active or does not exist
     */
    @Transactional
    public void deleteBenefitById(Long id){
        Benefit benefitInDB = this.getBenefitByIdObject(id);

        if (benefitInDB.getOpenAt().isBefore(LocalTime.now()) && benefitInDB.getCloseAt().isAfter(LocalTime.now())){
            throw new RuntimeException("Cannot delete a Benefit when is working.");
        }

        repository.deleteById(id);
    }

}
