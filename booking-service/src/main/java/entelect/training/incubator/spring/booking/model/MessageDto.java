package entelect.training.incubator.spring.booking.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto implements Serializable {
    private String phoneNumber;
    private String flightNumber;
    private String firstName;
    private String lastName;
    private LocalDate bookingDate;
}
