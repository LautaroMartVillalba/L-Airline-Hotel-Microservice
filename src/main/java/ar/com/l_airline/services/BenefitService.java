package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.BenefitDTO;
import ar.com.l_airline.domain.entities.Attraction;
import ar.com.l_airline.domain.entities.Benefit;
import ar.com.l_airline.repositories.BenefitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BenefitService {

    private final BenefitRepository repository;

    public BenefitService(BenefitRepository repository) {
        this.repository = repository;
    }

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
    public Benefit getBenefitByIdObject(Long id) {
        if (id == 0) {
            throw new RuntimeException("Insert a valid id number.");
        }

        return repository.findById(id).orElseThrow();
    }

    public List<BenefitDTO> getBenefitByName(String name){
        if (name.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Benefit> result = repository.findByNameContaining(name);

        return parseBenefitListToBenefitDTOList(result);
    }

    public List<BenefitDTO> getBenefitByDescription(String desc){
        if (desc.isBlank()){
            throw new RuntimeException("Description cannot be null.");
        }

        List<Benefit> result = repository.findByDescriptionContaining(desc);

        return parseBenefitListToBenefitDTOList(result);
    }

    public List<BenefitDTO> getBenefitByOpening(LocalTime opening){
        if (opening == null){
            throw new RuntimeException("Opening time cannot be null.");
        }

        List<Benefit> result = repository.findByOpenAtGreaterThan(opening);

        return parseBenefitListToBenefitDTOList(result);
    }

    public List<BenefitDTO> getBenefitByEnding(LocalTime ending){
        if (ending == null){
            throw new RuntimeException("ending time cannot be null.");
        }

        List<Benefit> result = repository.findByCloseAtLessThan(ending);

        return parseBenefitListToBenefitDTOList(result);
    }

    public List<BenefitDTO> getByOpenBetween(LocalTime open, LocalTime close){
        if (open == null || close == null){
            throw new RuntimeException("Both ending or opening cannot be null.");
        }

        List<Benefit> result = repository.findByOpenAtGreaterThanEqualAndCloseAtLessThanEqual(open, close);

        return parseBenefitListToBenefitDTOList(result);
    }

    public List<BenefitDTO> getByHotelId(Long hotelId){
        if (hotelId == null){
            throw new RuntimeException("Id cannot be null");
        }

        List<Benefit> result = repository.findByHotel(hotelId);

        return parseBenefitListToBenefitDTOList(result);
    }

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

    @Transactional
    public void deleteBenefitById(Long id){
        Benefit benefitInDB = this.getBenefitByIdObject(id);

        if (benefitInDB.getOpenAt().isBefore(LocalTime.now()) && benefitInDB.getCloseAt().isAfter(LocalTime.now())){
            throw new RuntimeException("Cannot delete a Benefit when is working.");
        }

        repository.deleteById(id);
    }

}
