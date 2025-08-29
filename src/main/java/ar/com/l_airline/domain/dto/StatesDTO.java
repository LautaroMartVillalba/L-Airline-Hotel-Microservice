package ar.com.l_airline.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import ar.com.l_airline.domain.entities.address.States;

/**
 * Data Transfer Object for {@link States}.
 * <p>
 * Used to transfer state (or province) data between layers without exposing the entity directly.
 * Contains basic state information and a list of associated address IDs.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StatesDTO {
    private String code;
    private String countryCode;
    private String subdivision;
    private List<Long> addresses;
}
