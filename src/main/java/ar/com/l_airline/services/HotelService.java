package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.AddressDTO;
import ar.com.l_airline.domain.dto.HotelDTO;
import ar.com.l_airline.domain.entities.Attraction;
import ar.com.l_airline.domain.entities.Benefit;
import ar.com.l_airline.domain.entities.Hotel;
import ar.com.l_airline.domain.entities.Room;
import ar.com.l_airline.domain.entities.address.Address;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.repositories.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final AddressService addressService;

    public HotelService(HotelRepository repository, RoomService roomService, BenefitService benefitService, AttractionService attractionService, AddressService addressService) {
        this.hotelRepository = repository;
        this.roomService = roomService;
        this.benefitService = benefitService;
        this.attractionService = attractionService;
        this.addressService = addressService;
    }

    private void validateInfo(String name, int totalRooms, String phoneNumber){
        if(name.isBlank()){
            throw new MissingDataException("Hotel name cannot be null.");
        }
        if (totalRooms <1){
            throw new MissingDataException("A hotel must have at leas one room.");
        }
        if (phoneNumber == null || phoneNumber.isBlank()){
            throw new MissingDataException("Hotel's contact cell phone number cannot be null.");
        }
    }

    private List<HotelDTO> convertFromHotelListToHotelDTOList(List<Hotel> list){
        return list.stream().map(hotel -> {
            List<Long> roomsId = hotel.getRooms().stream().map(Room::getId).toList();
            List<Long> benefitsId = hotel.getBenefits().stream().map(Benefit::getId).toList();
            List<Long> attractionsId = hotel.getAttractions().stream().map(Attraction::getId).toList();

            String countryCode = hotel.getAddress().getState().getCountryCode();
            String stateName = hotel.getAddress().getState().getSubdivision();
            String streetName = hotel.getAddress().getStreet();
            String streetNumber = hotel.getAddress().getNumber();


            return HotelDTO.builder()
                    .name(hotel.getName())
                    .stars(hotel.getStars())
                    .address(streetName + " " + streetNumber)
                    .ubication(stateName + ", " + countryCode)
                    .totalRooms(hotel.getTotalRooms())
                    .freeRooms(hotel.getTotalRooms())
                    .reservedRooms(0)
                    .contactPhone(hotel.getContactPhone())
                    .roomsId(roomsId)
                    .benefitsId(benefitsId)
                    .attractionsId(attractionsId)
                    .build();
        }).toList();
    }

    /**
     * Creates and persists a new Hotel entity based on the given DTOs.
     * Performs data validation and fetches associated entities (rooms, attractions, benefits).
     *
     * @param hotelDTO    DTO containing hotel data
     * @return The persisted Hotel entity
     */
    @Transactional
    public Hotel createHotel (HotelDTO hotelDTO, AddressDTO addressDTO){
        validateInfo(hotelDTO.getName(), hotelDTO.getTotalRooms(), hotelDTO.getContactPhone());

        // Resolve room references from IDs
        List<Room> rooms = new ArrayList<>();
        hotelDTO.getRoomsId().forEach(room -> {
            Room roomInDB = roomService.getRoomById(room);

            rooms.add(roomInDB);
        });

        Address createdAddress = addressService.createAddress(addressDTO);

        // Create hotel entity with initial values
        Hotel hotel = Hotel.builder()
                .name(hotelDTO.getName())
                .stars(0)
                .address(createdAddress)
                .totalRooms(hotelDTO.getTotalRooms())
                .freeRooms(hotelDTO.getTotalRooms())
                .contactPhone(hotelDTO.getContactPhone())
                .rooms(rooms)
                .reservedRooms(0)
                .build();

        // Attach attractions to hotel if any are provided
        List<Attraction> attractionsList = new ArrayList<>();
        if (!hotelDTO.getAttractionsId().isEmpty()){
            hotelDTO.getAttractionsId().forEach(attraction -> {
                Attraction attractionInDb = attractionService.getAttractionByIdObject(attraction);

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
     * @throws MissingDataException if the ID is less than or equal to 0, or if the hotel is not found
     */
    public HotelDTO getHotelByIdDTO(Long id){
        if (id <= 0){
            throw new MissingDataException("Id cannot be null");
        }

        Hotel hotelInDb = hotelRepository.findById(id).orElseThrow(() -> new MissingDataException("Register not found in the DataBase."));

        List<Long> roomIdList = new ArrayList<>();
        List<Long> attractionIdList = new ArrayList<>();
        List<Long> benefitsIdList = new ArrayList<>();

        hotelInDb.getRooms().forEach(room -> roomIdList.add(room.getId()));
        hotelInDb.getAttractions().forEach(attraction -> attractionIdList.add(attraction.getId()));
        hotelInDb.getBenefits().forEach(benefit -> benefitsIdList.add(benefit.getId()));

        String countryCode = hotelInDb.getAddress().getState().getCountryCode();
        String stateName = hotelInDb.getAddress().getState().getSubdivision();
        String streetName = hotelInDb.getAddress().getStreet();
        String streetNumber = hotelInDb.getAddress().getNumber();

        return HotelDTO.builder()
                .name(hotelInDb.getName())
                .stars(hotelInDb.getStars())
                .address(streetName + " " + streetNumber)
                .ubication(stateName + ", " + countryCode)
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
     * @throws MissingDataException if the ID is invalid or the hotel does not exist
     */
    public Hotel getHotelByIdObject(Long id){
        if (id <= 0){
            throw new MissingDataException("Id cannot be null");
        }

        return hotelRepository.findById(id).orElseThrow(() -> new MissingDataException("Register not found in the Database"));
    }

    /**
     * Retrieves all hotels with the specified star rating and maps them to HotelDTOs.
     * Each DTO includes associated room, attraction, and benefit IDs.
     *
     * @param stars The star rating to filter hotels by
     * @return List of HotelDTOs matching the given star rating; empty if none found
     * @throws MissingDataException if the star rating is less than or equal to zero
     */
    public List<HotelDTO> getHotelByStars(double stars){
        if (stars <= 0){
            throw new MissingDataException("Stars rating cannot be less than zero.");
        }

        return convertFromHotelListToHotelDTOList(hotelRepository.findByStars(stars));
    }

    /**
     * Retrieves a list of hotels whose names contain the specified substring.
     * Each hotel is mapped to a HotelDTO including associated entity IDs.
     *
     * @param name Partial or full hotel name
     * @return List of HotelDTOs matching the name criteria
     * @throws MissingDataException if the name is blank
     */
    public List<HotelDTO> getHotelByName(String name){
        if (name.isBlank()){
            throw new MissingDataException("Name cannot be null.");
        }

        return convertFromHotelListToHotelDTOList(hotelRepository.findByNameContaining(name));
    }

    /**
     * Retrieves hotels that offer a specific benefit by name.
     * Each hotel is mapped to a HotelDTO with associated entity IDs.
     *
     * @param benefitsName The benefit name to search for (supports partial match)
     * @return List of HotelDTOs with matching benefits
     * @throws MissingDataException if the benefit name is blank
     */
    public List<HotelDTO> getHotelByBenefits(String benefitsName){
        if (benefitsName.isBlank()){
            throw new MissingDataException("Name cannot be null.");
        }

        return convertFromHotelListToHotelDTOList(hotelRepository.findByBenefits(benefitsName));
    }

    /**
     * Retrieves hotels associated with a specific attraction by name.
     *
     * @param attractionName Name of the attraction (partial match supported)
     * @return List of HotelDTOs related to the attraction
     * @throws MissingDataException if the name is blank
     */
    public List<HotelDTO> getHotelByAttraction(String attractionName){
        if (attractionName.isBlank()){
            throw new MissingDataException("Name cannot be null.");
        }

        return convertFromHotelListToHotelDTOList(hotelRepository.findByAttractions(attractionName));
    }

    /**
     * Updates an existing hotel entity by applying only the basic field changes:
     * name, stars, and totalRooms. Associated rooms, benefits, and attractions remain unchanged.
     *
     * @param id  The ID of the hotel to update
     * @param dto DTO containing the new hotel values
     * @return The updated Hotel entity
     * @throws MissingDataException if the resulting hotel state is invalid
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

        validateInfo(hotelInDb.getName(), hotelInDb.getTotalRooms(), hotelInDb.getContactPhone());

        hotelRepository.save(hotelInDb);

        return hotelInDb;
    }

    /**
     * Deletes a hotel by its ID, only if it has no active (reserved) rooms.
     *
     * @param id The ID of the hotel to delete
     * @throws MissingDataException if the ID is null or if there are active reservations
     */
    @Transactional
    public void deleteHotel(Long id){
        if (id == null || id < 1){
            throw new MissingDataException("Id cannot be null");
        }

        Hotel hotelInDb = this.getHotelByIdObject(id);

        if (hotelInDb.getReservedRooms() > 0){
            throw new MissingDataException("Cannot delete a Hotel entity when have active clients.");
        }

        hotelRepository.delete(hotelInDb);
    }

}
