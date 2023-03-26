package entelect.training.incubator.spring.booking.service;

import entelect.training.incubator.spring.booking.model.*;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.*;
import java.util.function.Supplier;


@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking) {
        //Assists in making REST api calls over HTTP
        RestTemplate restTemplate = new RestTemplate();

        String restCustomer = "http://localhost:8201/customers/" + booking.getCustomerId().toString();
        String restFlight ="http://localhost:8202/flights/" + booking.getFlightId().toString();

        Customer customer = restTemplate.getForObject(restCustomer, Customer.class);
        Flight flight = restTemplate.getForObject(restFlight, Flight.class);

        if (customer == null || flight == null) {
            return null;
        }

        return bookingRepository.save(booking);
    }


    public List<Booking> getBookings() {
        Iterable<Booking> bookingIterable = bookingRepository.findAll();

        List<Booking> result = new ArrayList<>();
        bookingIterable.forEach(result::add);

        return result;
    }

    public Booking getBooking(Integer id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        return bookingOptional.orElse(null);
    }

    public List<Booking> searchBookings(BookingSearchRequest bookingSearchRequest) {
        Map<SearchType, Supplier<List<Booking>>> searchStrategies = new HashMap<>();

        searchStrategies.put(SearchType.CUSTOMERID_SEARCH, () -> bookingRepository.searchBookingsByCustomerId(bookingSearchRequest.getCustomerId()));
        searchStrategies.put(SearchType.REFERENCENUMBER_SEARCH, () -> bookingRepository.searchBookingsByReferenceNumber(bookingSearchRequest.getReferenceNumber()));

        return searchStrategies.get(bookingSearchRequest.getSearchType()).get();
    }
}
