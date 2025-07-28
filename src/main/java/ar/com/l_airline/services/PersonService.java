package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.PersonDTO;
import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.Reservation;
import ar.com.l_airline.repositories.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final ReservationService reservationService;

    public PersonService(PersonRepository personRepository, ReservationService reservationService) {
        this.personRepository = personRepository;
        this.reservationService = reservationService;
    }

    private void validatePerson (PersonDTO dto){
        if (dto.getAge() < 18){
            throw new RuntimeException("Only an adult can reservate a room.");
        }
        if (dto.getName().isBlank() || dto.getDni().isBlank() || dto.getEmail().isBlank() || dto.getCellPhone().isBlank()){
            throw new RuntimeException("Name, DNI, Email and Cell Phone Number are mandatory parameters.");
        }
    }
    private void validatePerson (Person object){
        if (object.getAge() < 18){
            throw new RuntimeException("Only an adult can reservate a room.");
        }
        if (object.getName().isBlank() || object.getDni().isBlank() || object.getEmail().isBlank() || object.getCellPhone().isBlank()){
            throw new RuntimeException("Name, DNI, Email and Cell Phone Number are mandatory parameters.");
        }
    }

    @Transactional
    public Person createPerson (PersonDTO dto){
        validatePerson(dto);

        Person person = Person.builder()
                .name(dto.getName())
                .dni(dto.getDni())
                .age(dto.getAge())
                .email(dto.getEmail())
                .numberOfReservations(0)
                .cellPhone(dto.getCellPhone()).build();

        personRepository.save(person);

        return person;
    }

    public PersonDTO getPersonByIdDTO (Long id){
        if (id == null){
            throw new RuntimeException("Id parameter cannot be null.");
        }

        Person result = personRepository.findById(id).orElseThrow();

        return PersonDTO.builder()
                .name(result.getName())
                .age(result.getAge())
                .cellPhone(result.getCellPhone())
                .reservationId(result.getReservation().getId())
                .numberOfReservations(result.getNumberOfReservations()).build();
    }

    public Optional<Person> getPersonByIdObject (Long id){
        if (id == null){
            throw new RuntimeException("Id parameter cannot be null.");
        }

        return personRepository.findById(id);
    }

    public List<PersonDTO> getPersonByEmail (String email){
        if (email.isBlank()){
            throw new RuntimeException("Email parameter cannot be null.");
        }

        List<Person> result = personRepository.findByEmailContaining(email);
        List<PersonDTO> retrieve = new ArrayList<>();

        result.forEach(person -> {
            PersonDTO dto = PersonDTO.builder()
                    .name(person.getName())
                    .age(person.getAge())
                    .cellPhone(person.getCellPhone())
                    .reservationId(person.getReservation().getId())
                    .numberOfReservations(person.getNumberOfReservations()).build();
            retrieve.add(dto);
        });

        return retrieve;
    }

    public List<PersonDTO> getPersonByDNI (String DNI){
        if (DNI.isBlank()){
            throw new RuntimeException("DNI parameter cannot be null.");
        }

        List<Person> result = personRepository.findByDniContaining(DNI);
        List<PersonDTO> retrieve = new ArrayList<>();

        result.forEach(person -> {
            PersonDTO dto = PersonDTO.builder()
                    .name(person.getName())
                    .age(person.getAge())
                    .cellPhone(person.getCellPhone())
                    .reservationId(person.getReservation().getId())
                    .numberOfReservations(person.getNumberOfReservations()).build();
            retrieve.add(dto);
        });

        return retrieve;
    }

    public List<PersonDTO> getPersonByName (String name){
        if (name.isBlank()){
            throw new RuntimeException("Name parameter cannot be null.");
        }

        List<Person> result = personRepository.findByNameContaining(name);
        List<PersonDTO> retrieve = new ArrayList<>();

        result.forEach(person -> {
            PersonDTO dto = PersonDTO.builder()
                                .name(person.getName())
                                .age(person.getAge())
                                .cellPhone(person.getCellPhone())
                                .reservationId(person.getReservation().getId())
                                .numberOfReservations(person.getNumberOfReservations()).build();
            retrieve.add(dto);
        });

        return retrieve;
    }

    public List<PersonDTO> getPersonByCellphone (String cellPhoneNumber){
        if (cellPhoneNumber.isBlank()){
            throw new RuntimeException("Cellphone number parameter cannot be null.");
        }

        List<Person> result = personRepository.findByCellPhoneContaining(cellPhoneNumber);
        List<PersonDTO> retrieve = new ArrayList<>();

        result.forEach(person -> {
            PersonDTO dto = PersonDTO.builder()
                                .name(person.getName())
                                .age(person.getAge())
                                .cellPhone(person.getCellPhone())
                                .reservationId(person.getReservation().getId())
                                .numberOfReservations(person.getNumberOfReservations()).build();
            retrieve.add(dto);
        });

        return retrieve;
    }

    public List<PersonDTO> getPersonByReservations (int numberOfReservations){

        List<Person> result = personRepository.findByNumberOfReservations(numberOfReservations);
        List<PersonDTO> retrieve = new ArrayList<>();

        result.forEach(person -> {
            PersonDTO dto = PersonDTO.builder()
                                .name(person.getName())
                                .age(person.getAge())
                                .cellPhone(person.getCellPhone())
                                .reservationId(person.getReservation().getId())
                                .numberOfReservations(person.getNumberOfReservations()).build();
            retrieve.add(dto);
        });

        return retrieve;
    }

    public List<PersonDTO> getPersonByReservationsGreaterThan (int numberOfReservations){

        List<Person> result = personRepository.findByNumberOfReservationsGreaterThan(numberOfReservations);
        List<PersonDTO> retrieve = new ArrayList<>();

        result.forEach(person -> {
            PersonDTO dto = PersonDTO.builder()
                    .name(person.getName())
                    .age(person.getAge())
                    .cellPhone(person.getCellPhone())
                    .reservationId(person.getReservation().getId())
                    .numberOfReservations(person.getNumberOfReservations()).build();
            retrieve.add(dto);
        });

        return retrieve;
    }

    public List<PersonDTO> getPersonByReservationsLessThan (int numberOfReservations){

        List<Person> result = personRepository.findByNumberOfReservationsLessThan(numberOfReservations);
        List<PersonDTO> retrieve = new ArrayList<>();

        result.forEach(person -> {
            PersonDTO dto = PersonDTO.builder()
                    .name(person.getName())
                    .age(person.getAge())
                    .cellPhone(person.getCellPhone())
                    .reservationId(person.getReservation().getId())
                    .numberOfReservations(person.getNumberOfReservations()).build();
            retrieve.add(dto);
        });

        return retrieve;
    }

    public PersonDTO getPersonByReservationId (int reservation){

        Person result = personRepository.findByReservation(reservation).orElseThrow();

        return PersonDTO.builder()
                .name(result.getName())
                .age(result.getAge())
                .cellPhone(result.getCellPhone())
                .reservationId(result.getReservation().getId())
                .numberOfReservations(result.getNumberOfReservations()).build();
    }

    @Transactional
    public Person updatePersonInfo(Long personId, PersonDTO dto){
        Person personInDB = this.getPersonByIdObject(personId).orElseThrow();

        if (!dto.getEmail().isBlank()){
            personInDB.setEmail(dto.getEmail());
        }
        if (dto.getReservationId() != null){
            Reservation newReservation = reservationService.getById(dto.getReservationId());
            personInDB.setReservation(newReservation);
        }
        if (!dto.getName().isBlank()){
            personInDB.setName(dto.getName());
        }
        if (!dto.getDni().isBlank()){
            personInDB.setDni(dto.getDni());
        }
        if (!dto.getCellPhone().isBlank()){
            personInDB.setCellPhone(dto.getCellPhone());
        }
        if (dto.getNumberOfReservations() > 0){

            personInDB.setCellPhone(dto.getCellPhone());
        }
        if (dto.getAge() >= 18){
            personInDB.setAge(dto.getAge());
        }

        validatePerson(personInDB);

        personRepository.save(personInDB);
        return personInDB;
    }

    @Transactional
    public void deletePersonByID(Long id){
        if (id == null){
            throw new RuntimeException("Id cannot be null");
        }

        Person personInDB = this.getPersonByIdObject(id).orElseThrow();
        Reservation reservationRelatedWithPerson = personInDB.getReservation();

        if (reservationRelatedWithPerson != null){
            throw new RuntimeException("Cannot delete a client when his reservation is active.");
        }

        personRepository.delete(personInDB);
    }

}
