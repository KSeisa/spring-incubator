package entelect.training.incubator.spring.loyalty.config;

import entelect.training.incubator.spring.loyalty.client.RewardsBalanceClient;
import entelect.training.incubator.spring.loyalty.ws.model.RewardsBalanceRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class RewardsBalanceSoapClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
       Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
       jaxb2Marshaller.setContextPath(""); //Context Path - same as pom.xml generatePackage
       return jaxb2Marshaller;
    }

    @Bean
    public RewardsBalanceClient rewardsBalanceClient(Jaxb2Marshaller jaxb2Marshaller) {
        RewardsBalanceClient rewardsBalanceClient = new RewardsBalanceClient();
        rewardsBalanceClient.setDefaultUri("");
        rewardsBalanceClient.setMarshaller(jaxb2Marshaller);
        rewardsBalanceClient.setUnmarshaller(jaxb2Marshaller);
        return rewardsBalanceClient;
    }
}
