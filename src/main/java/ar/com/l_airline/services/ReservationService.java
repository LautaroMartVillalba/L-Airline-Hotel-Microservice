package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.PersonDTO;
import ar.com.l_airline.domain.dto.ReservationDTO;
import ar.com.l_airline.domain.dto.RoomDTO;
import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.Reservation;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundInDatabaseException;
import ar.com.l_airline.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for handling business logic related to Reservations.
 * <p>
 * Provides validation methods, entity-to-DTO conversion logic, and interacts with
 * repositories and other services to process reservation-related operations.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PersonService personService;
    private final RoomService roomService;

    public ReservationService(ReservationRepository reservationRepository, PersonService personService, RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.personService = personService;
        this.roomService = roomService;
    }

    /**
     * Validates that an ID is not null and greater than zero.
     *
     * @param id the ID to validate
     * @param entity the name of the entity (used in exception message)
     * @throws MissingDataException if ID is null or less than 1
     */
    private void validateId(Long id, String entity){
        if (id == null || id < 1){
            throw new MissingDataException(entity + " id cannot be null or less than zero.");
        }
    }
    /**
     * Validates that both reservation dates are provided and that the start date
     * is not in the past and the end date is not before the start date.
     *
     * @param startAt the reservation start date
     * @param endAt the reservation end date
     * @throws MissingDataException if validation fails
     */
    private Long validateReservationDate(LocalDate startAt, LocalDate endAt){
        if (startAt == null || endAt == null){
            throw new MissingDataException("Both parameters cannot be null.");
        }
        if (endAt.isBefore(startAt)){
            throw new MissingDataException("Please, insert a valid reservation date.");
        }
        return ChronoUnit.DAYS.between(startAt, endAt);
    }
    /**
     * Validates the number of people in the reservation.
     * The Acceptable range is from 1 to 4 inclusive.
     *
     * @param numberOfPeople the number of people to validate
     * @throws MissingDataException if number is outside the valid range
     */
    private void validateNumberOfPeople(Long numberOfPeople){
        if (numberOfPeople < 1 || numberOfPeople > 4){
            throw new MissingDataException("A room can only accommodate one to four people.");
        }
    }

    private void validateIfTargetRoomIsReserved(Long roomId, LocalDate startAt, LocalDate endAt){
        List<RoomDTO> freeRoomsInReservationDate = roomService.getFreeRoomsByScheduleBetween(startAt, endAt);

        if (freeRoomsInReservationDate.stream().noneMatch(room -> Objects.equals(room.getId(), roomId))){
            throw new MissingDataException("Selected room is not available to reserve between the received date.");
        }
    }

    /**
     * Converts a list of Reservation entities into a list of ReservationDTOs.
     *
     * @param list the list of Reservation entities to convert
     * @return a list of ReservationDTOs, or an empty list if input is empty
     */
    List<ReservationDTO> convertFromEntityListToDTOList(List<Reservation> list){
        if (list.isEmpty()){
            return new ArrayList<>();
        }

        List<ReservationDTO> response = new ArrayList<>();

        list.forEach(res -> {
            ReservationDTO transfer = ReservationDTO.builder()
                    .numberOfPeople(res.getNumberOfPeople())
                    .numberOfNights(res.getNumberOfNights())
                    .endAt(res.getEndAt())
                    .startAt(res.getStartAt())
                    .personId(res.getClient().getId())
                    .roomBookedId(res.getRoomBooked().getId()).build();

            response.add(transfer);
        });

        return response;
    }

    /**
     * Creates a new reservation with the provided reservation and person data.
     * <p>
     * This method performs multiple validations, including reservation dates and number of people.
     * It also ensures that the room is not already reserved during the specified date range.
     * If the person does not exist, a new one is created.
     * The room is then marked as RESERVED.
     *
     * @param dto the reservation data transfer object containing reservation info
     * @param personDTO the person data transfer object for client creation if needed
     * @return the created Reservation entity
     * @throws MissingDataException if any validation fails or if the room is already booked
     */
    @Transactional
    public Reservation createReservation(ReservationDTO dto, PersonDTO personDTO) {
        Long numberOfNights = validateReservationDate(dto.getStartAt(), dto.getEndAt());
        validateNumberOfPeople(dto.getNumberOfPeople());
        validateIfTargetRoomIsReserved(dto.getRoomBookedId(), dto.getStartAt(), dto.getEndAt());

        Person client = personService.getPersonByIdObject(dto.getPersonId()).orElseGet(() -> personService.createPerson(personDTO));

        Reservation reservation = Reservation.builder()
                .numberOfPeople(dto.getNumberOfPeople())
                .numberOfNights(numberOfNights)
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .client(client)
                .roomBooked(roomService.getRoomById(dto.getRoomBookedId())).build();

        reservationRepository.save(reservation);
        roomService.changeRoomState(dto.getRoomBookedId(), RoomState.RESERVED);

        return reservation;
    }

    /**
     * Retrieves a reservation entity by its ID.
     *
     * @param id the ID of the reservation
     * @return the Reservation entity
     * @throws MissingDataException if the ID is invalid or the reservation does not exist
     */
    public Reservation getById(Long id) {
        validateId(id, "Reservation");

        return reservationRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase."));
    }
    /**
     * Retrieves a reservation by its ID and converts it into a DTO representation.
     *
     * @param id the ID of the reservation
     * @return the ReservationDTO for the requested reservation
     * @throws MissingDataException if the ID is invalid or the reservation does not exist
     */
    public ReservationDTO getByIdResponse(Long id) {
        validateId(id, "Reservation");

        Reservation result = reservationRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase."));
        
        return ReservationDTO.builder()
                .numberOfPeople(result.getNumberOfPeople())
                .numberOfNights(result.getNumberOfNights())
                .endAt(result.getEndAt())
                .startAt(result.getStartAt())
                .personId(result.getClient().getId())
                .roomBookedId(result.getRoomBooked().getId()).build();
    }

    /**
     * Retrieves a list of reservations filtered by the number of people,
     * and converts the result into a list of DTOs.
     *
     * @param people the number of people in the reservation
     * @return list of ReservationDTOs matching the specified number of people
     * @throws MissingDataException if the number of people is outside the valid range [1, 4]
     */
    public List<ReservationDTO> getByNumberOfPeople(int people) {
        if (people < 1 || people > 4) {
            throw new MissingDataException("Number of people must be between 1 and 4 people.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByNumberOfPeople(people));
    }

    /**
     * Retrieves a list of reservations that match the specified number of nights,
     * and converts them to DTOs.
     *
     * @param nights the number of nights to filter by (must be >= 1)
     * @return list of ReservationDTOs with the specified number of nights
     * @throws MissingDataException if the number of nights is less than 1
     */
    public List<ReservationDTO> getByNumberOfNight(int nights) {
        if (nights < 1) {
            throw new MissingDataException("A reservation must be at least at 1 night.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByNumberOfNights(nights));
    }

    /**
     * Retrieves a list of reservations that match both the specified number of people
     * and number of nights, and converts them to DTOs.
     *
     * @param people number of people in the reservation (must be 1-4)
     * @param night number of nights in the reservation (must be >= 1)
     * @return list of ReservationDTOs matching both filters
     * @throws MissingDataException if validation fails for either parameter
     */
    public List<ReservationDTO> getByPeopleAndNights(int people, int night) {
        if (people < 1 || people > 4) {
            throw new MissingDataException("Number of people must be between 1 and 4 people.");
        }
        if (night < 1) {
            throw new MissingDataException("A reservation must be at least at 1 night.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByNumberOfPeopleAndNumberOfNights(people, night));
    }

    /**
     * Retrieves reservations that start after the specified date,
     * and converts them to DTOs.
     *
     * @param date the minimum start date (exclusive)
     * @return list of ReservationDTOs starting after the specified date
     * @throws MissingDataException if the date is null
     */
    public List<ReservationDTO> getByStartIn(LocalDate date) {
        if (date == null) {
            throw new MissingDataException("Date cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByStartAtGreaterThan(date));
    }

    /**
     * Retrieves reservations that end before the specified date,
     * and converts them to DTOs.
     *
     * @param date the maximum end date (exclusive)
     * @return list of ReservationDTOs ending before the specified date
     * @throws MissingDataException if the date is null
     */
    public List<ReservationDTO> getByFinishIn(LocalDate date) {
        if (date == null) {
            throw new MissingDataException("Date cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByEndAtLessThan(date));
    }

    /**
     * Retrieves reservations that start after the given start date and end before
     * the given end date, and converts them to DTOs.
     *
     * @param start the lower bound for reservation start date (exclusive)
     * @param end the upper bound for reservation end date (exclusive)
     * @return list of ReservationDTOs within the specified date range
     * @throws MissingDataException if either date is null
     */
    public List<ReservationDTO> getByBetweenDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new MissingDataException("Date cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByStartAtGreaterThanAndEndAtLessThan(start, end));
    }

    /**
     * Retrieves all reservations associated with a given room ID,
     * and converts them to DTOs.
     *
     * @param roomId the ID of the room
     * @return list of ReservationDTOs for the specified room
     * @throws MissingDataException if the ID is null or less than 1
     */
    public List<ReservationDTO> getByRoom(Long roomId) {
        if (roomId < 1) {
            throw new MissingDataException("Id cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByRoom(roomId));
    }

    /**
     * Retrieves all reservations made by a specific client (person ID),
     * and converts them to DTOs.
     *
     * @param clientId the ID of the client (person)
     * @return list of ReservationDTOs associated with the specified client
     * @throws MissingDataException if the ID is null or less than 1
     */
    public List<ReservationDTO> getByClient(Long clientId) {
        if (clientId < 1) {
            throw new MissingDataException("Id cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByPerson(clientId));
    }

    /**
     * Updates an existing reservation with the provided data.
     * <p>
     * Only non-null and valid fields from the DTO are applied to the existing reservation.
     * The updated reservation is then validated (dates and number of people).
     *
     * @param reservationId the ID of the reservation to update
     * @param dto the ReservationDTO containing the updated fields
     * @return the updated Reservation entity
     * @throws MissingDataException if the reservation does not exist or if validation fails
     */
    @Transactional
    public Reservation update(Long reservationId, ReservationDTO dto) {
        validateId(reservationId, "Reservation");

        Reservation reservationInDB = reservationRepository.findById(reservationId).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase."));
        if (dto.getRoomBookedId() > 0) {
            validateIfTargetRoomIsReserved(dto.getRoomBookedId(), dto.getStartAt(), dto.getEndAt());
            Room room = roomService.getRoomById(dto.getRoomBookedId());
            reservationInDB.setRoomBooked(room);
        }
        if (dto.getNumberOfNights() > 0) {
            validateIfTargetRoomIsReserved(reservationInDB.getRoomBooked().getId(), dto.getStartAt(), dto.getEndAt());
            reservationInDB.setNumberOfNights(dto.getNumberOfNights());
        }
        if (dto.getNumberOfPeople() > 0 && dto.getNumberOfPeople() < 5) {
            reservationInDB.setNumberOfPeople(dto.getNumberOfPeople());
        }
        if (dto.getStartAt() != null) {
            validateIfTargetRoomIsReserved(dto.getRoomBookedId(), dto.getStartAt(), dto.getEndAt());
            reservationInDB.setStartAt(dto.getStartAt());
        }
        if (dto.getEndAt() != null) {
            validateIfTargetRoomIsReserved(dto.getRoomBookedId(), dto.getStartAt(), dto.getEndAt());
            reservationInDB.setEndAt(dto.getEndAt());
        }
        // Validate the final state of the updated reservation
        validateReservationDate(reservationInDB.getStartAt(), reservationInDB.getEndAt());
        validateNumberOfPeople(reservationInDB.getNumberOfPeople());

        return reservationInDB;
    }

    /**
     * Deletes an existing reservation by its ID.
     * <p>
     * The reservation can only be deleted if it is not currently active
     * (i.e., the current date is not within the reservation range).
     * Upon deletion, the room state is reset to FREE.
     *
     * @param reservationId the ID of the reservation to delete
     * @throws MissingDataException if the reservation is currently active or does not exist
     */
    @Transactional
    public void delete(Long reservationId) {
        validateId(reservationId, "Reservation");

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase."));

        if (reservation.getStartAt().isBefore(LocalDate.now()) && reservation.getEndAt().isAfter(LocalDate.now())){
            throw new MissingDataException("The reservation is actually available. Cannot be deleted.");
        }
        roomService.changeRoomState(reservation.getRoomBooked().getId(), RoomState.FREE);

        reservationRepository.delete(reservation);
    }

}
