package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.model.*;
import entelect.training.incubator.spring.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.jms.TextMessage;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("bookings")
@CrossOrigin(origins ={"http://localhost:4200"}, methods={RequestMethod.GET, RequestMethod.POST})
public class BookingController {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final JmsTemplate jmsTemplate;
    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public BookingController(BookingService bookingService, JmsTemplate jmsTemplate){
        this.bookingService = bookingService;
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(final String queueName, final MessageDto message) {
        final String textMessage = "{phoneNumber: " + message.getPhoneNumber() +
                ", message: 'Molo Air: Confirming flight " + message.getFlightNumber() +
                " booked for " + message.getFirstName() +
                " " + message.getLastName() +
                " on " + message.getBookingDate() + "'}";
        System.out.println("Sending message " + textMessage + "to queue - " + queueName);
        jmsTemplate.send(queueName, session -> {
            TextMessage message1 = session.createTextMessage(textMessage);
            return message1;
        });
    }

    private Object getRequest(String url, Integer parameter, Class t) {
        String authStr = "admin:is_a_lie";
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        // create request
        HttpEntity request = new HttpEntity(headers);
        String uri = url + parameter;
        RestTemplate restTemplate =  restTemplateBuilder
                .basicAuthentication("admin", "is_a_lie")
                .build();
        Object o = restTemplate.getForEntity(uri, t)
                .getBody();
        return o;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        LOGGER.info("Processing booking creation request for booking={}", booking);

        Random random = new Random();
        String generatedString = random.ints(97, 122 + 1)
                .limit(4)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        String generatedNumber = String.valueOf(random.nextInt(10000));

        booking.setReferenceNumber(generatedString.toUpperCase() + generatedNumber);

        final Booking savedBooking = bookingService.createBooking(booking);

        Customer customer = (Customer) getRequest("http://localhost:8201/customers/", booking.getCustomer(), Customer.class);
        Flight flight = (Flight) getRequest("http://localhost:8202/flights/", booking.getFlight(), Flight.class);

        MessageDto message = new MessageDto(){};
        message.setFirstName(customer.getFirstName());
        message.setLastName(customer.getLastName());
        message.setPhoneNumber(customer.getPhoneNumber());
        message.setFlightNumber(flight.getFlightNumber());
        message.setBookingDate(LocalDate.now());

        sendMessage("New Booking", message);

        LOGGER.trace("Booking created");
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<?> getBooking() {
        LOGGER.info("Fetching all bookings");
        List<Booking> bookings = this.bookingService.getBooking();

        if (!bookings.isEmpty()) {
            LOGGER.trace("Found bookings");
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }

        LOGGER.trace("No bookings found");
        return ResponseEntity.notFound().build();
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        LOGGER.info("Processing booking search request for booking id={}", id);
        Booking booking = this.bookingService.getBookingById(id);

        if (booking != null) {
            LOGGER.trace("Found booking");
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }

        LOGGER.trace("Booking not found");
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchBookings(@RequestBody BookingSearchRequest searchRequest) {
        LOGGER.info("Processing booking search request: {}", searchRequest);

        List<Booking> bookings = bookingService.searchBookings(searchRequest);

        if (!bookings.isEmpty()) {
            LOGGER.trace("Found bookings: {}", bookings);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }

        LOGGER.trace("No bookings found");
        return ResponseEntity.notFound().build();
    }
}
