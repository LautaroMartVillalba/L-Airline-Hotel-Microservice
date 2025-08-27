package ar.com.l_airline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
