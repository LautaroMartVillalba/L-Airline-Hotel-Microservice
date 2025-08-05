package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.PersonDTO;
import ar.com.l_airline.domain.dto.ReservationDTO;
import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.Reservation;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.repositories.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private void validateId(Long id, String entity){
        if (id == null || id < 1){
            throw new RuntimeException(entity + " id cannot be null or less than zero.");
        }
    }
    private void validateReservationDate(LocalDate startAt, LocalDate endAt){
        if (startAt == null || endAt == null){
            throw new RuntimeException("Both parameters cannot be null.");
        }
        if (startAt.isBefore(LocalDate.now()) || endAt.isBefore(startAt)){
            throw new RuntimeException("Please, insert a valid reservation date.");
        }
    }
    private void validateNumberOfPeople(int numberOfPeople){
        if (numberOfPeople < 1 || numberOfPeople > 4){
            throw new RuntimeException("A room can only accommodate one to four people.");
        }
    }

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

    @Transactional
    public Reservation createReservation(ReservationDTO dto, PersonDTO personDTO) {
        validateReservationDate(dto.getStartAt(), dto.getEndAt());
        validateNumberOfPeople(dto.getNumberOfPeople());

        List<Reservation> allRoomReservationInDb = reservationRepository.findByRoom(dto.getRoomBookedId());

        for (Reservation reservation : allRoomReservationInDb){
            boolean overlaps = !(dto.getStartAt().isBefore(reservation.getStartAt()) || dto.getStartAt().isAfter(reservation.getEndAt()));
            if (overlaps){
                throw new RuntimeException("This room is reserved.");
            }
        }

        Person client = personService.getPersonByIdObject(dto.getPersonId()).orElseGet(() ->personService.createPerson(personDTO));

        Reservation reservation = Reservation.builder()
                .numberOfPeople(dto.getNumberOfPeople())
                .numberOfNights(dto.getNumberOfNights())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .client(client)
                .roomBooked(roomService.getRoomById(dto.getRoomBookedId())).build();

        reservationRepository.save(reservation);
        roomService.changeRoomState(dto.getRoomBookedId(), RoomState.RESERVED);

        return reservation;
    }

    public Reservation getById(Long id) {
        validateId(id, "Reservation");

        return reservationRepository.findById(id).orElseThrow();
    }
    public ReservationDTO getByIdResponse(Long id) {
        validateId(id, "Reservation");

        Reservation result = reservationRepository.findById(id).orElseThrow();
        
        return ReservationDTO.builder()
                .numberOfPeople(result.getNumberOfPeople())
                .numberOfNights(result.getNumberOfNights())
                .endAt(result.getEndAt())
                .startAt(result.getStartAt())
                .personId(result.getClient().getId())
                .roomBookedId(result.getRoomBooked().getId()).build();
    }

    public List<ReservationDTO> getByNumberOfPeople(int people) {
        if (people < 1 || people > 4) {
            throw new RuntimeException("Number of people must be between 1 and 4 people.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByNumberOfPeople(people));
    }

    public List<ReservationDTO> getByNumberOfNight(int nights) {
        if (nights < 1) {
            throw new RuntimeException("A reservation must be at least at 1 night.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByNumberOfNights(nights));
    }

    public List<ReservationDTO> getByPeopleAndNights(int people, int night) {
        if (people < 1 || people > 4) {
            throw new RuntimeException("Number of people must be between 1 and 4 people.");
        }
        if (night < 1) {
            throw new RuntimeException("A reservation must be at least at 1 night.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByNumberOfPeopleAndNumberOfNights(people, night));
    }

    public List<ReservationDTO> getByStartIn(LocalDate date) {
        if (date == null) {
            throw new RuntimeException("Date cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByStartAtGreaterThan(date));
    }

    public List<ReservationDTO> getByFinishIn(LocalDate date) {
        if (date == null) {
            throw new RuntimeException("Date cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByEndAtLessThan(date));
    }

    public List<ReservationDTO> getByBetweenDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new RuntimeException("Date cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByStartAtGreaterThanAndEndAtLessThan(start, end));
    }

    public List<ReservationDTO> getByRoom(Long roomId) {
        if (roomId < 1) {
            throw new RuntimeException("Id cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByRoom(roomId));
    }

    public List<ReservationDTO> getByClient(Long clientId) {
        if (clientId < 1) {
            throw new RuntimeException("Id cannot be null.");
        }

        return convertFromEntityListToDTOList(reservationRepository.findByPerson(clientId));
    }

    @Transactional
    public Reservation update(Long reservationId, ReservationDTO dto) {
        validateId(reservationId, "Reservation");

        Reservation reservationInDB = reservationRepository.findById(reservationId).orElseThrow();
        if (dto.getRoomBookedId() > 0) {
            Room room = roomService.getRoomById(dto.getRoomBookedId());
            reservationInDB.setRoomBooked(room);
        }
        if (dto.getNumberOfNights() > 0) {
            reservationInDB.setNumberOfNights(dto.getNumberOfNights());
        }
        if (dto.getNumberOfPeople() > 0 && dto.getNumberOfPeople() < 5) {
            reservationInDB.setNumberOfPeople(dto.getNumberOfPeople());
        }
        if (dto.getStartAt() != null) {
            reservationInDB.setStartAt(dto.getStartAt());
        }
        if (dto.getEndAt() != null) {
            reservationInDB.setEndAt(dto.getEndAt());
        }
        validateReservationDate(reservationInDB.getStartAt(), reservationInDB.getEndAt());
        validateNumberOfPeople(reservationInDB.getNumberOfPeople());

        return reservationInDB;
    }

    @Transactional
    public void delete(Long reservationId) {
        validateId(reservationId, "Reservation");

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        if (reservation.getStartAt().isBefore(LocalDate.now()) && reservation.getEndAt().isAfter(LocalDate.now())){
            throw new RuntimeException("The reservation is actually available. Cannot be deleted.");
        }
        roomService.changeRoomState(reservation.getRoomBooked().getId(), RoomState.FREE);

        reservationRepository.delete(reservation);
    }

}
