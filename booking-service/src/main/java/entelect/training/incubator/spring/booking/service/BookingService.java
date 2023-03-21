package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.model.Booking;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import entelect.training.incubator.spring.booking.model.Customer;
import entelect.training.incubator.spring.booking.model.Flight;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking) {
        //Assists in making REST api calls over HTTP
        RestTemplate restTemplate = new RestTemplate();

        String customerUrl = "http://localhost:8201/customers/";
        String flightUrl = "http://localhost:8202/flights/";

        String restCustomer = customerUrl + booking.getCustomer();
        String restFlight = flightUrl + booking.getFlight();

        Customer customer = restTemplate.getForObject(restCustomer, Customer.class);
        Flight flight = restTemplate.getForObject(restFlight, Flight.class);

        if (customer == null || flight == null) {
            return null;
        }

        return bookingRepository.save(booking);
    }
}
