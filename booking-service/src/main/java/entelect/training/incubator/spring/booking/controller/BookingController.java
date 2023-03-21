package entelect.training.incubator.spring.booking.controller;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.service.BookingService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;

import java.util.Random;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking){
        //Create booking reference number
        Random random = new Random();

        String randomReferenceNumber = random.ints(48, 123)
            .filter(num -> (num<58 || num>64) && (num<91 || num>96))
            .limit(15)
            .mapToObj(c -> (char)c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
            .toString();

        booking.setReferenceNumber(randomReferenceNumber.toUpperCase());

        LOGGER.info("Create booking process for reference number {} is starting.", booking.getReferenceNumber());

        final Booking savedBooking = bookingService.createBooking(booking);

        LOGGER.trace("Booking created.");
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }


}
