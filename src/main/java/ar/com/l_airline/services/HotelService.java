package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.HotelDTO;
import ar.com.l_airline.domain.entities.Attraction;
import ar.com.l_airline.domain.entities.Benefit;
import ar.com.l_airline.domain.entities.Hotel;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.repositories.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class responsible for business logic related to Hotel entities.
 * Handles creation and validation of hotels, as well as managing associations with rooms,
 * benefits, and attractions.
 */
@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomService roomService;
    private final BenefitService benefitService;
    private final AttractionService attractionService;

    public HotelService(HotelRepository repository, RoomService roomService, BenefitService benefitService, AttractionService attractionService) {
        this.hotelRepository = repository;
        this.roomService = roomService;
        this.benefitService = benefitService;
        this.attractionService = attractionService;
    }

    /**
     * Validates the data of a HotelDTO before creating the Hotel entity.
     * Throws a RuntimeException if any constraint is violated.
     *
     * @param dto Data Transfer Object containing hotel data
     */
    private void validateHotel(HotelDTO dto){
        if(dto.getName().isBlank()){
            throw new RuntimeException("Hotel name cannot be null.");
        }
        if (dto.getTotalRooms() <1){
            throw new RuntimeException("A hotel must have at leas one room.");
        }
        if (dto.getFreeRooms() > dto.getTotalRooms()){
            throw new RuntimeException("Free rooms cannot be more than total rooms.");
        }
        if (dto.getReservedRooms() > dto.getTotalRooms()){
            throw new RuntimeException("Reserved rooms cannot be more than total rooms.");
        }
        if ((dto.getReservedRooms() + dto.getFreeRooms()) > dto.getTotalRooms()){
            throw new RuntimeException("The sum of free and reserved rooms cannot be more than total rooms.");
        }
        if (dto.getRoomsId().isEmpty()){
            throw new RuntimeException("A Hotel must have at least one room.");
        }
    }
    /**
     * Validates the data of a Hotel entity.
     * Throws a RuntimeException if any constraint is violated.
     *
     * @param obj Hotel entity to be validated
     */
    private void validateHotel(Hotel obj){
        if(obj.getName().isBlank()){
            throw new RuntimeException("Hotel name cannot be null.");
        }
        if (obj.getTotalRooms() <1){
            throw new RuntimeException("A hotel must have at leas one room.");
        }
        if (obj.getFreeRooms() > obj.getTotalRooms()){
            throw new RuntimeException("Free rooms cannot be more than total rooms.");
        }
        if (obj.getReservedRooms() > obj.getTotalRooms()){
            throw new RuntimeException("Reserved rooms cannot be more than total rooms.");
        }
        if ((obj.getReservedRooms() + obj.getFreeRooms()) > obj.getTotalRooms()){
            throw new RuntimeException("The sum of free and reserved rooms cannot be more than total rooms.");
        }
    }

    /**
     * Creates and persists a new Hotel entity based on the given DTOs.
     * Performs data validation and fetches associated entities (rooms, attractions, benefits).
     *
     * @param hotelDTO    DTO containing hotel data
     * @return The persisted Hotel entity
     */
    @Transactional
    public Hotel createHotel (HotelDTO hotelDTO){
        validateHotel(hotelDTO);

        // Resolve room references from IDs
        List<Room> rooms = new ArrayList<>();
        hotelDTO.getRoomsId().forEach(room -> {
            Room roomInDB = roomService.getRoomById(room);

            rooms.add(roomInDB);
        });

        // Create hotel entity with initial values
        Hotel hotel = Hotel.builder()
                .name(hotelDTO.getName())
                .stars(hotelDTO.getStars())
                .totalRooms(hotelDTO.getTotalRooms())
                .freeRooms(hotelDTO.getTotalRooms())
                .rooms(rooms)
                .reservedRooms(0)
                .build();

        // Attach attractions to hotel if any are provided
        List<Attraction> attractionsList = new ArrayList<>();
        if (!hotelDTO.getAttractionsId().isEmpty()){
            hotelDTO.getAttractionsId().forEach(attraction -> {
                Attraction attractionInDb = attractionService.getAttractionByIdObject(attraction).orElseThrow();

                attractionsList.add(attractionInDb);
            });
        }
        hotel.setAttractions(attractionsList);

        // Attach benefits to hotel if any are provided
        List<Benefit> benefitList = new ArrayList<>();
        if (!hotelDTO.getBenefitsId().isEmpty()){
            hotelDTO.getBenefitsId().forEach(benefit -> {
                Benefit benefitInDb = benefitService.getBenefitByIdObject(benefit);

                benefitList.add(benefitInDb);
            });
        }
        hotel.setBenefits(benefitList);

        hotelRepository.save(hotel);

        return hotel;
    }

        /**
     * Retrieves a hotel by its ID and maps it to a HotelDTO object.
     * Extracts associated room IDs, attraction IDs, and benefit IDs for inclusion in the DTO.
     *
     * @param id The ID of the hotel to retrieve
     * @return A HotelDTO representing the retrieved hotel
     * @throws RuntimeException if the ID is less than or equal to 0, or if the hotel is not found
     */
    public HotelDTO getHotelByIdDTO(Long id){
        if (id <= 0){
            throw new RuntimeException("Id cannot be null");
        }

        Hotel hotelInDb = hotelRepository.findById(id).orElseThrow();

        List<Long> roomIdList = new ArrayList<>();
        List<Long> attractionIdList = new ArrayList<>();
        List<Long> benefitsIdList = new ArrayList<>();

        hotelInDb.getRooms().forEach(room -> roomIdList.add(room.getId()));
        hotelInDb.getAttractions().forEach(attraction -> attractionIdList.add(attraction.getId()));
        hotelInDb.getBenefits().forEach(benefit -> benefitsIdList.add(benefit.getId()));

        return HotelDTO.builder()
                .name(hotelInDb.getName())
                .stars(hotelInDb.getStars())
                .freeRooms(hotelInDb.getFreeRooms())
                .roomsId(roomIdList)
                .attractionsId(attractionIdList)
                .benefitsId(benefitsIdList)
                .build();
    }
    /**
     * Retrieves a Hotel entity by its ID.
     *
     * @param id The ID of the hotel to retrieve
     * @return The corresponding Hotel entity
     * @throws RuntimeException if the ID is invalid or the hotel does not exist
     */
    public Hotel getHotelByIdObject(Long id){
        if (id <= 0){
            throw new RuntimeException("Id cannot be null");
        }

        return hotelRepository.findById(id).orElseThrow();
    }

    /**
     * Retrieves all hotels with the specified star rating and maps them to HotelDTOs.
     * Each DTO includes associated room, attraction, and benefit IDs.
     *
     * @param stars The star rating to filter hotels by
     * @return List of HotelDTOs matching the given star rating; empty if none found
     * @throws RuntimeException if the star rating is less than or equal to zero
     */
    public List<HotelDTO> getHotelByStars(double stars){
        if (stars <= 0){
            throw new RuntimeException("Stars rating cannot be less than zero.");
        }

        List<Hotel> hotelsInDb = hotelRepository.findByStars(stars);
        List<HotelDTO> retrieveList = new ArrayList<>();

        if (hotelsInDb.isEmpty()){
            return Collections.emptyList();
        }

        hotelsInDb.forEach(hotel -> {
            // Prepare lists of associated entity IDs
            List<Long> roomIdList = new ArrayList<>();
            List<Long> attractionIdList = new ArrayList<>();
            List<Long> benefitsIdList = new ArrayList<>();

            hotel.getRooms().forEach(room -> roomIdList.add(room.getId()));
            hotel.getAttractions().forEach(attraction -> attractionIdList.add(attraction.getId()));
            hotel.getBenefits().forEach(benefit -> benefitsIdList.add(benefit.getId()));

            // Build DTO for each hotel and add to result list
            HotelDTO dto = HotelDTO.builder()
                    .name(hotel.getName())
                    .stars(hotel.getStars())
                    .reservedRooms(hotel.getReservedRooms())
                    .totalRooms(hotel.getTotalRooms())
                    .freeRooms(hotel.getFreeRooms())
                    .roomsId(roomIdList)
                    .attractionsId(attractionIdList)
                    .benefitsId(benefitsIdList).build();

            retrieveList.add(dto);
        });

        return retrieveList;
    }

    /**
     * Retrieves a list of hotels whose names contain the specified substring.
     * Each hotel is mapped to a HotelDTO including associated entity IDs.
     *
     * @param name Partial or full hotel name
     * @return List of HotelDTOs matching the name criteria
     * @throws RuntimeException if the name is blank
     */
    public List<HotelDTO> getHotelByName(String name){
        if (name.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Hotel> hotelsInDb = hotelRepository.findByNameContaining(name);
        List<HotelDTO> retrieveList = new ArrayList<>();

        if (hotelsInDb.isEmpty()){
            return Collections.emptyList();
        }

        hotelsInDb.forEach(hotel -> {
            List<Long> roomIdList = new ArrayList<>();
            List<Long> attractionIdList = new ArrayList<>();
            List<Long> benefitsIdList = new ArrayList<>();

            hotel.getRooms().forEach(room -> roomIdList.add(room.getId()));
            hotel.getAttractions().forEach(attraction -> attractionIdList.add(attraction.getId()));
            hotel.getBenefits().forEach(benefit -> benefitsIdList.add(benefit.getId()));

            HotelDTO dto = HotelDTO.builder()
                    .name(hotel.getName())
                    .stars(hotel.getStars())
                    .reservedRooms(hotel.getReservedRooms())
                    .totalRooms(hotel.getTotalRooms())
                    .freeRooms(hotel.getFreeRooms())
                    .roomsId(roomIdList)
                    .attractionsId(attractionIdList)
                    .benefitsId(benefitsIdList).build();

            retrieveList.add(dto);
        });

        return retrieveList;
    }

    /**
     * Retrieves hotels that offer a specific benefit by name.
     * Each hotel is mapped to a HotelDTO with associated entity IDs.
     *
     * @param benefitsName The benefit name to search for (supports partial match)
     * @return List of HotelDTOs with matching benefits
     * @throws RuntimeException if the benefit name is blank
     */
    public List<HotelDTO> getHotelByBenefits(String benefitsName){
        if (benefitsName.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Hotel> hotelsInDb = hotelRepository.findByBenefits(benefitsName);
        List<HotelDTO> retrieveList = new ArrayList<>();

        if (hotelsInDb.isEmpty()){
            return Collections.emptyList();
        }

        hotelsInDb.forEach(hotel -> {
            List<Long> roomIdList = new ArrayList<>();
            List<Long> attractionIdList = new ArrayList<>();
            List<Long> benefitsIdList = new ArrayList<>();

            hotel.getRooms().forEach(room -> roomIdList.add(room.getId()));
            hotel.getAttractions().forEach(attraction -> attractionIdList.add(attraction.getId()));
            hotel.getBenefits().forEach(benefit -> benefitsIdList.add(benefit.getId()));

            HotelDTO dto = HotelDTO.builder()
                    .name(hotel.getName())
                    .stars(hotel.getStars())
                    .reservedRooms(hotel.getReservedRooms())
                    .totalRooms(hotel.getTotalRooms())
                    .freeRooms(hotel.getFreeRooms())
                    .roomsId(roomIdList)
                    .attractionsId(attractionIdList)
                    .benefitsId(benefitsIdList).build();

            retrieveList.add(dto);
        });

        return retrieveList;
    }

    /**
     * Retrieves hotels associated with a specific attraction by name.
     *
     * @param attractionName Name of the attraction (partial match supported)
     * @return List of HotelDTOs related to the attraction
     * @throws RuntimeException if the name is blank
     */
    public List<HotelDTO> getHotelByAttraction(String attractionName){
        if (attractionName.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Hotel> hotelsInDb = hotelRepository.findByAttractions(attractionName);
        List<HotelDTO> retrieveList = new ArrayList<>();

        if (hotelsInDb.isEmpty()){
            return Collections.emptyList();
        }

        hotelsInDb.forEach(hotel -> {
            List<Long> roomIdList = new ArrayList<>();
            List<Long> attractionIdList = new ArrayList<>();
            List<Long> benefitsIdList = new ArrayList<>();

            hotel.getRooms().forEach(room -> roomIdList.add(room.getId()));
            hotel.getAttractions().forEach(attraction -> attractionIdList.add(attraction.getId()));
            hotel.getBenefits().forEach(benefit -> benefitsIdList.add(benefit.getId()));

            HotelDTO dto = HotelDTO.builder()
                    .name(hotel.getName())
                    .stars(hotel.getStars())
                    .reservedRooms(hotel.getReservedRooms())
                    .totalRooms(hotel.getTotalRooms())
                    .freeRooms(hotel.getFreeRooms())
                    .roomsId(roomIdList)
                    .attractionsId(attractionIdList)
                    .benefitsId(benefitsIdList).build();

            retrieveList.add(dto);
        });

        return retrieveList;
    }

    /**
     * Updates an existing hotel entity by applying only the basic field changes:
     * name, stars, and totalRooms. Associated rooms, benefits, and attractions remain unchanged.
     *
     * @param id  The ID of the hotel to update
     * @param dto DTO containing the new hotel values
     * @return The updated Hotel entity
     * @throws RuntimeException if the resulting hotel state is invalid
     */
    @Transactional
    public Hotel updateHotelWithoutModifyBenefitsRoomsOrAttractions(Long id, HotelDTO dto){
        Hotel hotelInDb = this.getHotelByIdObject(id);

        if (!dto.getName().isBlank()){
            hotelInDb.setName(dto.getName());
        }
        if (dto.getStars() > 0){
            hotelInDb.setStars(dto.getStars());
        }
        if (dto.getTotalRooms() > 0){
            hotelInDb.setTotalRooms(dto.getTotalRooms());
        }

        validateHotel(hotelInDb);

        hotelRepository.save(hotelInDb);

        return hotelInDb;
    }

    /**
     * Deletes a hotel by its ID, only if it has no active (reserved) rooms.
     *
     * @param id The ID of the hotel to delete
     * @throws RuntimeException if the ID is null or if there are active reservations
     */
    @Transactional
    public void deleteHotel(Long id){
        if (id == null){
            throw new RuntimeException("Id cannot be null");
        }

        Hotel hotelInDb = this.getHotelByIdObject(id);

        if (hotelInDb.getReservedRooms() > 0){
            throw new RuntimeException("Cannot delete a Hotel entity when have active clients.");
        }

        hotelRepository.delete(hotelInDb);
    }

}
