package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.RoomDTO;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.domain.enums.BedsType;
import ar.com.l_airline.domain.enums.RoomState;
import ar.com.l_airline.domain.enums.RoomType;
import ar.com.l_airline.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class responsible for handling business logic related to {@link ar.com.l_airline.domain.entities.Room}.
 *
 * <p>It provides CRUD methods.</p>
 *
 * <p>Constraints are enforced to maintain room validity based on predefined conditions, and DTOs are
 * used to transfer room data between layers.</p>
 */
@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    private static final int MIN_BEDS = 1;
    private static final int MAX_BEDS = 4;
    private static final int MIN_PEOPLE = 1;
    private static final int MAX_PEOPLE = 4;

    /**
     * Validates a {@link RoomDTO} based on room business constraints.
     * Throws {@link RuntimeException} if any validation rule is violated.
     *
     * @param room the DTO containing room data to validate
     */
    private void checkIfRoomIsValid(RoomDTO room){
        if (room.getNumberOfBeds() <= MIN_BEDS || room.getNumberOfBeds() > MAX_BEDS){
            throw new RuntimeException("Rooms only can have 1 to 4 beds.");
        }
        if (room.getPeopleCapacity() <= MIN_PEOPLE || room.getPeopleCapacity() > MAX_PEOPLE){
            throw new RuntimeException("Rooms only accept 1 to 4 people.");
        }
        if (room.getRoomType() == null){
            throw new RuntimeException("Room category cannot be null.");
        }
        //If you see a warning here, it is because of your IDE.
        if (room.getBedType().equals(BedsType.KING_BED) && room.getNumberOfBeds() != 1){
            throw new RuntimeException("Only one king bed per room.");
        }
        //If you see a warning here, it is because of your IDE.
        if (room.getBedType().equals(BedsType.QUEEN_BED) && room.getNumberOfBeds() != 1){
            throw new RuntimeException("Only one queen bed per room.");
        }
        if (room.getBedType().equals(BedsType.DOUBLE_BED) && room.getNumberOfBeds() > 2){
            throw new RuntimeException("Only two double bed per room.");
        }
    }

    public List<RoomDTO> parseFromRoomListToRoomDTOList(List<Room> rooms){
        List<RoomDTO> response = new ArrayList<>();

        rooms.forEach(room -> {
            RoomDTO transfer = RoomDTO.builder()
                    .id(room.getId())
                    .floor(room.getFloor())
                    .peopleCapacity(room.getPeopleCapacity())
                    .numberOfBeds(room.getNumberOfBeds())
                    .bedType(room.getBedType())
                    .state(room.getState()).build();

            response.add(transfer);
        });

        return response;
    }

    /**
     * Creates and saves a new Room entity in the database.
     * The room is validated before persistence.
     *
     * @param room the DTO containing room data
     * @return the created Room entity
     */
    @Transactional
    public Room createRoom(RoomDTO room){
        checkIfRoomIsValid(room);

        Room newRoom = Room.builder()
                .peopleCapacity(room.getPeopleCapacity())
                .roomType(room.getRoomType())
                .bedType(room.getBedType())
                .numberOfBeds(room.getNumberOfBeds())
                .state(room.getState())
                .timeWasBooked(0).build();

        repository.save(newRoom);

        return newRoom;
    }

    /**
     * Retrieves a room by its ID and returns a RoomDTO response.
     *
     * @param id the room ID
     * @return DTO containing room data
     */
    public RoomDTO getRoomByIdResponse(Long id){
        Room result = repository.findById(id).orElseThrow(RuntimeException::new);

        return RoomDTO.builder()
                .id(result.getId())
                .floor(result.getFloor())
                .peopleCapacity(result.getPeopleCapacity())
                .numberOfBeds(result.getNumberOfBeds())
                .bedType(result.getBedType())
                .state(result.getState()).build();
    }

    /**
     * Retrieves a Room entity by its ID.
     *
     * @param id the room ID
     * @return the Room entity
     */
    public Room getRoomById(Long id){
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }

    /**
     * Returns a list of rooms that match a given number of beds.
     *
     * @param number number of beds (1-4)
     * @return list of RoomDTOs
     */
    public List<RoomDTO> getRoomsByBedsNumber(int number){
        if (number < 1 || number > 4){
            throw new RuntimeException("No room will have less than 1 bed or more than 4 beds.");
        }

        List<Room> result = repository.findByNumberOfBeds(number);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromRoomListToRoomDTOList(result);
    }

    /**
     * Returns rooms filtered by bed type.
     *
     * @param bedsType the bed type
     * @return list of RoomDTOs
     */
    public List<RoomDTO> getRoomsByBedsTypes(BedsType bedsType){
        List<Room> result = repository.findByBedType(bedsType);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromRoomListToRoomDTOList(result);
    }

    /**
     * Returns rooms filtered by people capacity.
     *
     * @param people number of people (1-4)
     * @return list of RoomDTOs
     */
    public List<RoomDTO> getRoomsByPeopleCapacity(int people){
        if (people < 1 || people > 4){
            throw new RuntimeException("A room only can accommodate between 1 and 4 people over 13 years old.");
        }
        List<Room> result = repository.findByPeopleCapacity(people);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromRoomListToRoomDTOList(result);
    }

    /**
     * Returns rooms filtered by {@link RoomType}.
     *
     * @param roomType the type of room
     * @return list of RoomDTOs
     */
    public List<RoomDTO> getRoomsByRoomType(RoomType roomType){
        if (roomType == null){
            throw new RuntimeException("You must search a valid type of room.");
        }

        List<Room> result = repository.findByRoomType(roomType);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromRoomListToRoomDTOList(result);
    }

    /**
     * Returns rooms filtered by {@link RoomState}.
     *
     * @param state the room state
     * @return list of RoomDTOs
     */
    public List<RoomDTO> getRoomsByState(RoomState state){
        if (state == null){
            throw new RuntimeException("You must search a valid room state.");
        }

        List<Room> result = repository.findByState(state);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromRoomListToRoomDTOList(result);
    }

    /**
     * Retrieves a list of RoomDTOs associated with a specific hotel ID.
     *
     * @param hotelId the ID of the hotel to filter rooms by
     * @return a list of RoomDTOs that belong to the specified hotel;
     *         returns an empty list if no rooms are found
     * @throws RuntimeException if the provided hotelId is null
     */
    List<RoomDTO> getByHotelId(Long hotelId){
        if(hotelId == null){
            throw new RuntimeException("Id cannot be null");
        }

        List<Room> result = repository.findByHotel(hotelId);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromRoomListToRoomDTOList(result);
    }

    /**
     * Retrieves a list of RoomDTOs associated with a specific reservation ID.
     *
     * @param reservationId the ID of the reservation to filter rooms by
     * @return a list of RoomDTOs that are linked to the specified reservation;
     *         returns an empty list if no rooms are found
     * @throws RuntimeException if the provided reservationId is null
     */
    List<RoomDTO> getByReservationId(Long reservationId){
        if(reservationId == null){
            throw new RuntimeException("Id cannot be null");
        }

        List<Room> result = repository.findByReservation(reservationId);

        if (result.isEmpty()){
            return Collections.emptyList();
        }

        return parseFromRoomListToRoomDTOList(result);
    }

    /**
     * Updates modifiable fields of a room based on the provided DTO.
     * Handles bed type and number validation consistency.
     *
     * @param id the room ID
     * @param dto DTO containing the new data
     * @return the updated Room entity
     */
    @Transactional
    public Room updateRoomInfoById(Long id, RoomDTO dto){
        Room room = this.getRoomById(id);

        if ((dto.getNumberOfBeds() != 0 && dto.getBedType() == null)
                || (dto.getNumberOfBeds() == 0 && dto.getBedType() != null)){
            throw new RuntimeException("When beds type or number of beds were changed you must specify the two datas.");
        }
        if(dto.getBedType() != null){
            if ((dto.getBedType().equals(BedsType.KING_BED) || dto.getBedType().equals(BedsType.QUEEN_BED))
                    && (dto.getNumberOfBeds() == 1)){
                room.setBedType(dto.getBedType());
                room.setNumberOfBeds(dto.getNumberOfBeds());
            }
            if (dto.getBedType().equals(BedsType.DOUBLE_BED)
                    && (dto.getNumberOfBeds() < 3 && dto.getNumberOfBeds() > 0)){
                room.setBedType(dto.getBedType());
                room.setNumberOfBeds(dto.getNumberOfBeds());
            }
        }
        if(dto.getState() != null){
            room.setState(dto.getState());
        }
        if(dto.getRoomType() != null){
            room.setRoomType(dto.getRoomType());
        }

        repository.save(room);
        return room;
    }

    @Transactional
    public void changeRoomState(Long roomId, RoomState state){
        if (roomId == null || state == null){
            throw new RuntimeException("Insert all data to update room state");
        }

        Room roomInDb = this.getRoomById(roomId);
        roomInDb.setState(state);

        repository.save(roomInDb);
    }

    /**
     * Deletes a room by its ID only if it is currently FREE.
     *
     * @param roomId the ID of the room to delete
     */
    @Transactional
    public void deleteRoom(Long roomId){
        Room roomInDbB = this.getRoomById(roomId);

        if (roomInDbB.getState() == RoomState.FREE){
            repository.deleteById(roomId);
        }
        else {
            throw new RuntimeException("Cannot delete a room if it not free.");
        }
    }
}
