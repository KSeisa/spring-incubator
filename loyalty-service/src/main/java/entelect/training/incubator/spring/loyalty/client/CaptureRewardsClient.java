package entelect.training.incubator.spring.loyalty.client;

import entelect.training.incubator.spring.loyalty.ws.model.CaptureRewardsRequest;
import entelect.training.incubator.spring.loyalty.ws.model.CaptureRewardsResponse;
import entelect.training.incubator.spring.loyalty.ws.model.RewardsBalanceRequest;
import entelect.training.incubator.spring.loyalty.ws.model.RewardsBalanceResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.math.BigDecimal;

public class CaptureRewardsClient extends WebServiceGatewaySupport {
    //WebServiceGatewaySupport - generate SOAP request --> WebServiceTemplate invoke SOAP endpoint --> request message

    public CaptureRewardsResponse captureRewardsResponse(String passportNumber, BigDecimal amount) {
        CaptureRewardsRequest captureRewardsRequest = new CaptureRewardsRequest();
        captureRewardsRequest.setPassportNumber(passportNumber);
        captureRewardsRequest.setAmount(amount);
        return (CaptureRewardsResponse) getWebServiceTemplate().marshalSendAndReceive(captureRewardsRequest);
    }
}
