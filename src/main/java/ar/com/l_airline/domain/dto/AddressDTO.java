package ar.com.l_airline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ar.com.l_airline.domain.entities.address.Address;

/**
 * Data Transfer Object for {@link Address}.
 * <p>
 * Used to transfer address data between layers without exposing the entity directly.
 * Contains basic address fields and references to associated hotel, person, and state.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressDTO {

    private Long id;
    private String street;
    private String number;
    private String floor;
    private String departmentNumber;
    private String stateId;
    private String subdivisionName;
    private Long hotelId;
    private Long personId;

}
