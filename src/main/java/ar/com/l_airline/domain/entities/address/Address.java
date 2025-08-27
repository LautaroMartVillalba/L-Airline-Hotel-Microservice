package ar.com.l_airline.domain.entities.address;

import ar.com.l_airline.domain.entities.Hotel;
import ar.com.l_airline.domain.entities.Person;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entity_address")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nonnull
    private String street;
    @Nonnull
    private String number;
    @Nullable
    private String floor;
    @Nullable
    @Column(name = "door_number")
    private String departmentNumber;
    @ManyToOne
    private States state;
    @OneToOne(mappedBy = "address")
    private Hotel hotel;
    @OneToOne(mappedBy = "address")
    private Person person;

}
