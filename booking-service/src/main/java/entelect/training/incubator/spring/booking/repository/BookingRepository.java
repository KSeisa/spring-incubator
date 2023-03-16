package entelect.training.incubator.spring.booking.repository;

import entelect.training.incubator.spring.booking.model.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer> {
//    Optional<Booking> makeBooking(Integer customer, Integer flight);
//    Optional<Booking> getBookingById(Integer id);
    List<Booking> searchBookingByCustomer(Integer customerId);
    List<Booking> searchBookingByReferenceNumber(String referenceNumber);
//    Optional<Booking> findByCustomer(Integer customer);
}
