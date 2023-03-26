package entelect.training.incubator.spring.notification.sms.client.impl;

import com.google.gson.Gson;
import entelect.training.incubator.spring.notification.sms.client.SmsClient;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.w3c.dom.Text;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Map;

/**
 * A custom implementation of a fictional SMS service.
 */
@Service
public class MoloCellSmsClient implements SmsClient {
    
    @Override
    public void sendSms(String phoneNumber, String message) {
        System.out.println(String.format("Sending SMS, destination='{}', '{}'", phoneNumber, message));
    }

    @JmsListener(destination = "inbound.queue")
    @SendTo("outbound.queue")
    public String receiveMessage(final Message jsonMessage) throws JMSException {
        String messageData = null;
        System.out.println("Received Message " + jsonMessage);
        String response = null;

        if (jsonMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) jsonMessage;
            messageData = textMessage.getText();
            Map map = new Gson().fromJson(messageData, Map.class);

            sendSms(map.get("phoneNumber").toString(), map.get("message").toString());
        }
        return response;
    }
}
