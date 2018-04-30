package nl.sytac.intro.microservices.streamer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

    @Scheduled( initialDelay = 500, fixedDelay = 30000)
    public void scheduledPolling(){
        String localServerPort = System.getProperty(SERVER_PORT);
        if(localServerPort == null){
            localServerPort = STANDARD_LOCAL_SERVER_PORT;
        }
        final String url = String.format("http://localhost:%s/%s/%s", registryPort, seviceName, localServerPort);
        final String response = new RestTemplate().postForObject(url, null, String.class); //execute post to registry
        log.info(response);
    }
}
