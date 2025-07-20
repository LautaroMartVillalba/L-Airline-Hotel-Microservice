package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.BenefitDTO;
import ar.com.l_airline.domain.entities.Benefit;
import ar.com.l_airline.repositories.BenefitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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

    public Benefit getBenefitById(Long id){
        if (id == 0){
            throw new RuntimeException("Insert a valid id number.");
        }

        return repository.findById(id).orElseThrow();
    }

    public List<Benefit> getBenefitByName(String name){
        if (name.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        return repository.findByNameContaining(name);
    }

    public List<Benefit> getBenefitByDescription(String desc){
        if (desc.isBlank()){
            throw new RuntimeException("Description cannot be null.");
        }

        return repository.findByDescriptionContaining(desc);
    }

    public List<Benefit> getBenefitByOpening(LocalTime opening){
        if (opening == null){
            throw new RuntimeException("Opening time cannot be null.");
        }

        return repository.findByOpenAtGreaterThan(opening);
    }

    public List<Benefit> getBenefitByEnding(LocalTime ending){
        if (ending == null){
            throw new RuntimeException("ending time cannot be null.");
        }

        return repository.findByCloseAtLessThan(ending);
    }

    public List<Benefit> getByOpenBetween(LocalTime open, LocalTime close){
        if (open == null || close == null){
            throw new RuntimeException("Both ending or opening cannot be null.");
        }

        return repository.findByOpenAtGreaterThanEqualAndCloseAtLessThanEqual(open, close);
    }

    public Benefit updateBenefit(Long id, BenefitDTO dto){
        Benefit benefitInDB = this.getBenefitById(id);

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

    public void deleteBenefitById(Long id){
        Benefit benefitInDB = this.getBenefitById(id);

        if (benefitInDB.getOpenAt().isBefore(LocalTime.now()) && benefitInDB.getCloseAt().isAfter(LocalTime.now())){
            throw new RuntimeException("Cannot delete a Benefit when is working.");
        }

        repository.deleteById(id);
    }

}
