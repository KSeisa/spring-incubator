package entelect.training.incubator.spring.loyalty.client;

import entelect.training.incubator.spring.loyalty.ws.model.RewardsBalanceRequest;
import entelect.training.incubator.spring.loyalty.ws.model.RewardsBalanceResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class RewardsBalanceClient extends WebServiceGatewaySupport {
//WebServiceGatewaySupport - generate SOAP request --> WebServiceTemplate invoke SOAP endpoint --> request message

    public RewardsBalanceResponse getRewardsBalance(String passportNumber) {
        RewardsBalanceRequest rewardsBalanceRequest = new RewardsBalanceRequest();
        rewardsBalanceRequest.setPassportNumber(passportNumber);
        return (RewardsBalanceResponse) getWebServiceTemplate().marshalSendAndReceive(rewardsBalanceRequest);
    }
}
