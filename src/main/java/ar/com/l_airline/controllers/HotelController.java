package ar.com.l_airline.controllers;

import ar.com.l_airline.domain.hotel.Hotel;
import ar.com.l_airline.domain.dto.HotelDTO;
import ar.com.l_airline.domain.enums.Room;
import ar.com.l_airline.domain.enums.City;
import ar.com.l_airline.exceptionHandler.custom_exceptions.ExistingObjectException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundException;
import ar.com.l_airline.services.HotelService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service) {
        this.service = service;
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @GetMapping("/byId")
    public ResponseEntity<Hotel> getById(@RequestParam Long id){
        return ResponseEntity.ok(service.findHotelById(id));
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @GetMapping("/byName")
    public ResponseEntity<List<Hotel>> getByName(@RequestParam String name){
        return ResponseEntity.ok(service.findHotelByName(name));
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @GetMapping("/byCity")
    public ResponseEntity<List<Hotel>> findByCity(@RequestParam City city){
        return ResponseEntity.ok(service.findHotelByCity(city));
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @GetMapping("/byRoom")
    public ResponseEntity<List<Hotel>> findByRoom(@RequestParam Room room){
        return ResponseEntity.ok(service.findHotelByRoom(room));
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @GetMapping("/byPrice")
    public ResponseEntity<List<Hotel>> findByPrice(@RequestParam double min, @RequestParam double max){
        return ResponseEntity.ok(service.findHotelByPrice(min, max));
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @PostMapping("/insert")
    public ResponseEntity<Hotel> createHotel(@RequestBody HotelDTO dto){
        return ResponseEntity.ok(service.createHotel(dto));
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteHotel(@RequestParam Long id){
        service.deleteHotelById(id);
        return ResponseEntity.ok("¡Hotel deleted!");
    }

    @CircuitBreaker(name = "hotelBreaker", fallbackMethod = "fallback")
    @PatchMapping("/update")
    public ResponseEntity<Hotel> updateHotel (@RequestParam Long id, @RequestBody HotelDTO dto){
        return ResponseEntity.ok(service.updateHotel(id, dto));
    }

    private ResponseEntity<String> fallback(Exception e){
        if (e instanceof NotFoundException) {
            throw new NotFoundException();
        }
        if (e instanceof ExistingObjectException) {
            throw new ExistingObjectException();
        }
        if (e instanceof MissingDataException) {
            throw new MissingDataException();
        }

        return new ResponseEntity<>("An error has occurred in our services servers. Please, try again later.", HttpStatusCode.valueOf(503));
    }
}
