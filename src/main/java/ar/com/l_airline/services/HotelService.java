package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.AttractionDTO;
import ar.com.l_airline.domain.dto.BenefitDTO;
import ar.com.l_airline.domain.dto.HotelDTO;
import ar.com.l_airline.domain.dto.RoomDTO;
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

@Service
public class HotelService {

    private final HotelRepository hotelRepositorytory;
    private final RoomService roomService;
    private final BenefitService benefitService;
    private final AttractionService attractionService;

    public HotelService(HotelRepository repository, RoomService roomService, BenefitService benefitService, AttractionService attractionService) {
        this.hotelRepositorytory = repository;
        this.roomService = roomService;
        this.benefitService = benefitService;
        this.attractionService = attractionService;
    }

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

    @Transactional
    public Hotel createHotel (HotelDTO hotelDTO, BenefitDTO benefitDTO){
        validateHotel(hotelDTO);

        List<Room> rooms = new ArrayList<>();
        hotelDTO.getRoomsId().forEach(room -> {
            Room roomInDB = roomService.getRoomById(room);

            rooms.add(roomInDB);
        });

        Hotel hotel = Hotel.builder()
                .name(hotelDTO.getName())
                .stars(hotelDTO.getStars())
                .totalRooms(hotelDTO.getTotalRooms())
                .freeRooms(hotelDTO.getTotalRooms())
                .rooms(rooms)
                .reservedRooms(0)
                .build();

        List<Attraction> attractionsList = new ArrayList<>();
        if (!hotelDTO.getAttractionsId().isEmpty()){
            hotelDTO.getAttractionsId().forEach(attraction -> {
                Attraction attractionInDb = attractionService.getAttractionByIdObject(attraction).orElseThrow();

                attractionsList.add(attractionInDb);
            });
        }
        hotel.setAttractions(attractionsList);

        List<Benefit> benefitList = new ArrayList<>();
        if (!hotelDTO.getBenefitsId().isEmpty()){
            hotelDTO.getBenefitsId().forEach(benefit -> {
                Benefit benefitInDb = benefitService.getBenefitById(benefit);

                benefitList.add(benefitInDb);
            });
        }
        hotel.setBenefits(benefitList);

        hotelRepositorytory.save(hotel);

        return hotel;
    }

    public HotelDTO getHotelByIdDTO(Long id){
        if (id <= 0){
            throw new RuntimeException("Id cannot be null");
        }

        Hotel hotelInDb = hotelRepositorytory.findById(id).orElseThrow();

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
    public Hotel getHotelByIdObject(Long id){
        if (id <= 0){
            throw new RuntimeException("Id cannot be null");
        }

        return hotelRepositorytory.findById(id).orElseThrow();
    }

    public List<HotelDTO> getHotelByStars(double stars){
        if (stars <= 0){
            throw new RuntimeException("Stars qualification cannot be less than zero.");
        }

        List<Hotel> hotelsInDb = hotelRepositorytory.findByStars(stars);
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

    public List<HotelDTO> getHotelByName(String name){
        if (name.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Hotel> hotelsInDb = hotelRepositorytory.findByNameContaining(name);
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

    public List<HotelDTO> getHotelByBenefits(String benefitsName){
        if (benefitsName.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Hotel> hotelsInDb = hotelRepositorytory.findByBenefits(benefitsName);
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

    public List<HotelDTO> getHotelByAttraction(String attractionName){
        if (attractionName.isBlank()){
            throw new RuntimeException("Name cannot be null.");
        }

        List<Hotel> hotelsInDb = hotelRepositorytory.findByBenefits(attractionName);
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

        hotelRepositorytory.save(hotelInDb);

        return hotelInDb;
    }

    @Transactional
    public void deleteHotel(Long id){
        if (id == null){
            throw new RuntimeException("Id cannot be null");
        }

        Hotel hotelInDb = this.getHotelByIdObject(id);

        if (hotelInDb.getReservedRooms() > 0){
            throw new RuntimeException("Cannot delete a Hotel entity when have active clients.");
        }

        hotelRepositorytory.delete(hotelInDb);
    }

}
