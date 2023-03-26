package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.model.*;
import entelect.training.incubator.spring.booking.service.BookingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.springframework.web.client.RestTemplate;

import javax.jms.TextMessage;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private final BookingService bookingService;
    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    private final JmsTemplate jmsTemplate;

    @Autowired
    public BookingController(BookingService bookingService, org.springframework.jms.core.JmsTemplate jmsTemplate, org.springframework.jms.core.JmsTemplate jmsTemplate1) {
        this.bookingService = bookingService;
        this.jmsTemplate = jmsTemplate1;
    }

    public void sendMessage(final String queueName, final MessageDTO message) {
        final String textMessage = String.format("Molo Air: Confirming flight {} booked for {} {} on {}.",
                message.getFlightNumber(), message.getFirstName(), message.getLastName(), message.getDate());

        System.out.println(String.format("Sending Message: {} to queue {}", textMessage, queueName));

        jmsTemplate.send(queueName, session -> {
            TextMessage messageSend = session.createTextMessage(textMessage);
            return messageSend;
        });
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking){
        LOGGER.info("Create booking process for booking is starting.", booking);
        RestTemplate restTemplate = new RestTemplate();
        //Create booking reference number
        Random random = new Random();

        String randomReferenceNumber = random.ints(48, 123)
            .filter(num -> (num<58 || num>64) && (num<91 || num>96))
            .limit(15)
            .mapToObj(c -> (char)c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
            .toString();

        booking.setReferenceNumber(randomReferenceNumber.toUpperCase());

        final Booking savedBooking = bookingService.createBooking(booking);

        String restCustomer = "http://localhost:8201/customers/" + booking.getCustomerId().toString();
        String restFlight ="http://localhost:8202/flights/" + booking.getFlightId().toString();

        Customer customer = restTemplate.getForObject(restCustomer, Customer.class);
        Flight flight = restTemplate.getForObject(restFlight, Flight.class);
        flight.setSeatsAvailable(flight.getSeatsAvailable()-1);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setPhoneNumber(customer.getPhoneNumber());
        messageDTO.setFlightNumber(flight.getFlightNumber());
        messageDTO.setFirstName(customer.getFirstName());
        messageDTO.setLastName(customer.getLastName());
        messageDTO.setDate(flight.getDepartureTime().toLocalDate());

        sendMessage("Booking Creation", messageDTO);

        LOGGER.info("Booking with reference number {} created.", savedBooking.getReferenceNumber());
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public  ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        LOGGER.info("Processing booking search request for booking id = {}", id);
        Booking booking = this.bookingService.getBooking(id);

        if (booking != null) {
            LOGGER.trace("Found booking.");
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }

        LOGGER.trace("Booking not found");
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchBookings(@RequestBody BookingSearchRequest bookingSearchRequest) {
        LOGGER.info("Processing booking search request for request {}", bookingSearchRequest);

        List<Booking> bookings = bookingService.searchBookings(bookingSearchRequest);

        if (bookings != null) {
            return ResponseEntity.ok(bookings);
        }

        LOGGER.trace("Booking not found");
        return ResponseEntity.notFound().build();
    }
}
