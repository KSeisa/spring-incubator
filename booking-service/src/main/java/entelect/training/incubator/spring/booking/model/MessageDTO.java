package entelect.training.incubator.spring.booking.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MessageDTO {
    private String flightNumber;
    private String firstName;
    private String lastName;
    private LocalDate date;
    private String phoneNumber;
}
