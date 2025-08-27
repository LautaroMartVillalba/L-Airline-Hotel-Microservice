package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.AddressDTO;
import ar.com.l_airline.domain.entities.Hotel;
import ar.com.l_airline.domain.entities.Person;
import ar.com.l_airline.domain.entities.address.Address;
import ar.com.l_airline.domain.entities.address.States;
import ar.com.l_airline.exceptionHandler.custom_exceptions.ConflictStateException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundInDatabaseException;
import ar.com.l_airline.repositories.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final StatesService statesService;
    private final HotelService hotelService;
    private final PersonService personService;

    public AddressService(AddressRepository addressRepository, StatesService statesService, HotelService hotelService, PersonService personService) {
        this.addressRepository = addressRepository;
        this.statesService = statesService;
        this.hotelService = hotelService;
        this.personService = personService;
    }

    private void validate(String street, String number, String stateCode, Long personId, Long hotelId){
        if (hotelId != null && personId != null){
            throw new MissingDataException("An address registry only cant point to Person or Hotel entity, never both.");
        }
        if (hotelId == null && personId == null){
            throw new MissingDataException("An Address must point to Person or Hotel entity at least.");
        }
        if (stateCode.isBlank()){
            throw new MissingDataException("An Address must point to a state.");
        }
        if (street.isBlank()){
            throw new MissingDataException("Please, insert the Street data.");
        }
        if (number.isBlank()){
            throw new MissingDataException("Please, insert the house number data.");
        }
    }

    private List<AddressDTO> parseFromAddressEntityToAddressDTO(List<Address> list){
        return list.stream().map(address ->
             AddressDTO.builder()
                     .street(address.getStreet())
                     .number(address.getNumber())
                     .floor(address.getFloor())
                     .departmentNumber(address.getDepartmentNumber())
                     .stateId(address.getState().getCode())
                     .subdivisionName(address.getState().getSubdivision())
                     .hotelId(address.getHotel().getId())
                     .personId(address.getPerson().getId()).build()
        ).toList();
    }

    @Transactional
    public Address createAddress(AddressDTO dto){
        validate(dto.getStreet(), dto.getStreet(), dto.getStateId(), dto.getPersonId(), dto.getHotelId());

        Address address = Address.builder().build();

        if (dto.getHotelId() != null && dto.getPersonId() != null){
            throw new ConflictStateException("An address only can point to Hotel or Person entity.");
        }

        if (dto.getHotelId() != null){
            Hotel hotel = hotelService.getHotelByIdObject(dto.getHotelId());
            address.setHotel(hotel);
        }
        if (dto.getPersonId() != null) {
            Person person = personService.getPersonByIdObject(dto.getPersonId()).orElseThrow(()-> new NotFoundInDatabaseException("Cannot found a Person register with that ID."));
            address.setPerson(person);
        }

        States state = statesService.getStateByCodeObject(dto.getStateId());

        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setFloor(dto.getFloor());
        address.setDepartmentNumber(dto.getDepartmentNumber());
        address.setState(state);

        addressRepository.save(address);

        return address;
    }

    public Address getAddressByIdEntity(Long id){
        if (id == null || id < 1){
            throw new MissingDataException("Insert a valid ID value.");
        }

        return addressRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException("Cannot found this register in the DataBase."));
    }

    public AddressDTO getAddressByIdResponse(Long id){
        if (id == null || id < 1){
            throw new MissingDataException("Insert a valid ID value.");
        }

        Address result = addressRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException("Cannot found this register in the DataBase."));

        return AddressDTO.builder()
                .street(result.getStreet())
                .number(result.getNumber())
                .floor(result.getFloor())
                .departmentNumber(result.getDepartmentNumber())
                .stateId(result.getState().getCode())
                .subdivisionName(result.getState().getSubdivision())
                .hotelId(result.getHotel().getId())
                .personId(result.getPerson().getId()).build();
    }

    public List<AddressDTO> getAddressByLocationInDepartment(String street, String number,String cityCode, String floor, String departmentNumber){
        if (street.isBlank() || number.isBlank() || cityCode.isBlank() || floor.isBlank() || departmentNumber.isBlank()){
            throw new MissingDataException("Please, insert all required data to identify a department on the DataBase.");
        }

        return parseFromAddressEntityToAddressDTO(addressRepository.findByStateAndStreetAndNumberAndFloorAndDepartmentNumber(street, number, cityCode, floor, departmentNumber));
    }

    public List<AddressDTO> getAddressByLocationInHouse(String street, String number,String cityCode){
        if (street.isBlank() || number.isBlank() || cityCode.isBlank()){
            throw new MissingDataException("Please, insert all required data to identify a house on the DataBase.");
        }

        return parseFromAddressEntityToAddressDTO(addressRepository.findByStreetAndNumberAndCityCode(street, number, cityCode));
    }

    @Transactional
    public AddressDTO updateAddress(Long id, AddressDTO dto){
        if (id == null || id < 1){
            throw new MissingDataException("Insert a valid ID value.");
        }

        Address addressInDb = this.getAddressByIdEntity(id);

        if (!dto.getStreet().isBlank()){
            addressInDb.setStreet(dto.getStreet());
        }
        if (!dto.getNumber().isBlank()){
            addressInDb.setNumber(dto.getNumber());
        }
        if (!dto.getFloor().isBlank()){
            addressInDb.setFloor(dto.getFloor());
        }
        if (!dto.getDepartmentNumber().isBlank()){
            addressInDb.setDepartmentNumber(dto.getDepartmentNumber());
        }

        validate(addressInDb.getStreet(), addressInDb.getStreet(), addressInDb.getState().getCode(), addressInDb.getPerson().getId(), addressInDb.getHotel().getId());

        addressRepository.save(addressInDb);

        return AddressDTO.builder()
                .street(addressInDb.getStreet())
                .number(addressInDb.getNumber())
                .floor(addressInDb.getFloor())
                .departmentNumber(addressInDb.getDepartmentNumber())
                .stateId(addressInDb.getState().getCode())
                .subdivisionName(addressInDb.getState().getSubdivision())
                .hotelId(addressInDb.getHotel().getId())
                .personId(addressInDb.getPerson().getId()).build();
    }

    @Transactional
    public void deleteAddress(Long id){
        if (id == null || id < 1){
            throw new MissingDataException("Insert a valid ID value.");
        }

        addressRepository.delete(getAddressByIdEntity(id));
    }

}
