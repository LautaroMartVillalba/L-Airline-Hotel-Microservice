package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.AddressDTO;
import ar.com.l_airline.domain.dto.PersonDTO;
import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.Reservation;
import ar.com.l_airline.domain.entities.address.Address;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundInDatabaseException;
import ar.com.l_airline.repositories.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing Person-related operations.
 * It provides methods to create, retrieve, and validate Person entities.
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final AddressService addressService;

    public PersonService(PersonRepository personRepository, AddressService addressService) {
        this.personRepository = personRepository;
        this.addressService = addressService;
    }

    /**
     * Validates the required fields and conditions for creating a Person.
     *
     * @param name the name of the person
     * @param dni the national ID of the person
     * @param email the email address of the person
     * @param age the age of the person
     * @param cellPhone the cell phone number of the person
     * @throws MissingDataException if any of the required parameters are blank or age is under 18
     */
    private void validatePerson(String name, String dni, String email, int age, String cellPhone){
        if (age < 18){
            throw new MissingDataException("Only an adult can reservate a room.");
        }
        if (name.isBlank() || dni.isBlank() || email.isBlank() || cellPhone.isBlank()){
            throw new MissingDataException("Name, DNI, Email and Cell Phone Number are mandatory parameters.");
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

            String countryCode = person.getAddress().getState().getCountryCode();
            String stateName = person.getAddress().getState().getSubdivision();
            String streetName = person.getAddress().getStreet();
            String streetNumber = person.getAddress().getNumber();

            return PersonDTO.builder()
                    .name(person.getName())
                    .age(person.getAge())
                    .cellPhone(person.getCellPhone())
                    .address(streetName + " " + streetNumber)
                    .ubication(stateName + ", " + countryCode)
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
     * @param personDTO the data transfer object containing person data
     * @return the persisted Person entity
     * @throws MissingDataException if validation fails
     */
    @Transactional
    public Person createPerson (PersonDTO personDTO, AddressDTO addressDTO){
        validatePerson(personDTO.getName(), personDTO.getDni(), personDTO.getEmail(), personDTO.getAge(), personDTO.getCellPhone());

        Address address = addressService.createAddress(addressDTO);

        Person person = Person.builder()
                .name(personDTO.getName())
                .dni(personDTO.getDni())
                .age(personDTO.getAge())
                .email(personDTO.getEmail())
                .address(address)
                .numberOfReservations(0)
                .cellPhone(personDTO.getCellPhone()).build();

        personRepository.save(person);

        return person;
    }

    /**
     * Retrieves a Person by ID and returns its DTO representation.
     *
     * @param id the ID of the person
     * @return the PersonDTO corresponding to the given ID
     * @throws MissingDataException if the ID is null or not found
     */
    public PersonDTO getPersonByIdDTO (Long id){
        if (id == null){
            throw new MissingDataException("Id parameter cannot be null.");
        }

        Person result = personRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase."));

        String countryCode = result.getAddress().getState().getCountryCode();
        String stateName = result.getAddress().getState().getSubdivision();
        String streetName = result.getAddress().getStreet();
        String streetNumber = result.getAddress().getNumber();

        return PersonDTO.builder()
                .name(result.getName())
                .age(result.getAge())
                .cellPhone(result.getCellPhone())
                .address(streetName + " " + streetNumber)
                .ubication(stateName + ", " + countryCode)
                .reservationId(result.getReservation().getId())
                .numberOfReservations(result.getNumberOfReservations()).build();
    }

    /**
     * Retrieves a Person entity by ID.
     *
     * @param id the ID of the person
     * @return an Optional containing the Person entity if found
     * @throws MissingDataException if the ID is null
     */
    public Optional<Person> getPersonByIdObject (Long id){
        if (id == null){
            throw new MissingDataException("Id parameter cannot be null.");
        }

        return personRepository.findById(id);
    }

    /**
     * Retrieves all persons whose email contains the given string.
     *
     * @param email the partial or full email to search
     * @return a list of PersonDTOs matching the given email
     * @throws MissingDataException if the email is blank
     */
    public List<PersonDTO> getPersonByEmail (String email){
        if (email.isBlank()){
            throw new MissingDataException("Email parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByEmailContaining(email));
    }

    /**
     * Retrieves all persons whose DNI contains the given string.
     *
     * @param DNI the partial or full DNI to search
     * @return a list of PersonDTOs matching the given DNI
     * @throws MissingDataException if the DNI is blank
     */
    public List<PersonDTO> getPersonByDNI (String DNI){
        if (DNI.isBlank()){
            throw new MissingDataException("DNI parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByDniContaining(DNI));
    }

    /**
     * Retrieves a list of PersonDTOs filtered by name (contains).
     *
     * @param name the name substring to search
     * @return list of matching PersonDTOs
     * @throws MissingDataException if name is blank
     */
    public List<PersonDTO> getPersonByName (String name){
        if (name.isBlank()){
            throw new MissingDataException("Name parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNameContaining(name));
    }

    /**
     * Retrieves a list of PersonDTOs filtered by cell phone number (contains).
     *
     * @param cellPhoneNumber the phone number substring to search
     * @return list of matching PersonDTOs
     * @throws MissingDataException if the phone number is blank
     */
    public List<PersonDTO> getPersonByCellphone (String cellPhoneNumber){
        if (cellPhoneNumber.isBlank()){
            throw new MissingDataException("Cellphone number parameter cannot be null.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByCellPhoneContaining(cellPhoneNumber));
    }

    /**
     * Retrieves a list of PersonDTOs with an exact number of reservations.
     *
     * @param numberOfReservations exact reservation count to match
     * @return list of matching PersonDTOs
     * @throws MissingDataException if number is negative
     */
    public List<PersonDTO> getPersonByReservations (int numberOfReservations){
        if (numberOfReservations < 0){
            throw new MissingDataException("Number of reservations must be at least zero.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNumberOfReservations(numberOfReservations));
    }

    /**
     * Retrieves a list of PersonDTOs with more than a certain number of reservations.
     *
     * @param numberOfReservations minimum exclusive number of reservations
     * @return list of matching PersonDTOs
     * @throws MissingDataException if number is negative
     */
    public List<PersonDTO> getPersonByReservationsGreaterThan (int numberOfReservations){
        if (numberOfReservations < 0){
            throw new MissingDataException("Number of reservations must be at least zero.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNumberOfReservationsGreaterThan(numberOfReservations));
    }

    /**
     * Retrieves a list of PersonDTOs with fewer than a certain number of reservations.
     *
     * @param numberOfReservations maximum exclusive number of reservations
     * @return list of matching PersonDTOs
     * @throws MissingDataException if number is negative
     */
    public List<PersonDTO> getPersonByReservationsLessThan (int numberOfReservations){
        if (numberOfReservations < 0){
            throw new MissingDataException("Number of reservations must be at least zero.");
        }

        return convertFromPersonListToPersonDTOList(personRepository.findByNumberOfReservationsLessThan(numberOfReservations));
    }

    /**
     * Retrieves a person associated with the given reservation ID.
     *
     * @param reservation the ID of the reservation linked to the person
     * @return a {@link PersonDTO} containing the person's information
     * @throws MissingDataException if the reservation ID is less than 0
     */
    public PersonDTO getPersonByReservationId (int reservation){
        if (reservation < 0){
            throw new MissingDataException("Reservation id cannot be null or less than zero.");
        }

        Person result = personRepository.findByReservation(reservation).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase"));

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
        Person personInDB = this.getPersonByIdObject(personId).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase"));

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
     * @throws MissingDataException if the ID is null
     * @throws MissingDataException if the person has an active reservation
     */
    @Transactional
    public void deletePersonByID(Long id){
        if (id == null){
            throw new MissingDataException("Id cannot be null");
        }

        Person personInDB = this.getPersonByIdObject(id).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase"));
        Reservation reservationRelatedWithPerson = personInDB.getReservation();

        if (reservationRelatedWithPerson != null){
            throw new MissingDataException("Cannot delete a client when his reservation is active.");
        }

        personRepository.delete(personInDB);
    }

}
