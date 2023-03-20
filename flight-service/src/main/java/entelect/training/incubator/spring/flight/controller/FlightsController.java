package entelect.training.incubator.spring.flight.controller;

import entelect.training.incubator.spring.flight.model.Flight;
import entelect.training.incubator.spring.flight.model.FlightsSearchRequest;
import entelect.training.incubator.spring.flight.service.FlightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerAdvice
@RequestMapping("flights")
@CrossOrigin(origins ={"http://localhost:4200"}, methods={RequestMethod.GET, RequestMethod.POST})
public class FlightsController {

    private final Logger LOGGER = LoggerFactory.getLogger(FlightsController.class);

    private final FlightsService flightsService;

    public FlightsController(FlightsService flightsService) {
        this.flightsService = flightsService;
    }

    @Operation(summary = "Create a new flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight created successfully")
    })
    @PostMapping
    public ResponseEntity<?> createFlight(@RequestBody Flight flight) {
        LOGGER.info("Processing flight creation request for flight={}", flight);

        final Flight savedFlight = flightsService.createFlight(flight);

        LOGGER.trace("Flight created");
        return new ResponseEntity<>(savedFlight, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all flights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flights retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No flights available")
    })
    @GetMapping()
    public ResponseEntity<?> getFlights() {
        LOGGER.info("Fetching all flights");
        List<Flight> flights = this.flightsService.getFlights();

        if (!flights.isEmpty()) {
            LOGGER.trace("Found flights");
            return new ResponseEntity<>(flights, HttpStatus.OK);
        }

        LOGGER.trace("No flights found");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get flight by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Flight does not exist")
    })
    @GetMapping("{id}")
    public ResponseEntity<?> getFlightById(@PathVariable Integer id) {
        LOGGER.info("Processing flight search request for flight id={}", id);
        Flight flight = this.flightsService.getFlight(id);

        if (flight != null) {
            LOGGER.trace("Found flight");
            return new ResponseEntity<>(flight, HttpStatus.OK);
        }

        LOGGER.trace("Flight not found");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Search for flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight(s) retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No flights matching parameters")
    })
    @PostMapping("/search")
    public ResponseEntity<?> searchFlights(@RequestBody FlightsSearchRequest searchRequest) {
        LOGGER.info("Processing flight search request: {}", searchRequest);

        List<Flight> flights = flightsService.searchFlights(searchRequest);

        if (!flights.isEmpty()) {
            LOGGER.trace("Found flights: {}", flights);
            return new ResponseEntity<>(flights, HttpStatus.OK);
        }

        LOGGER.trace("No flights found");
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all flight specials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight specials retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No flight specials")
    })
    @GetMapping("/specials")
    public List<Flight> getFlightSpecials() {
        LOGGER.info("Processing flight specials request");

        List<Flight> discountedFlights = flightsService.getDiscountedFlights();

        LOGGER.trace("Flight specials: {}", discountedFlights);
        return discountedFlights;
    }
}
