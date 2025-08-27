package ar.com.l_airline.domain.entities.address;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "entity_states")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class States {

    @Id
    private String code;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "subdivision_name")
    private String subdivision;
    @OneToMany(mappedBy = "state")
    private List<Address> addresses;

}
