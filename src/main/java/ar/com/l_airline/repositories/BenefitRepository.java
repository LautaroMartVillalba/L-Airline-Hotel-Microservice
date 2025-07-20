package ar.com.l_airline.repositories;

import ar.com.l_airline.domain.entities.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    List<Benefit> findByNameContaining(String name);
    List<Benefit> findByDescriptionContaining(String description);
    List<Benefit> findByOpenAtGreaterThan(LocalTime opening);
    List<Benefit> findByCloseAtLessThan(LocalTime ending);
    List<Benefit> findByOpenAtGreaterThanEqualAndCloseAtLessThanEqual(LocalTime opening, LocalTime ending);

}