package nl.sytac.intro.microservices.streamer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController()
@Slf4j
public class TwitterResource {

    private final int REGISTRY_PORT = 3000;
    private Integer redisTweetPort;

    @GetMapping(value = "/tweets", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TweetsWrapper giveMeTweets(@RequestParam(value = "hashtag") String hashtag,
                HttpServletRequest request){

        log.info("received tweets request from {}:{}", request.getRemoteAddr(), request.getRemotePort());
        if(redisTweetPort != null) {
            final String url = "http://localhost:" + redisTweetPort + "/tweets?hashtag=" + hashtag;
            return new RestTemplate().getForObject(url,  TweetsWrapper.class);
        }
        return null;
    }

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String ping(HttpServletRequest request){
        log.info("received ping request from {}:{}", request.getRemoteAddr(), request.getRemotePort());
        return "pong\n";
    }

    @Scheduled( initialDelay = 500, fixedDelay = 10000)
    public void askForTweetsRedisLocation(){
        try {
            redisTweetPort = new RestTemplate().getForObject("http://localhost:" + REGISTRY_PORT + "/tweetsRedis/", Integer.class);
        } catch (ResourceAccessException | HttpServerErrorException e) {
            log.error("Registry not available, trying again in 10 sec");
        }
    }
}
