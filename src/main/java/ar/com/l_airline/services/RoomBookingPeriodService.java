package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.RoomBookingPeriodDTO;
import ar.com.l_airline.domain.entities.Reservation;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.domain.entities.RoomBookingPeriod;
import ar.com.l_airline.domain.enums.RoomBookingStatus;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundInDatabaseException;
import ar.com.l_airline.repositories.RoomBookingPeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomBookingPeriodService {

    private final RoomBookingPeriodRepository repository;
    private final RoomService roomService;
    private final ReservationService reservationService;

    public RoomBookingPeriodService(RoomBookingPeriodRepository repository, RoomService roomService, ReservationService reservationService) {
        this.repository = repository;
        this.roomService = roomService;
        this.reservationService = reservationService;
    }

    private void validate(LocalDate startAt, LocalDate endAt, Long roomId, Long reservationId){
        if (startAt.isBefore(LocalDate.now()) || endAt.isBefore(LocalDate.now())){
            throw new MissingDataException("Cannot create registers in the past.");
        }
        if (roomId == null || roomId < 1 || reservationId == null || reservationId < 1){
            throw new MissingDataException("Insert both room and reservation id numbers.");
        }
        if (startAt.isBefore(LocalDate.now()) || endAt.isBefore(startAt)){
            throw new MissingDataException("Please, insert a valid reservation date.");
        }
    }

    public RoomBookingPeriod getByIdObject (Long id){
        if (id == null || id < 1){
            throw new MissingDataException("Insert a valid id.");
        }

        return repository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase."));
    }

    public RoomBookingPeriodDTO getByIdResponse (Long id){
        if (id == null || id < 1){
            throw new MissingDataException("Insert a valid id.");
        }

        RoomBookingPeriod result = repository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException("Register not found in the DataBase."));

        return RoomBookingPeriodDTO.builder()
                .startAt(result.getStartAt())
                .endAt(result.getEndAt())
                .status(result.getStatus())
                .roomId(result.getRoom().getId())
                .reservationId(result.getReservation().getId()).build();
    }

    private List<RoomBookingPeriodDTO> convertEntityToDTO(List<RoomBookingPeriod> list){
        return list.stream().map(roomBookingPeriod ->
                RoomBookingPeriodDTO.builder()
                .startAt(roomBookingPeriod.getStartAt())
                .endAt(roomBookingPeriod.getEndAt())
                .status(roomBookingPeriod.getStatus())
                .roomId(roomBookingPeriod.getRoom().getId())
                .reservationId(roomBookingPeriod.getReservation().getId())
                .build()).toList();
    }

    @Transactional
    public RoomBookingPeriod create(RoomBookingPeriodDTO dto){
        validate(dto.getStartAt(), dto.getEndAt(), dto.getRoomId(), dto.getReservationId());
        Room room = roomService.getRoomById(dto.getRoomId());
        Reservation reservation = reservationService.getById(dto.getReservationId());

        RoomBookingPeriod register = RoomBookingPeriod.builder()
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .status(RoomBookingStatus.RESERVED)
                .room(room)
                .reservation(reservation).build();

        repository.save(register);

        return register;
    }

    public List<RoomBookingPeriodDTO> getByStartAt(LocalDate startAt){
        if (startAt == null){
            throw new MissingDataException("Date cannot be null.");
        }

        return convertEntityToDTO(repository.findByStartAtGreaterThan(startAt));
    }

    public List<RoomBookingPeriodDTO> getByEndAt(LocalDate endAt){
        if (endAt == null || endAt.isBefore(LocalDate.now())){

            throw new MissingDataException("Date cannot be null.");
        }

        return convertEntityToDTO(repository.findByEndAtLessThan(endAt));
    }

    public List<RoomBookingPeriodDTO> getByStarAndEndBetween(LocalDate startAt, LocalDate endAt){
        if (startAt == null || endAt == null || endAt.isBefore(LocalDate.now()) || startAt.isAfter(endAt)){
            throw new MissingDataException("Start date have to be before end date. Please, insert dates data correctly.");
        }

        return convertEntityToDTO(repository.findByStartAtGreaterThanAndEndAtLessThan(startAt, endAt));
    }

    public List<RoomBookingPeriodDTO> getByStatusRegister(RoomBookingStatus status){
        if (status == null){
            throw new MissingDataException("Please, set a valid status.");
        }

        return convertEntityToDTO(repository.findByStatus(status));
    }

    public List<RoomBookingPeriodDTO> getByRoomId(Long roomId){
        if (roomId == null || roomId < 1){
            throw new MissingDataException("Insert a valid room id.");
        }

        return convertEntityToDTO(repository.findByRoom(roomId));
    }

    @Transactional
    public RoomBookingPeriodDTO updateInfo(Long roomBookingPeriodId, RoomBookingPeriodDTO dto){
        RoomBookingPeriod registerInDB = this.getByIdObject(roomBookingPeriodId);

        if (dto.getStartAt() != null){
            registerInDB.setStartAt(dto.getStartAt());
        }
        if (dto.getEndAt() != null){
            registerInDB.setEndAt(dto.getEndAt());
        }
        if (dto.getRoomId() != null && dto.getRoomId() > 0){
            Room roomInDB = roomService.getRoomById(dto.getRoomId());
            registerInDB.setRoom(roomInDB);
        }

        validate(registerInDB.getStartAt(), registerInDB.getEndAt(), registerInDB.getRoom().getId(), registerInDB.getReservation().getId());

        repository.save(registerInDB);

        return RoomBookingPeriodDTO.builder()
                .startAt(registerInDB.getStartAt())
                .endAt(registerInDB.getEndAt())
                .roomId(registerInDB.getRoom().getId())
                .reservationId(registerInDB.getReservation().getId()).build();
    }

    @Transactional
    public RoomBookingPeriodDTO updateStatus(Long roomBookingPeriodId, RoomBookingStatus status){
        RoomBookingPeriod registerInDB = this.getByIdObject(roomBookingPeriodId);

        if (status != null){
            registerInDB.setStatus(status);
        }

        repository.save(registerInDB);

        return RoomBookingPeriodDTO.builder()
                .startAt(registerInDB.getStartAt())
                .endAt(registerInDB.getEndAt())
                .status(registerInDB.getStatus())
                .roomId(registerInDB.getRoom().getId())
                .reservationId(registerInDB.getReservation().getId()).build();
    }

    @Transactional
    public void delete (Long id){
        if (id == null || id < 1){
            throw new MissingDataException("Id cannot be null.");
        }

        RoomBookingPeriod resultInDB = this.getByIdObject(id);

        if (resultInDB.getStatus() == RoomBookingStatus.RESERVED || resultInDB.getStatus() == RoomBookingStatus.COMPLETED){
            throw new MissingDataException("Only canceled or completes reservations register can be deleted.");
        }

        repository.delete(resultInDB);
    }

}