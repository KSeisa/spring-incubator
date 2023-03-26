package entelect.training.incubator.spring.booking.repository;

import entelect.training.incubator.spring.booking.model.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer> {
    List<Booking> searchBookingsByCustomerId(Integer id);

    List<Booking> searchBookingsByReferenceNumber(String referenceNumber);

}
