package nl.sytac.intro.microservices.streamer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController()
@Slf4j
public class TwitterResource {

    @Autowired
    private RedisService redisService;

    @GetMapping(value = "/tweets", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TweetsWrapper giveMeTweets(
                @RequestParam(value = "hashtag") String hashtag,
                HttpServletRequest request
            ) throws InterruptedException, IOException {

        log.info("received tweets request from {}:{}", request.getRemoteAddr(), request.getRemotePort());
        return redisService.getTweets(hashtag);
    }

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String ping(HttpServletRequest request){
        log.info("received ping request from {}:{}", request.getRemoteAddr(), request.getRemotePort());
        return "pong\n";
    }
}
