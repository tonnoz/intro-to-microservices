package nl.sytac.intro.microservices.streamer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class RegistryPollingService {


    private static final String STANDARD_LOCAL_SERVER_PORT = "8080";
    private static final String SERVER_PORT = "server.port";

    @Value("${registryPort}")
    private String registryPort;

    @Value("${seviceName}")
    private String seviceName;

    @Scheduled( initialDelay = 500, fixedDelay = 10000)
    public void iamAlivePolling(){
        String localServerPort = System.getProperty(SERVER_PORT);
        if(localServerPort == null){
            localServerPort = STANDARD_LOCAL_SERVER_PORT;
        }
        final String url = String.format("http://localhost:%s/%s/%s", registryPort, seviceName, localServerPort);
        final String response; //execute post to registry
        try {
            response = new RestTemplate().postForObject(url, null, String.class);
            log.info(response);
        } catch (ResourceAccessException e) {
            log.error("Registry not available, trying again in 10 sec");
        }
    }
}
