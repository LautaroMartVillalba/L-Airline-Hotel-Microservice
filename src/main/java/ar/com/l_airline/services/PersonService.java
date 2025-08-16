package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.PersonDTO;
import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.Reservation;
import ar.com.l_airline.repositories.PersonRepository;
import ar.com.l_airline.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Service class responsible for managing Person-related operations.
 * It provides methods to create, retrieve, and validate Person entities.
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final ReservationRepository reservationRepository;

    public PersonService(PersonRepository personRepository, ReservationRepository reservationService) {
        this.personRepository = personRepository;
        this.reservationRepository = reservationService;
    }

    /**
     * Validates the required fields and conditions for creating a Person.
     *
     * @param name the name of the person
     * @param dni the national ID of the person
     * @param email the email address of the person
     * @param age the age of the person
     * @param cellPhone the cell phone number of the person
     * @throws RuntimeException if any of the required parameters are blank or age is under 18
     */
    private void validatePerson(String name, String dni, String email, int age, String cellPhone){
        if (age < 18){
            throw new RuntimeException("Only an adult can reservate a room.");
        }
        if (name.isBlank() || dni.isBlank() || email.isBlank() || cellPhone.isBlank()){
            throw new RuntimeException("Name, DNI, Email and Cell Phone Number are mandatory parameters.");
        }
    }

    /**
     * Converts a list of Person entities to a list of PersonDTOs.
     * This method maps each Person entity to its DTO representation.
     *
     * @param list the list of Person entities
     * @return a list of PersonDTOs
     */
    private List<PersonDTO> convertFromPersonListToPersonDTOList(List<Person> list){
        return list.stream().map(person -> {
            Reservation reservation = person.getReservation();

            return PersonDTO.builder()
                    .name(person.getName())
                    .age(person.getAge())
                    .cellPhone(person.getCellPhone())
                    .reservationId(reservation != null ? reservation.getId() : null)
                    .numberOfReservations(person.getNumberOfReservations())
                    .build();
            }
        ).toList();
    }

    /**
     * Creates a new Person entity from the provided DTO.
     * Performs field validation before saving the entity.
     *
     * @param dto the data transfer object containing person data
     * @return the persisted Person entity
     * @throws RuntimeException if validation fails
     */
    @Transactional
    public Person createPerson (PersonDTO dto){
        validatePerson(dto.getName(), dto.getDni(), dto.getEmail(), dto.getAge(), dto.getCellPhone());

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

    /**
     * Retrieves a Person by ID and returns its DTO representation.
     *
     * @param id the ID of the person
     * @return the PersonDTO corresponding to the given ID
     * @throws RuntimeException if the ID is null or not found
     */
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

    /**
     * Retrieves a Person entity by ID.
     *
     * @param id the ID of the person
     * @return an Optional containing the Person entity if found
     * @throws RuntimeException if the ID is null
     */
    public Person getPersonByIdObject (Long id){
        if (id == null){
            throw new RuntimeException("Id parameter cannot be null.");
        }

        return personRepository.findById(id).orElseThrow();
    }

    /**
     * Retrieves all persons whose email contains the given string.
     *
     * @param email the partial or full email to search
     * @return a list of PersonDTOs matching the given email
     * @throws RuntimeException if the email is blank
     */
    public List<PersonDTO> getPersonByEmail (String email){
        if (email.isBlank()){
            throw new RuntimeException("Email parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByEmailContaining(email));
    }

    /**
     * Retrieves all persons whose DNI contains the given string.
     *
     * @param DNI the partial or full DNI to search
     * @return a list of PersonDTOs matching the given DNI
     * @throws RuntimeException if the DNI is blank
     */
    public List<PersonDTO> getPersonByDNI (String DNI){
        if (DNI.isBlank()){
            throw new RuntimeException("DNI parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByDniContaining(DNI));
    }

    /**
     * Retrieves a list of PersonDTOs filtered by name (contains).
     *
     * @param name the name substring to search
     * @return list of matching PersonDTOs
     * @throws RuntimeException if name is blank
     */
    public List<PersonDTO> getPersonByName (String name){
        if (name.isBlank()){
            throw new RuntimeException("Name parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNameContaining(name));
    }

    /**
     * Retrieves a list of PersonDTOs filtered by cell phone number (contains).
     *
     * @param cellPhoneNumber the phone number substring to search
     * @return list of matching PersonDTOs
     * @throws RuntimeException if the phone number is blank
     */
    public List<PersonDTO> getPersonByCellphone (String cellPhoneNumber){
        if (cellPhoneNumber.isBlank()){
            throw new RuntimeException("Cellphone number parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByCellPhoneContaining(cellPhoneNumber));
    }

    /**
     * Retrieves a list of PersonDTOs with an exact number of reservations.
     *
     * @param numberOfReservations exact reservation count to match
     * @return list of matching PersonDTOs
     * @throws RuntimeException if number is negative
     */
    public List<PersonDTO> getPersonByReservations (int numberOfReservations){
        if (numberOfReservations < 0){
            throw new RuntimeException("Number of reservations must be at least zero.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNumberOfReservations(numberOfReservations));
    }

    /**
     * Retrieves a list of PersonDTOs with more than a certain number of reservations.
     *
     * @param numberOfReservations minimum exclusive number of reservations
     * @return list of matching PersonDTOs
     * @throws RuntimeException if number is negative
     */
    public List<PersonDTO> getPersonByReservationsGreaterThan (int numberOfReservations){
        if (numberOfReservations < 0){
            throw new RuntimeException("Number of reservations must be at least zero.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNumberOfReservationsGreaterThan(numberOfReservations));
    }

    /**
     * Retrieves a list of PersonDTOs with fewer than a certain number of reservations.
     *
     * @param numberOfReservations maximum exclusive number of reservations
     * @return list of matching PersonDTOs
     * @throws RuntimeException if number is negative
     */
    public List<PersonDTO> getPersonByReservationsLessThan (int numberOfReservations){
        if (numberOfReservations < 0){
            throw new RuntimeException("Number of reservations must be at least zero.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNumberOfReservationsLessThan(numberOfReservations));
    }

    /**
     * Retrieves a person associated with the given reservation ID.
     *
     * @param reservation the ID of the reservation linked to the person
     * @return a {@link PersonDTO} containing the person's information
     * @throws RuntimeException if the reservation ID is less than 0
     */
    public PersonDTO getPersonByReservationId (int reservation){
        if (reservation < 0){
            throw new RuntimeException("Reservation id cannot be null or less than zero.");
        }

        Person result = personRepository.findByReservation(reservation).orElseThrow();

        return PersonDTO.builder()
                .name(result.getName())
                .age(result.getAge())
                .cellPhone(result.getCellPhone())
                .reservationId(result.getReservation().getId())
                .numberOfReservations(result.getNumberOfReservations()).build();
    }

    /**
     * Updates information for an existing person based on the provided ID and data.
     * Only non-blank fields and valid values from the DTO will be updated.
     *
     * @param personId the ID of the person to update
     * @param dto the data transfer object containing the new person data
     * @return the updated {@link Person} entity
     */
    @Transactional
    public Person updatePersonInfo(Long personId, PersonDTO dto){
        Person personInDB = this.getPersonByIdObject(personId);

        if (!dto.getEmail().isBlank()){
            personInDB.setEmail(dto.getEmail());
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
            personInDB.setNumberOfReservations(dto.getNumberOfReservations());
        }
        if (dto.getAge() >= 18){
            personInDB.setAge(dto.getAge());
        }

        validatePerson(dto.getName(), dto.getDni(), dto.getEmail(), dto.getAge(), dto.getCellPhone());

        personRepository.save(personInDB);
        return personInDB;
    }

    /**
     * Deletes a person by their ID only if they do not have an active reservation.
     *
     * @param id the ID of the person to delete
     * @throws RuntimeException if the ID is null
     * @throws RuntimeException if the person has an active reservation
     */
    @Transactional
    public void deletePersonByID(Long id){
        if (id == null){
            throw new RuntimeException("Id cannot be null");
        }

        Person personInDB = this.getPersonByIdObject(id);
        Reservation reservationRelatedWithPerson = personInDB.getReservation();

        if (reservationRelatedWithPerson != null){
            throw new RuntimeException("Cannot delete a client when his reservation is active.");
        }

        personRepository.delete(personInDB);
    }

}
