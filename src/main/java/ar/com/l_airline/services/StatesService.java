package ar.com.l_airline.services;

import ar.com.l_airline.domain.dto.AddressDTO;
import ar.com.l_airline.domain.dto.StatesDTO;
import ar.com.l_airline.domain.entities.address.Address;
import ar.com.l_airline.domain.entities.address.States;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundInDatabaseException;
import ar.com.l_airline.repositories.StateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatesService {
    private final StateRepository stateRepository;

    public StatesService(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    private List<StatesDTO> parseFormStateEntityToStateDTO(List<States> list){
        return list.stream().map(states -> StatesDTO.builder()
                                                           .code(states.getCode())
                                                           .subdivision(states.getSubdivision())
                                                           .countryCode(states.getCountryCode()).build())
                .toList();
    }

    public StatesDTO getStateByCodeResponse(String stateCode){
        if (stateCode.isBlank()){
            throw new MissingDataException("Insert a state' code, please.");
        }

        States states = stateRepository.findByCode(stateCode).orElseThrow(() -> new NotFoundInDatabaseException("The resource cannot be found in the DataBase."));

        return StatesDTO.builder()
                .code(states.getCode())
                .countryCode(states.getCountryCode())
                .subdivision(states.getSubdivision()).build();
    }
    public States getStateByCodeObject(String stateCode){
        if (stateCode.isBlank()){
            throw new MissingDataException("Insert a state code, please.");
        }

        return stateRepository.findByCode(stateCode).orElseThrow(() -> new NotFoundInDatabaseException("The resource cannot be found in the DataBase."));
    }

    public List<StatesDTO> getStateByCountryCode(String countryCode){
        if (countryCode.isBlank()){
            throw new MissingDataException("Insert a country code, please.");
        }

        return parseFormStateEntityToStateDTO(stateRepository.findByCountryCode(countryCode));
    }

    public List<StatesDTO> getStateBySubdivisionName(String subdivisionName){
        if (subdivisionName.isBlank()){
            throw new MissingDataException("Insert a state subdivision same, please.");
        }

        return parseFormStateEntityToStateDTO(stateRepository.findBySubdivisionContaining(subdivisionName));
    }
}
