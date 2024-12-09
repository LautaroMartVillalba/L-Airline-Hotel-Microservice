package ar.com.l_airline.controllers;

import ar.com.l_airline.domain.hotel.Hotel;
import ar.com.l_airline.domain.dto.HotelDTO;
import ar.com.l_airline.domain.enums.Room;
import ar.com.l_airline.domain.enums.City;
import ar.com.l_airline.services.HotelService;
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

    @GetMapping("/byId")
    public ResponseEntity<Hotel> getById(@RequestParam Long id){
        return ResponseEntity.ok(service.findHotelById(id));
    }

    @GetMapping("/byName")
    public ResponseEntity<List<Hotel>> getByName(@RequestParam String name){
        return ResponseEntity.ok(service.findHotelByName(name));
    }

    @GetMapping("/byCity")
    public ResponseEntity<List<Hotel>> findByCity(@RequestParam City city){
        return ResponseEntity.ok(service.findHotelByCity(city));
    }

    @GetMapping("/byRoom")
    public ResponseEntity<List<Hotel>> findByRoom(@RequestParam Room room){
        return ResponseEntity.ok(service.findHotelByRoom(room));
    }

    @GetMapping("/byPrice")
    public ResponseEntity<List<Hotel>> findByPrice(@RequestParam double min, @RequestParam double max){
        return ResponseEntity.ok(service.findHotelByPrice(min, max));
    }

    @PostMapping("/insert")
    public ResponseEntity<Hotel> createHotel(@RequestBody HotelDTO dto){
        return ResponseEntity.ok(service.createHotel(dto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteHotel(@RequestParam Long id){
        service.deleteHotelById(id);
        return ResponseEntity.ok("¡Hotel deleted!");
    }

    @PatchMapping("/update")
    public ResponseEntity<Hotel> updateHotel (@RequestParam Long id, @RequestBody HotelDTO dto){
        return ResponseEntity.ok(service.updateHotel(id, dto));
    }
}
