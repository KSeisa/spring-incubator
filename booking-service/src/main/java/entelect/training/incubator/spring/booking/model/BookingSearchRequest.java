package entelect.training.incubator.spring.booking.model;

import lombok.Data;

@Data
public class BookingSearchRequest {
    private BookingSearchType searchType;
    private Integer customer;
    private String referenceNumber;
}
