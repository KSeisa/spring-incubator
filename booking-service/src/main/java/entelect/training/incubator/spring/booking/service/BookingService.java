package entelect.training.incubator.spring.booking.service;


import entelect.training.incubator.spring.booking.model.*;
import entelect.training.incubator.spring.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Supplier;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;


    public BookingService(BookingRepository bookingRepository){
        this.bookingRepository=bookingRepository;
    }

    public Booking createBooking(Booking booking) {
        RestTemplate restTemplate=new RestTemplate();
        String customerRest= "http://localhost:8201/customers/" + booking.getCustomer();
        String flightRest="http://localhost:8202/flights/" + booking.getFlight();
        Customer customer= restTemplate.getForObject(customerRest,Customer.class);
        Flight flight=restTemplate.getForObject(flightRest, Flight.class);

        if(customer == null || flight == null) {
            return null;
        }

        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Integer id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        return bookingOptional.orElse(null);
    }

    public List<Booking> getBooking() {
        Iterable<Booking> bookingIterable = bookingRepository.findAll();

        List<Booking> result = new ArrayList<>();
        bookingIterable.forEach(result::add);

        return result;
    }

    public List<entelect.training.incubator.spring.booking.model.Booking> searchBookings(BookingSearchRequest searchRequest) {
        Map<BookingSearchType, Supplier<List<entelect.training.incubator.spring.booking.model.Booking>>> searchStrategies = new HashMap<>();

        searchStrategies.put(BookingSearchType.CUSTOMER_SEARCH, () -> bookingRepository.searchBookingByCustomer(searchRequest.getCustomer()));
        searchStrategies.put(BookingSearchType.REFERENCE_NUMBER_SEARCH, () -> bookingRepository.searchBookingByReferenceNumber(searchRequest.getReferenceNumber()));

        return searchStrategies.get(searchRequest.getSearchType()).get();
    }
}
