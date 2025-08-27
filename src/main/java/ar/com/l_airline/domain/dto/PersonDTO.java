package ar.com.l_airline.domain.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class PersonDTO {

    private Long id;
    private String email;
    private String dni;
    private String name;
    private int age;
    private String cellPhone;
    private int numberOfReservations;
    private Long reservationId;
    private String ubication;
    private String address;

}
