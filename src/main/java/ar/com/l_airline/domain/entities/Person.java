package ar.com.l_airline.domain.entities;

import jakarta.persistence.*;
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
    private String email;
    private String dni;
    private String name;
    private int age;
    @Column(unique = true)
    private String cellPhone;
    private int numberOfReservations;
    @OneToOne(mappedBy = "client")
    private Reservation reservation;

}
