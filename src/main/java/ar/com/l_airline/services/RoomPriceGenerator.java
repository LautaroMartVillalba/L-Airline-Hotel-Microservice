package ar.com.l_airline.services;

import ar.com.l_airline.domain.enums.BedsType;
import ar.com.l_airline.domain.enums.RoomType;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;

import java.math.BigDecimal;

public class RoomPriceGenerator {

    private static final BigDecimal BASE_ROOM_PRICE = BigDecimal.valueOf(20);

    private static float priceIncreaseByFloor (int floor){
        if (floor <= 0){
            throw new MissingDataException("Please, insert a valid floor number");
        }
        if (floor > 15){
            return 1.15F;
        }
        return (float) floor/100+1;
    }
    private static float priceIncreaseByPeopleCapacity(int peopleCapacity){
        switch (peopleCapacity){
            case 1 -> {
                return 1.10F;
            }
            case 2 -> {
                return  1.18F;
            }
            case 3 -> {
                return 1.25F;
            }
            case 4 -> {
                return 1.33F;
            }
        }
        throw new MissingDataException("Please, insert a valid people capacity.");
    }

    public static BigDecimal priceGenerator(RoomType roomType, BedsType bedsType, int floor, int peopleCapacity){
        return BASE_ROOM_PRICE
                .multiply(BigDecimal.valueOf(roomType.getMultiplier()))
                .multiply(BigDecimal.valueOf(bedsType.getMultiplier()))
                .multiply(BigDecimal.valueOf(priceIncreaseByFloor(floor)))
                .multiply(BigDecimal.valueOf(priceIncreaseByPeopleCapacity(peopleCapacity)));
    }

}
