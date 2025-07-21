package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.ReservationDTO;
import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.Reservation;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.repositories.PersonRepository;
import ar.com.l_airline.repositories.ReservationRepository;
import ar.com.l_airline.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.MediaSize;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, PersonRepository personRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
    }

    private void validateReservation(Reservation obj) {
        if (obj.getNumberOfPeople() < 1 || obj.getNumberOfPeople() > 4) {
            throw new RuntimeException("Reservations only can contain 1 to 4 people.");
        }
        if (obj.getNumberOfNights() < 1) {
            throw new RuntimeException("Reservations only accepts 1 night as minimum reservation time.");
        }
        if (obj.getStartAt().isBefore(LocalDate.now())) {
            throw new RuntimeException("A reservation cannot be created in the past.");
        }
        long days = ChronoUnit.DAYS.between(obj.getStartAt(), obj.getEndAt());
        if (days < 1 || obj.getEndAt().isBefore(LocalDate.now()) || obj.getEndAt().isEqual(LocalDate.now())) {
            throw new RuntimeException("Reservations cannot be less than one night.");
        }
        if (obj.getRoomBooked() == null) {
            throw new RuntimeException("Reservations have to point to one room.");
        }
        if (obj.getClient() == null) {
            throw new RuntimeException("Reservations have to point to one client.");
        }
    }

    private void validateReservation(ReservationDTO dto) {
        if (dto.getNumberOfPeople() < 1 || dto.getNumberOfPeople() > 4) {
            throw new RuntimeException("Reservations only can contain 1 to 4 people.");
        }
        if (dto.getNumberOfNights() < 1) {
            throw new RuntimeException("Reservations only accepts 1 night as minimum reservation time.");
        }
        if (dto.getStartAt().isBefore(LocalDate.now())) {
            throw new RuntimeException("A reservation cannot be created in the past.");
        }
        long days = ChronoUnit.DAYS.between(dto.getStartAt(), dto.getEndAt());
        if (days < 1 || dto.getEndAt().isBefore(LocalDate.now()) || dto.getEndAt().isEqual(LocalDate.now())) {
            throw new RuntimeException("Reservations cannot be less than one night.");
        }
        if (dto.getRoomBookedId() < 1) {
            throw new RuntimeException("Reservations have to point to one room.");
        }
        if (dto.getClientId() < 1) {
            throw new RuntimeException("Reservations have to point to one client.");
        }
    }

    @Transactional
    public Reservation createReservation(ReservationDTO dto) {
        validateReservation(dto);

        Reservation reservation = Reservation.builder()
                .numberOfPeople(dto.getNumberOfPeople())
                .numberOfNights(dto.getNumberOfNights())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .client(personRepository.findById(dto.getClientId()).orElseThrow())
                .roomBooked(roomRepository.findById(dto.getRoomBookedId()).orElseThrow()).build();

        reservationRepository.save(reservation);

        return reservation;
    }

    public Reservation getById(Long id) {
        if (id == 0) {
            throw new RuntimeException("Id cannot be null.");
        }

        return reservationRepository.findById(id).orElseThrow();
    }

    public List<ReservationDTO> getByNumberOfPeople(int people) {
        if (people < 1 || people > 4) {
            throw new RuntimeException("Number of people must be between 1 and 4 people.");
        }

        List<Reservation> reservations = reservationRepository.findByNumberOfPeople(people);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    public List<ReservationDTO> getByNumberOfNight(int nights) {
        if (nights < 1) {
            throw new RuntimeException("A reservation must be at least at 1 night.");
        }

        List<Reservation> reservations = reservationRepository.findByNumberOfNights(nights);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    public List<ReservationDTO> getByPeopleAndNights(int people, int night) {
        if (people < 1 || people > 4) {
            throw new RuntimeException("Number of people must be between 1 and 4 people.");
        }
        if (night < 1) {
            throw new RuntimeException("A reservation must be at least at 1 night.");
        }

        List<Reservation> reservations = reservationRepository.findByNumberOfPeopleAndNumberOfNights(people, night);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    public List<ReservationDTO> getByStartIn(LocalDate date) {
        if (date == null) {
            throw new RuntimeException("Date cannot be null.");
        }

        List<Reservation> reservations = reservationRepository.findByStartAtGreaterThan(date);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    public List<ReservationDTO> getByFinishIn(LocalDate date) {
        if (date == null) {
            throw new RuntimeException("Date cannot be null.");
        }

        List<Reservation> reservations = reservationRepository.findByEndAtLessThan(date);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    public List<ReservationDTO> getByBetweenDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new RuntimeException("Date cannot be null.");
        }

        List<Reservation> reservations = reservationRepository.findByStartAtGreaterThanAndEndAtLessThan(start, end);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    public List<ReservationDTO> getByRoom(Long roomId) {
        if (roomId < 1) {
            throw new RuntimeException("Id cannot be null.");
        }

        List<Reservation> reservations = reservationRepository.findByRoom(roomId);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    public List<ReservationDTO> getByClient(Long clientId) {
        if (clientId < 1) {
            throw new RuntimeException("Id cannot be null.");
        }

        List<Reservation> reservations = reservationRepository.findByPerson(clientId);
        List<ReservationDTO> response = new ArrayList<>();

        if (!reservations.isEmpty()) {
            reservations.forEach(reservation -> {
                ReservationDTO transfer = ReservationDTO.builder()
                        .numberOfPeople(reservation.getNumberOfPeople())
                        .numberOfNights(reservation.getNumberOfNights())
                        .endAt(reservation.getEndAt())
                        .startAt(reservation.getStartAt())
                        .clientId(reservation.getClient().getId())
                        .roomBookedId(reservation.getRoomBooked().getId()).build();

                response.add(transfer);
            });
        }

        return response;
    }

    @Transactional
    public Reservation update(Long reservationId, ReservationDTO dto) {
        if (reservationId < 0) {
            throw new RuntimeException("Id cannot be null.");
        }

        Reservation reservationInDB = reservationRepository.findById(reservationId).orElseThrow();
        if (dto.getClientId() > 0) {
            Person client = personRepository.findById(dto.getClientId()).orElseThrow();
            reservationInDB.setClient(client);
        }
        if (dto.getRoomBookedId() > 0) {
            Room room = roomRepository.findById(dto.getRoomBookedId()).orElseThrow();
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

        validateReservation(reservationInDB);

        return reservationInDB;
    }

    @Transactional
    public void delete(Long reservationId) {
        if (reservationId < 1) {
            throw new RuntimeException("Id cannot be null.");
        }

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        reservationRepository.delete(reservation);
    }


}
