package ar.com.l_airline.domain.entities;

import ar.com.l_airline.domain.entities.address.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Represents a person entity in the system.
 * <p>
 * A person may be associated with a reservation and contains basic personal
 * information such as name, email, age, identification (DNI), and phone number.
 * <p>
 * Mapped to the database table "entity_person".
 */
@Entity
@Table(name = "entity_person")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @NotBlank
    @Email
    @Size(min = 10)
    @Column(unique = true)
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 10)
    @Column(unique = true)
    private String dni;
    @NotNull
    @NotBlank
    @Size(min = 16, max = 52)
    private String name;
    @NotNull
    @Min(18)
    private int age;
    @Column(unique = true, name = "cell_phone_number")
    @NotNull
    @Size(min = 7)
    private String cellPhone;
    @NotNull
    private int numberOfReservations;
    @NotNull
    @OneToOne(mappedBy = "client")
    private Reservation reservation;
    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

}
